package com.hs2solutions.aem.ondeployexamples.core.scripts;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.hs2solutions.aem.ondeploy.core.base.OnDeployScriptBase;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Delete all pages using the 'geometrixx/components/widepage' resource type.
 */
public class OnDeployExampleScriptDeleteObsoleteGeometrixxData extends OnDeployScriptBase {

    @Override
    public void execute() throws RepositoryException {
        List<Node> nodesToDelete = getNodesToDelete();
        logger.info("Found {} nodes to delete", nodesToDelete.size());
        for (Node nodeToDelete : nodesToDelete) {
            removeResource(nodeToDelete.getParent().getPath());
        }
    }

    /**
     * Query the nodes to delete.
     */
    private List<Node> getNodesToDelete() {
        Map<String, String> map = new HashMap<>();
        map.put("p.limit", "-1");
        map.put("path", "/content");
        map.put("1_property", "sling:resourceType");
        map.put("1_property.value", "geometrixx/components/widepage");

        Query query = queryBuilder.createQuery(PredicateGroup.create(map), session);
        List<Node> nodesToDelete = new ArrayList<>();
        Iterator<Node> nodeIterator = query.getResult().getNodes();
        while (nodeIterator.hasNext()) {
            nodesToDelete.add(nodeIterator.next());
        }
        return nodesToDelete;
    }
}
