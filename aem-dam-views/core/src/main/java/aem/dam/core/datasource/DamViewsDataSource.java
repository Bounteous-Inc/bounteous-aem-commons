package aem.dam.core.datasource;

import aem.dam.core.models.views.ViewService;
import com.adobe.granite.ui.components.ds.DataSource;
import com.day.cq.search.QueryBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

public class DamViewsDataSource implements DataSource {

    private static final Logger LOGGER = LoggerFactory.getLogger(DamViewsDataSource.class);

    private static final String DAM_CONFIG_VIEW_NODENAME = "damconfigviews";
    private static final String DAM_VIEWS_ROOT = "/content/dam-views";

    private final Resource requestedResource;
    private final QueryBuilder queryBuilder;
    private final BaseParameters baseParameters;
    private final ViewService viewService;

    public DamViewsDataSource(QueryBuilder queryBuilder, BaseParameters baseParameters, Resource folder, ViewService viewService) {
        this.queryBuilder = queryBuilder;
        this.baseParameters = baseParameters;
        this.requestedResource = folder;
        this.viewService = viewService;
    }

    @Override
    public Iterator<Resource> iterator() {
        List<Resource> assetsList = new ArrayList<>();
        String path = requestedResource.getPath();

        if (path.startsWith(DAM_VIEWS_ROOT) && path.length() > DAM_VIEWS_ROOT.length()) {
            ResourceResolver resourceResolver = this.requestedResource.getResourceResolver();

            String viewFullRelativePath = StringUtils.substringAfter(path, DAM_VIEWS_ROOT + "/");
            String[] viewPathTokens = viewFullRelativePath.split("/");

            String viewBaseAbsolutePath = DAM_VIEWS_ROOT + "/" + viewPathTokens[0];
            String viewConfigsParentPath = viewBaseAbsolutePath + "/" + JcrConstants.JCR_CONTENT + "/" + DAM_CONFIG_VIEW_NODENAME;
            Resource viewConfigsParent = resourceResolver.getResource(viewConfigsParentPath);

            LinkedHashMap<String, Resource> viewConfigs = new LinkedHashMap<>();
            if (viewConfigsParent != null) {
                int viewPathTokenIndex = 1;
                Iterator<Resource> viewConfigsIterator = viewConfigsParent.getChildren().iterator();
                while (viewConfigsIterator.hasNext() && viewConfigs.size() < viewPathTokens.length) {
                    // Ensure that duplicate keys do not conflict in the map
                    String key = StringUtils.repeat('/', viewPathTokenIndex);
                    if (viewPathTokenIndex < viewPathTokens.length) {
                        key += viewPathTokens[viewPathTokenIndex++];
                    }
                    viewConfigs.put(key, viewConfigsIterator.next());
                }

                try {
                    assetsList.addAll(viewService.getItems(requestedResource, viewConfigs));
                } catch (Exception e) {
                    LOGGER.error("Failed to calculate asset view for path {}", requestedResource.getPath(), e);
                }
            } else {
                LOGGER.warn("No configs found for view: {}", this.requestedResource.getPath());
            }
        } else if (path.equalsIgnoreCase(DAM_VIEWS_ROOT)) {
            // TODO: If not using cq:Page for children of DAM_VIEWS_ROOT, remove this
            requestedResource.getChildren().forEach(childResource -> {
                if (childResource.getResourceType().equals("cq:Page")) {
                    assetsList.add(childResource);
                }
            });
        }
        return assetsList.iterator();
    }
}
