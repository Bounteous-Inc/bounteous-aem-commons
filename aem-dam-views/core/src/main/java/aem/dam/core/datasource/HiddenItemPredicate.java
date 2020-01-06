package aem.dam.core.datasource;

import org.apache.commons.collections.Predicate;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

public class HiddenItemPredicate implements Predicate {
    private static final Logger LOGGER = LoggerFactory.getLogger(HiddenItemPredicate.class);

    @Override
    public boolean evaluate(Object object) {
        Resource resource = (Resource) object;

        Node node = resource.adaptTo(Node.class);
        if (node == null) {
            return false;
        }
        try {
            if ((node.hasProperty("hidden")) &&
                    ("true".equals(node.getProperty("hidden").getString()))) {
                return false;
            }
        } catch (RepositoryException e) {
            LOGGER.error("Could not read property 'hidden' on node.", e);
        }
        return true;
    }
}
