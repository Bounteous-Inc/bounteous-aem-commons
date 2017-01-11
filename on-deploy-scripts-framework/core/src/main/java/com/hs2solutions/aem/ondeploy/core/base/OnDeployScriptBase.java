package com.hs2solutions.aem.ondeploy.core.base;

import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.hs2solutions.aem.ondeploy.core.models.DispatcherFlushReplicationConfig;
import com.hs2solutions.aem.ondeploy.core.models.PublishReplicationConfig;
import com.hs2solutions.aem.ondeploy.core.utils.ServiceUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.oak.spi.security.principal.PrincipalImpl;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.jcr.base.util.AccessControlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Base on-deploy script implementation.
 */
public abstract class OnDeployScriptBase implements OnDeployScript {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected PageManager pageManager;
    protected QueryBuilder queryBuilder;
    protected ResourceResolver resourceResolver;
    protected Session session;
    protected Workspace workspace;

    /**
     * Add a user or another group as a member of a specified group.
     *
     * Function will log an error and do nothing if either the specified group
     * or group member cannot be found, or if the specified group is not actually
     * a group (i.e. a user).
     *
     * @param groupName The name of the group to add the member to.
     * @param memberName The name of the user or group to add as a member.
     * @throws RepositoryException
     */
    protected void addUserGroupMember(String groupName, String memberName) throws RepositoryException {
        UserManager userManager = AccessControlUtil.getUserManager(session);
        Authorizable group = userManager.getAuthorizable(groupName);
        Authorizable member = userManager.getAuthorizable(memberName);

        if (group == null) {
            logger.error("Cannot add member '{}' to group '{}' because '{}' does not exist", memberName, groupName, groupName);
        } else if (!group.isGroup()) {
            logger.error("Cannot add member '{}' to group '{}' because '{}' is not a group", memberName, groupName, groupName);
        } else if (member == null) {
            logger.error("Cannot add member '{}' to group '{}' because '{}' does not exist", memberName, groupName, memberName);
        } else {
            logger.info("Adding member '{}' to group '{}'", memberName, groupName);
            ((Group) group).addMember(member);
        }
    }

    /**
     * Configure the dispatcher flush agent.
     *
     * Generally this would be done only on a publish server.
     *
     * @param configs A list of potential configs.  The first config that has run modes that are
     *                all satisfied by the current server will be applied.  This allows a script
     *                to specify configs for all of the servers in an AEM ecosystem, applying the
     *                correct configs to each server.
     * @throws RepositoryException
     */
    protected void configureReplicationDispatcherFlushAgent(List<DispatcherFlushReplicationConfig> configs) throws RepositoryException {
        boolean configured = false;

        Set<String> serverRunModes = ServiceUtil.getRunModes();
        for (DispatcherFlushReplicationConfig config : configs) {
            if (!configured && serverRunModes.containsAll(config.getRunModes())) {
                logger.info("Configuring dispatcher flush agent for run modes '{}'", StringUtils.join(config.getRunModes().toArray(), ","));

                String replicationAgentPath = "/etc/replication/agents." +
                        (ServiceUtil.runModeIsAuthor() ? "author" : "publish") +
                        "/flush/jcr:content";
                Node replicationAgent = session.getNode(replicationAgentPath);

                replicationAgent.setProperty("transportUri", config.getDispatcherHost() + "/dispatcher/invalidate.cache");
                replicationAgent.setProperty("enabled", config.isEnabled());

                configured = true;
            }
        }

        if (!configured) {
            logger.info("No matching configs found for configuring dispatcher flush agent on this server");
        }
    }

