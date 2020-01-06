package aem.dam.core.datasource;

import org.apache.commons.collections.Predicate;

import java.util.regex.Pattern;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AssetFolderItemPredicate implements Predicate {
    private static final Logger LOGGER = LoggerFactory.getLogger(AssetFolderItemPredicate.class);

    private static final String COLLECTIONS_ROOT = "collections";
    private static final int MAX_COLLECTION_DEPTH = 4;
    private boolean includeCollections;
    private String exclude;
    private String include;

    public AssetFolderItemPredicate(boolean includeCollections, String exclude, String include) {
        this.includeCollections = includeCollections;
        this.exclude = exclude;
        this.include = include;
    }

    public void setIncludeCollections(boolean includeCollections) {
        this.includeCollections = includeCollections;
    }

    public void setExclude(String exclude) {
        this.exclude = exclude;
    }

    public void setInclude(String include) {
        this.include = include;
    }

    public boolean evaluate(Object object) {
        Resource resource = (Resource) object;

        Node node = resource.adaptTo(Node.class);
        if (node == null) {
            return false;
        }
        try {
            if (node.isNodeType("dam:Asset")) {
                return true;
            }
            if (node.isNodeType("nt:folder") || node.isNodeType("sling:Folder") || node.isNodeType("cq:Page")) {
                if (StringUtils.isNotBlank(this.exclude)) {
                    Pattern pattern = Pattern.compile(this.exclude);
                    if (pattern.matcher(node.getPath()).matches()) {
                        return false;
                    }
                }
                if (StringUtils.isNotBlank(this.include)) {
                    Pattern pattern = Pattern.compile(this.include);
                    return (!pattern.matcher(node.getPath()).matches());
                }
                return true;
            }
        } catch (RepositoryException e) {
            LOGGER.error("Could not determine node type.", e);
        }
        return this.includeCollections &&
                (isCollection(resource) || containsCollection(resource));
    }

    private boolean isCollection(Resource res) {
        return (res.isResourceType("dam/collection")) ||
                (res.isResourceType("dam/smartcollection"));
    }

    private boolean containsCollection(Resource resource) {
        return (includesPathElement(resource.getPath(), COLLECTIONS_ROOT)) && (containsCollection(resource, 0));
    }

    private boolean containsCollection(Resource resource, int depth) {
        if (depth >= MAX_COLLECTION_DEPTH) {
            return false;
        }

        for (Resource kid : resource.getChildren()) {
            if (isCollection(kid)) {
                return true;
            }
            if (containsCollection(kid, depth + 1)) {
                return true;
            }
        }
        return false;
    }

    private static boolean includesPathElement(String path, String name) {
        return (path + "/").contains("/" + name + "/");
    }
}

