package aem.dam.core.models.views.impl;

import aem.dam.core.models.views.View;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AssetsViewImpl extends AbstractViewImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(AssetsViewImpl.class);

    public AssetsViewImpl(Resource requestedResource, Resource configResource, View parentView, ResourceResolver serviceResourceResolver) {
        super(requestedResource, configResource, parentView, serviceResourceResolver);
    }

    @Override
    public List<Resource> getItems() {
        List<Resource> assets = new ArrayList<>();
        String sqlQuery = "SELECT * FROM [dam:Asset] AS asset WHERE ISDESCENDANTNODE(asset ,'/content/dam') AND " + getQueryFilter();
        try {
            QueryManager queryManager = getQueryManager();
            Query query = queryManager.createQuery(sqlQuery, Query.JCR_SQL2);
            QueryResult queryResult = query.execute();
            NodeIterator nodeIterator = queryResult.getNodes();
            while (nodeIterator.hasNext()) {
                Node node = nodeIterator.nextNode();
                assets.add(requestedResource.getResourceResolver().resolve(node.getPath()));
            }
        } catch (RepositoryException ex) {
            LOGGER.error("Unable to getItems query", ex);
        }

        return assets;
    }

    @Override
    protected Map<String, String> calculateViewFolders() {
        return new LinkedHashMap<>();
    }
}
