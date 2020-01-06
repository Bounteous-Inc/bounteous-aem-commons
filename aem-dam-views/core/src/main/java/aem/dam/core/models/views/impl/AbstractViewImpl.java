package aem.dam.core.models.views.impl;

import aem.dam.core.models.views.View;
import com.day.cq.commons.jcr.JcrConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AbstractViewImpl implements View {
    private final List<String> JCR_FOLDER_TYPES = Arrays.asList(JcrConstants.NT_FOLDER, JcrResourceConstants.NT_SLING_FOLDER,
            JcrResourceConstants.NT_SLING_ORDERED_FOLDER);

    protected final Resource requestedResource;
    protected final Resource configResource;
    protected final View parentView;
    protected final ResourceResolver serviceResourceResolver;

    private final Logger logger;

    private String baseQuery;
    private QueryManager serviceQueryManager;
    private QueryManager queryManager;

    AbstractViewImpl(Resource requestedResource, Resource configResource, View parentView, ResourceResolver serviceResourceResolver) {
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.requestedResource = requestedResource;
        this.configResource = configResource;
        this.parentView = parentView;
        this.serviceResourceResolver = serviceResourceResolver;
    }

    protected abstract Map<String, String> calculateViewFolders();

    @Override
    public List<Resource> getItems() {
        Map<String, String> expectedChildViewFolders = calculateViewFolders();

        Set<String> existingChildFolders = new HashSet<>();
        getChildFolders(serviceResourceResolver.resolve(requestedResource.getPath()))
                .forEach(existingChildResource -> existingChildFolders.add(existingChildResource.getName()));

        // Add any folders that are expected but don't yet exist
        for (Map.Entry<String, String> entry : expectedChildViewFolders.entrySet()) {
            if (!existingChildFolders.contains(entry.getKey())) {
                createFolder(entry.getKey(), entry.getValue());
            }
        }

        // Fetch child folders using the request resource resolver in order to apply appropriate permissions
        return getChildFolders(requestedResource);
    }

    private QueryManager getServiceQueryManager() {
        if (serviceQueryManager == null) {
            try {
                Session serviceSession = serviceResourceResolver.adaptTo(Session.class);
                serviceQueryManager = serviceSession.getWorkspace().getQueryManager();
            } catch (Exception e) {
                throw new RuntimeException("Unexpected error getting service QueryManager", e);
            }
        }
        return serviceQueryManager;
    }

    protected QueryManager getQueryManager() {
        if (queryManager == null) {
            try {
                Session session = requestedResource.getResourceResolver().adaptTo(Session.class);
                queryManager = session.getWorkspace().getQueryManager();
            } catch (Exception e) {
                throw new RuntimeException("Unexpected error getting QueryManager", e);
            }
        }
        return queryManager;
    }

    protected String getBaseQuery() {
        if (baseQuery == null) {
            baseQuery = "SELECT * FROM [dam:Asset] AS asset WHERE ISDESCENDANTNODE(asset ,'/content/dam')";
            if (parentView != null) {
                baseQuery += " AND " + parentView.getQueryFilter();
            }
        }
        return baseQuery;
    }

    public String getQueryFilter() {
        String filter = getQueryFilterForThisLevelOnly();
        if (StringUtils.isNotBlank(filter)) {
            filter = "(" + filter + ")";
        }
        if (parentView != null) {
            String parentFilter = parentView.getQueryFilter();
            if (StringUtils.isNotBlank(parentFilter)) {
                parentFilter = "(" + parentFilter + ")";
                if (StringUtils.isNotBlank(filter)) {
                    filter += " AND " + parentFilter;
                } else {
                    filter = parentFilter;
                }
            }
        }
        return filter;
    }

    protected boolean hasAtleastOneAsset(String queryFilter) {
        boolean hasAsset = false;

        String queryStr = getBaseQuery() + " AND " + queryFilter;
        logger.debug("Query for at least one asset to create folder: {}", queryStr);
        try {
            Query query = getServiceQueryManager().createQuery(queryStr, Query.JCR_SQL2);
            query.setLimit(1);
            QueryResult queryResult = query.execute();
            if (queryResult.getNodes().hasNext()) {
                hasAsset = true;
            }
        } catch (RepositoryException ex) {
            logger.error("Failed query to determine whether to create folder: {}", queryStr, ex);
        }

        return hasAsset;
    }


    protected String getQueryFilterForThisLevelOnly() {
        return "";
    }

    private void createFolder(String name, String title) {
        try {
            Map<String, Object> properties = new HashMap<>();
            properties.put(JcrConstants.JCR_PRIMARYTYPE, JcrResourceConstants.NT_SLING_FOLDER);
            Resource folderResource = serviceResourceResolver.create(requestedResource, name, properties);

            properties.put(JcrConstants.JCR_TITLE, title);
            properties.put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_UNSTRUCTURED);
            serviceResourceResolver.create(folderResource, JcrConstants.JCR_CONTENT, properties);

            serviceResourceResolver.commit();
        } catch (PersistenceException e) {
            logger.error("Failed to create the folder.", e);
        }
    }

    private List<Resource> getChildFolders(Resource parentResource) {
        List<Resource> childResources = new ArrayList<>();
        parentResource.getChildren().forEach(childResource -> {
            if (JCR_FOLDER_TYPES.contains(childResource.getResourceType())) {
                childResources.add(childResource);
            }
        });
        return childResources;
    }
}