    /**
     * Configure the publish replication agent.
     *
     * Generally this would be done only on an author server.
     *
     * @param configs A list of potential configs.  The first config that has run modes that are
     *                all satisfied by the current server will be applied.  This allows a script
     *                to specify configs for all of the servers in an AEM ecosystem, applying the
     *                correct configs to each server.
     * @throws RepositoryException
     */
    protected void configureReplicationPublishAgent(List<PublishReplicationConfig> configs) throws RepositoryException {
        boolean configured = false;

        Set<String> serverRunModes = ServiceUtil.getRunModes();
        for (PublishReplicationConfig config : configs) {
            if (!configured && serverRunModes.containsAll(config.getRunModes())) {
                logger.info("Configuring publish replication agent for run modes '{}'", StringUtils.join(config.getRunModes().toArray(), ","));

                String replicationAgentPath = "/etc/replication/agents.author/publish/jcr:content";
                Node replicationAgent = session.getNode(replicationAgentPath);

                replicationAgent.setProperty("transportUri", config.getPublishHost() + "/bin/receive?sling:authRequestLogin=1");
                replicationAgent.setProperty("enabled", config.isEnabled());

                configured = true;
            }
        }

        if (!configured) {
            logger.info("No matching configs found for configuring publish replication agent on this server");
        }
    }

    /**
     * Static method to create a new blueprint.
     *
     * @param name The name for the new blueprint.
     * @param title The title/jcr:title.
     * @param sitePath Path of the current site's location
     */
    protected void createBlueprint(String name, String title, String sitePath) throws RepositoryException, WCMException {
        logger.info("Creating blueprint '{}' ({}) for {}", title, name, sitePath);

        Page blueprintPage = pageManager.create("/etc/blueprints", name, "", title);
        Node jcrContentNode = blueprintPage.getContentResource().adaptTo(Node.class);

        jcrContentNode.setProperty("cq:template", "/libs/wcm/msm/templates/blueprint");
        jcrContentNode.setProperty("sitePath", sitePath);
        jcrContentNode.setProperty("sling:resourceType", "wcm/msm/components/blueprint");
        jcrContentNode.setProperty("thumbnailRotate", "0");

        Node dialogNode = jcrContentNode.addNode("dialog", "cq:Dialog");
        dialogNode.setProperty("title", title + " Blueprint");

        Node items1Node = dialogNode.addNode("items","cq:WidgetCollection");

        Node tabsNode = items1Node.addNode("tabs", "cq:TabPanel");

        Node items2Node = tabsNode.addNode("items", "cq:WidgetCollection");

        Node tabLanNode = items2Node.addNode("tab_lan", "cq:Widget");
        tabLanNode.setProperty("path", "/libs/wcm/msm/templates/blueprint/defaults/language_tab.infinity.json");
        tabLanNode.setProperty("xtype", "cqinclude");

        Node tabChapNode = items2Node.addNode("tab_chap", "cq:Widget");
        tabChapNode.setProperty("path", "/libs/wcm/msm/templates/blueprint/defaults/chapters_tab.infinity.json");
        tabChapNode.setProperty("xtype", "cqinclude");

        Node tabLcNode = items2Node.addNode("tab_lc", "cq:Widget");
        tabLcNode.setProperty("path", "/libs/wcm/msm/templates/blueprint/defaults/livecopy_tab.infinity.json");
        tabLcNode.setProperty("xtype", "cqinclude");
    }

    /**
     * Create a new user group.
     *
     * If the group already exists, the function will return null so that a calling function
     * can determine whether or not the group was actually created (vs. preexisting) by the
     * call to this function.
     *
     * @param groupName The name of the group to create.
     * @param intermediatePath The intermediate path in the JCR under which to save the group.
     *                         This can be used to organize multiple groups into a single folder.
     * @return Group if created, else null.
     * @throws RepositoryException
     */
    protected Group createUserGroup(String groupName, String intermediatePath) throws RepositoryException {
        UserManager userManager = AccessControlUtil.getUserManager(session);
        Group existingGroup = (Group) userManager.getAuthorizable(groupName);
        if (existingGroup == null) {
            logger.info("Creating user group '{}'", groupName);
            return userManager.createGroup(new PrincipalImpl(groupName), intermediatePath);
        } else {
            logger.info("'{}' group already exists", groupName);
            return null;
        }
    }

