package aem.dam.core.datasource;

import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_EXTENSIONS;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_METHODS;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_RESOURCE_TYPES;

import aem.dam.core.models.views.ViewService;
import com.adobe.granite.ui.components.Config;
import com.adobe.granite.ui.components.ExpressionResolver;
import com.adobe.granite.ui.components.ds.DataSource;
import com.day.cq.dam.commons.sort.ResourceSorter;
import com.day.cq.dam.commons.util.UIHelper;
import com.day.cq.search.QueryBuilder;

import java.util.Iterator;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.servlet.Servlet;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.iterators.FilterIterator;
import org.apache.commons.collections.iterators.IteratorChain;
import org.apache.commons.collections.iterators.TransformIterator;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ResourceWrapper;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
        service = Servlet.class,
        immediate = true,
        property = {
                SLING_SERVLET_RESOURCE_TYPES + "=" + "dam/gui/coral/components/commons/ui/shell/datasources/damviewdatasourceservletdatasource",
                SLING_SERVLET_METHODS + "=" + "GET",
                SLING_SERVLET_EXTENSIONS + "=" + "html"
        }
)
public class DamViewDataSourceServlet extends SlingSafeMethodsServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(DamViewDataSourceServlet.class);

    @Reference
    private transient ExpressionResolver expressionResolver;

    @Reference
    private transient QueryBuilder queryBuilder;

    @Reference
    private transient ResourceSorter resourceSorter;

    @Reference
    private transient ViewService viewService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        LOGGER.debug("Calling DamViewDataSourceServlet...");
        Resource component = request.getResource();
        ResourceResolver resolver = request.getResourceResolver();
        Resource datasourceComponent = component.getChild(Config.DATASOURCE);
        ExValueMap ds = this.getExValueMap(request, datasourceComponent);
        BaseParameters baseParams = this.getBaseConfig(ds);
        Resource folder = this.getFolder(request, baseParams.includeCollections);
        String[] paths = ds.getEx("paths", String[].class);

        if (paths == null) {
            paths = ds.getEx("resourceFilters", String[].class);
        }

        Iterator<?> linkedAssets = this.getLinkedAssets(request, baseParams);
        DataSource dataSource;

        if (this.fetchByQuery(request, folder, ds)) {
            QueryParameters queryParams = this.getQueryConfig(request, ds);

            if (baseParams.requiredProperties != null) {
                LOGGER.debug("Rendering Assets based on the query of required properties.");
                dataSource = new AssetQueryRequiredPropertiesDataSource(this.queryBuilder, this.resourceSorter, folder, baseParams, queryParams);
            } else if (folder.getPath().startsWith("/content/dam-views")) {
                LOGGER.debug("Rendering DAM Views.");
                dataSource = new DamViewsDataSource(this.queryBuilder, baseParams, folder, viewService);
            } else {
                // TODO: This is never reached. Delete this block and AssetsQueryDataSource.java.
                LOGGER.debug("Rendering Assets based on query.");
                dataSource = new AssetsQueryDataSource(this.queryBuilder, this.resourceSorter, folder, baseParams, queryParams);
            }
        } else {
            if (paths != null) {
                LOGGER.debug("paths is null in doGet");
                dataSource = new FixedPathsDataSource(resolver, paths);
            } else {
                if (folder == null) {
                    LOGGER.debug("abort, no '../path' or 'paths' set: {}", datasourceComponent.getPath());
                    return;
                }

                LOGGER.debug("Rendering child resources in doGet");
                dataSource = new FolderListingDataSource(folder);
            }

            dataSource = new AssetsDataSource(dataSource, baseParams);
        }

        DataSource dataSource1 = this.prependLinkedAssets(dataSource, linkedAssets, baseParams.includeCollections, baseParams.exclude, baseParams.include);
        dataSource = this.overwriteCustomResourceTypes(dataSource1, ds, resolver);
        request.setAttribute(DataSource.class.getName(), dataSource);
        this.setRequestAttributes(request, datasourceComponent, baseParams);
    }

    private boolean fetchByQuery(SlingHttpServletRequest request, Resource folder, ValueMap ds) {
        if (folder == null) {
            LOGGER.debug("folder is null in fetchByQuery");
            return false;
        } else if (request.getParameter("requiredproperty") != null) {
            LOGGER.debug("requiredproperty is present in the request in fetchByQuery");
            return true;
        } else if (request.getParameter("sortName") != null) {
            LOGGER.debug("sortName is present in the request in fetchByQuery");
            return true;
        } else {
            boolean inverseOrder = ds.get("inverseOrder", false);
            if (this.isOrderedFolder(folder)) {
                inverseOrder = false;
            }
            return ds.get("fetchByQuery", inverseOrder);
        }
    }

    private BaseParameters getBaseConfig(ExValueMap ds) {
        BaseParameters baseParams = new BaseParameters();
        baseParams.includeCollections = ds.getEx("includeCollections", "false", Boolean.class);
        baseParams.offset = ds.getEx("offset", "0", Integer.class);

        if (baseParams.offset < 0) {
            baseParams.offset = 0;
        }

        baseParams.limit = ds.getEx("limit", "10", Integer.class);
        if (baseParams.limit <= 0) {
            baseParams.limit = 2147483647;
        }

        baseParams.solution = ds.getEx("solution", String.class);
        String[] dsFilters = ds.get("filters", String[].class);
        String[] elFilters = ds.getEx("filterEL", String[].class);
        baseParams.mimeTypes = ((String[]) ArrayUtils.addAll(dsFilters, elFilters));
        String[] dsPropFilters = ds.get("propertyFilters", String[].class);
        String[] elReqProps = ds.getEx("requiredProperties", String[].class);
        baseParams.requiredProperties = ((String[]) ArrayUtils.addAll(dsPropFilters, elReqProps));
        baseParams.exclude = ds.get("exclude", String.class);
        baseParams.include = ds.get("include", String.class);
        return baseParams;
    }

    private QueryParameters getQueryConfig(SlingHttpServletRequest request, ExValueMap ds) {
        QueryParameters queryParams = new QueryParameters();
        if (ds.get("requestMap", false)) {
            queryParams.map = request.getParameterMap();
        }

        queryParams.directChildrenOnly = ds.get("fetchDirectChildrenOnly", true);
        queryParams.propertyName = ds.getEx("profiletype", "", String.class);
        queryParams.propertyValue = ds.getEx("profilepath", "", String.class);
        queryParams.type = request.getParameter("type");
        queryParams.orderBy = request.getParameter("sortName");
        queryParams.sort = request.getParameter("sortDir");
        queryParams.useTypeAsyncIndex = ds.get("useTypeAsyncIndex", false);
        return queryParams;
    }

    private Resource getFolder(SlingHttpServletRequest request, boolean includeCollections) {
        ExValueMap map = this.getExValueMap(request, request.getResource());
        String contentPath = map.getEx("path", String.class);
        contentPath = StringUtils.substringBefore(contentPath, "?");
        Resource contentRsc = request.getResourceResolver().getResource(contentPath);
        if (contentRsc == null) {
            LOGGER.debug("Content Resource is null @ {}", contentPath);
            contentRsc = UIHelper.getCurrentSuffixResource(request);
        }

        if (contentRsc == null) {
            return null;
        } else {
            try {
                Node node = contentRsc.adaptTo(Node.class);
                if (node.isNodeType("sling:Folder") || node.isNodeType("cq:Page")) {
                    return contentRsc;
                }

                if (node.isNodeType("dam:Asset") && node.hasNode("subassets")) {
                    return contentRsc;
                }

                if (includeCollections) {
                    return contentRsc;
                }
            } catch (RepositoryException var7) {
                LOGGER.error("Could not get datasource for " + contentRsc.getPath(), var7);
            }

            return null;
        }
    }

    private boolean isOrderedFolder(Resource resource) {
        Node resNode = resource != null ? resource.adaptTo(Node.class) : null;

        try {
            return resNode != null && (resNode.isNodeType("sling:OrderedFolder"));
        } catch (RepositoryException var3) {
            LOGGER.error("Could not inspect node type for " + resource.getPath(), var3);
        }

        return false;
    }

    private Iterator<?> getLinkedAssets(SlingHttpServletRequest request, BaseParameters params) {
        Iterator<?> linkedAssets = (Iterator) request.getAttribute("com.adobe.cq.assets.linked");
        if (linkedAssets != null) {
            while (linkedAssets.hasNext() && params.offset > 0) {
                --params.offset;
                linkedAssets.next();
            }
        }
        return linkedAssets;
    }

    private DataSource prependLinkedAssets(final DataSource inDataSource, final Iterator<?> linkedAssets, final boolean includeCollections, final String exclude, final String include) {
        return linkedAssets == null ? inDataSource : new DataSource() {
            public Iterator<Resource> iterator() {
                Iterator filteredLinkedAssets = new FilterIterator(linkedAssets, new AssetFolderItemPredicate(includeCollections, exclude, include));
                return new IteratorChain(filteredLinkedAssets, inDataSource.iterator());
            }
        };
    }

    private DataSource overwriteCustomResourceTypes(final DataSource inDataSource, ValueMap ds, ResourceResolver resolver) {
        final String fixedType = ds.get("itemResourceType", String.class);
        final String assetType = ds.get("asset/itemResourceType", String.class);
        final String folderType = ds.get("directory/itemResourceType", String.class);
        final String contentFragmentType = ds.get("contentfragment/itemResourceType", String.class);
        final boolean hasFragmentEditor = resolver.getResource("/libs/dam/cfm/admin/content/fragment-editor") != null;

        return new DataSource() {
            public Iterator<Resource> iterator() {
                return new TransformIterator(inDataSource.iterator(), new Transformer() {
                    public Object transform(Object input) {
                        Resource resource = (Resource) input;
                        final String customRT = this.getCustomResourceType(resource);
                        return new ResourceWrapper(resource) {
                            public String getResourceType() {
                                return customRT != null ? customRT : super.getResourceType();
                            }
                        };
                    }

                    private String getCustomResourceType(Resource resource) {
                        if (fixedType != null) {
                            return fixedType;
                        } else {
                            String resourceType = resource.getResourceType();
                            try {
                                //Node node = (Node)resource.adaptTo(Node.class);
                                if (resourceType.equalsIgnoreCase("dam:Asset")) {
                                    if (hasFragmentEditor && resource.getValueMap().get("jcr:content/contentFragment", false)) {
                                        resourceType = contentFragmentType;
                                    } else {
                                        resourceType = assetType;
                                    }
                                } else if (resourceType.equalsIgnoreCase("cq:Page") || resourceType.equalsIgnoreCase("sling:Folder")) {
                                    // todo: if resource.getPath() starts with /content/dam-views, we need to give it a different folderType for rendering
                                    resourceType = folderType;
                                }
                            } catch (Throwable var5) {
                                DamViewDataSourceServlet.LOGGER.error("Could not provide custom resource type.", var5);
                            }

                            return resourceType;
                        }
                    }
                });
            }
        };
    }

    private void setRequestAttributes(SlingHttpServletRequest request, Resource datasourceRsc, BaseParameters baseParams) {
        ValueMap ds = ResourceUtil.getValueMap(datasourceRsc);
        request.setAttribute("showInsight", ds.get("showInsight", String.class));
        request.setAttribute("com.adobe.cq.assets.contentrenderer.showQuickActions", ds.get("showQuickActions", true));
        request.setAttribute("com.adobe.cq.assets.contentrenderer.allowNavigation", ds.get("allowNavigation", true));
        request.setAttribute("filters", baseParams.mimeTypes);
        request.setAttribute("propertyFilters", baseParams.requiredProperties);
        Resource assetRes = datasourceRsc != null ? datasourceRsc.getChild("asset") : null;
        ValueMap assetVm = ResourceUtil.getValueMap(assetRes);
        request.setAttribute("showOriginalIfNoRenditionAvailable", assetVm.get("showOriginalIfNoRenditionAvailable", false));
        request.setAttribute("showOriginalForGifImages", assetVm.get("showOriginalForGifImages", false));
        request.setAttribute("disableActions", assetVm.get("disableActions", false));
    }

    private ExValueMap getExValueMap(SlingHttpServletRequest request, Resource resource) {
        return new ExValueMap(request, this.expressionResolver, ResourceUtil.getValueMap(resource));
    }
}