    /**
     * @see OnDeployScript#execute(ResourceResolver, QueryBuilder)
     */
    public final void execute(ResourceResolver resourceResolver, QueryBuilder queryBuilder) throws Exception {
        this.resourceResolver = resourceResolver;
        this.queryBuilder = queryBuilder;

        this.pageManager = resourceResolver.adaptTo(PageManager.class);
        this.session = resourceResolver.adaptTo(Session.class);
        this.workspace = session.getWorkspace();

        execute();

        session.save();
    }

    /**
     * Execute the script.
     *
     * This function must be implemented by all scripts.
     */
    protected abstract void execute() throws Exception;

    /**
     * Retrieve a node, or create it if not present.
     *
     * The node, as well as non-existent parent nodes, are created as type
     * nt:unstructured.
     *
     * @param absolutePath Path to fetch or create.
     * @return The fetched or created node.
     */
    protected Node getOrCreateNode(String absolutePath) throws RepositoryException {
        return getOrCreateNode(absolutePath, "nt:unstructured", "nt:unstructured");
    }

    /**
     * Retrieve a node, or create it if not present.
     *
     * If the node does not exist, it is created as the specified nodeType.
     *
     * Non-existent parent nodes are created as type nt:unstructured.
     *
     * @param absolutePath Path to fetch or create.
     * @param nodeType The type of node to create.
     * @return The fetched or created node.
     */
    protected Node getOrCreateNode(String absolutePath, String nodeType) throws RepositoryException {
        return getOrCreateNode(absolutePath, "nt:unstructured", nodeType);
    }

    /**
     * Retrieve a node, or create it if not present.
     *
     * If the node does not exist, it is created as the specified nodeType.
     *
     * Non-existent parent nodes are created as the type specified by
     * intermediateNodeType.
     *
     * @param absolutePath Path to fetch or create.
     * @param intermediateNodeType The type of intermediate nodes to create.
     * @param nodeType The type of node to create.
     * @return The fetched or created node.
     */
    protected Node getOrCreateNode(String absolutePath, String intermediateNodeType, String nodeType) throws RepositoryException {
        try {
            return session.getNode(absolutePath);
        } catch (PathNotFoundException e) {
            logger.info("Creating node (and missing parents) at {}", absolutePath);
            return JcrUtil.createPath(absolutePath, intermediateNodeType, nodeType, session, false);
        }
    }

    /**
     * Remove node at a given path.
     *
     * @param path Path to the node to remove.
     */
    protected void removeResource(String path) throws RepositoryException {
        Resource resource = resourceResolver.getResource(path);
        if (resource != null) {
            logger.info("Deleting node at {}", path);
            session.removeItem(path);
        } else {
            logger.info("Node at {} has already been removed", path);
        }
    }

    /**
     * Searches for the current sling:resourceType under /content and replaces any nodes it finds
     * with the newResourceType.
     *
     * @param oldResourceType The current sling:resourceType.
     * @param newResourceType The new sling:resourceType to be used.
     */
    protected void searchAndUpdateResourceType(String oldResourceType, String newResourceType) throws RepositoryException {
        Map<String, String> map = new HashMap<>();
        map.put("p.limit", "-1");
        map.put("path", "/content");
        map.put("1_property", "sling:resourceType");
        map.put("1_property.value", oldResourceType);

        Query query = queryBuilder.createQuery(PredicateGroup.create(map), session);
        SearchResult result = query.getResult();
        Iterator<Node> nodeItr = result.getNodes();
        while (nodeItr.hasNext()) {
            Node node = nodeItr.next();
            updateResourceType(node, newResourceType);
        }
    }

    /**
     * Update the sling:resourceType of a node.
     *
     * @param node         The node to update.
     * @param resourceType The new sling:resourceType to be used.
     */
    protected void updateResourceType(Node node, String resourceType) throws RepositoryException {
        String currentResourceType = node.getProperty("sling:resourceType").getString();
        if (!resourceType.equals(currentResourceType)) {
            logger.info("Updating node at {} to resource type '{}'", node.getPath(), resourceType);
            node.setProperty("sling:resourceType", resourceType);
        } else {
            logger.info("Node at {} is already resource type '{}'", node.getPath(), resourceType);
        }
    }
}
