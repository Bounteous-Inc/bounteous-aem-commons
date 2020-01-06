<%--
    Copied and modified from '/libs/dam/gui/coral/components/admin/contentrenderer/base'.
--%>
<%@page import="org.apache.sling.api.resource.Resource,
                javax.jcr.security.AccessControlManager,
                javax.jcr.security.AccessControlPolicy,
                javax.jcr.security.AccessControlEntry,
                javax.jcr.AccessDeniedException,
                javax.jcr.security.AccessControlList,
                javax.jcr.RepositoryException,
                javax.jcr.Session,
                javax.jcr.Node,
                javax.jcr.security.Privilege,
                java.util.List,
                java.util.ArrayList,
                org.apache.jackrabbit.api.security.JackrabbitAccessControlList,
                org.apache.jackrabbit.api.security.JackrabbitAccessControlEntry,
                org.apache.jackrabbit.commons.jackrabbit.authorization.AccessControlUtils,
                com.day.cq.dam.api.DamConstants,
                com.day.cq.dam.entitlement.api.EntitlementConstants,
                org.apache.sling.featureflags.Features,
                com.day.cq.dam.commons.util.UIHelper" %>
<%@taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0" %>

<%--###
    Directory Base API
    =========

    This JSP is included by spawned pages, hence it may gets called multiple time. This JSP should not evaluate or
    perform any execution task, rather it should only contain methods & configs to avoid performance overhead.
###--%>

<%
    // Attributes set by '/contentrenderer/base/init/directoryBase.jsp' should be accessed through following constants

    final String IS_CC_SHARED = "com.adobe.cq.assets.contentrenderer.directory.isCCShared";
    final String IS_MAC_SHARED = "com.adobe.cq.assets.contentrenderer.directory.isMACShared";
    final String IS_MP_SHARED = "com.adobe.cq.assets.contentrenderer.directory.isMPShared";
    final String IS_ROOT_MAC_SHARED = "com.adobe.cq.assets.contentrenderer.directory.isRootMACShared";
    final String IS_ROOT_MP_SHARED = "com.adobe.cq.assets.contentrenderer.directory.isRootMPShared";
    final String IS_PROCESSING_PROFILE_ENTITLED = "com.adobe.cq.assets.contentrenderer.directory.isProcessingProfileEntitled";
    final String PROFILE_TITLE_LIST = "com.adobe.cq.assets.contentrenderer.directory.profileTitleList";
%>

<%!
    private List<String> getDirectoryActionRels(boolean hasJcrRead, boolean hasModifyAccessControl, boolean hasJcrWrite, boolean hasReplicate,
        boolean isMACShared, boolean isCCShared, boolean isRootMACShared, boolean isMPShared, boolean isRootMPShared) {
        List<String> actionRels = new ArrayList<String>();

        actionRels.add("cq-damadmin-admin-actions-createworkflow");

        if (hasJcrRead) {
            actionRels.add("icon-share");
            //actionRels.add("cq-damadmin-admin-actions-download-activator");
            //actionRels.add("cq-damadmin-admin-actions-add-to-collection-activator");
            actionRels.add("cq-damadmin-admin-actions-revealdesktop");
            actionRels.add("aem-assets-admin-actions-moderatetags-activator");
        }

        if (hasModifyAccessControl) {
            actionRels.add("cq-damadmin-admin-actions-adhocassetshare-activator");
        }

        if (hasJcrWrite) {
            actionRels.add("cq-damadmin-admin-actions-fileupload-at-activator");
            actionRels.add("cq-damadmin-admin-actions-createfolder-at-activator");
            actionRels.add("cq-damadmin-admin-actions-imageset-at-activator");
            actionRels.add("cq-damadmin-admin-actions-spinset-at-activator");
            actionRels.add("cq-damadmin-admin-actions-mixedmedia-at-activator");
            actionRels.add("cq-damadmin-admin-actions-createcarousel-at-activator");
            actionRels.add("cq-damadmin-admin-actions-createfragment-at-activator");
            actionRels.add("cq-damadmin-admin-actions-foldershare");
            actionRels.add("dam-asset-createtask-action-activator");
            actionRels.add("dam-asset-exportmetadata-action-activator");
        }

        if (hasJcrWrite && hasReplicate) {
            actionRels.add("cq-damadmin-admin-actions-macshare-activator");
            actionRels.add("cq-damadmin-admin-actions-ccshare-activator");
            actionRels.add("cq-damadmin-admin-actions-mpshare-activator");
        }

        if (isMACShared || isCCShared || isRootMACShared || isMPShared || isRootMPShared) {
            actionRels.remove("cq-damadmin-admin-actions-move-activator");
            actionRels.remove("cq-damadmin-admin-actions-delete-activator");
        }

        return actionRels;
    }

    private Node findSharedParent(Node currentNode, String propName) throws RepositoryException {
        if (currentNode == null || DamConstants.MOUNTPOINT_ASSETS.equals(currentNode.getPath()) || "/".equals(currentNode.getPath())) {
            return null;
        }

        Node parent;
        try {
            parent = currentNode.getParent();
        } catch (AccessDeniedException ade) {
            return null;
        }

        if (parent.hasProperty(propName)) {
            return parent;
        }

        return findSharedParent(parent, propName);
    }

    private boolean isPrivate(Resource resource) {
        String path = resource.getPath();
        Session session = resource.getResourceResolver().adaptTo(Session.class);
        try {
            AccessControlManager acm = session.getAccessControlManager();
            for (AccessControlPolicy policy : acm.getPolicies(path)) {
                if (policy instanceof AccessControlList) {
                    AccessControlList accessControlList = (AccessControlList) policy;
                    for (AccessControlEntry ace : accessControlList.getAccessControlEntries()) {
                        boolean isEveryone = ace.getPrincipal().equals(AccessControlUtils.getEveryonePrincipal(session));
                        boolean isJCRALL = false;
                        boolean isAllow = true;
                        for (Privilege privilege : ace.getPrivileges()) {
                            if (privilege.getName().equalsIgnoreCase("jcr:all")) {
                                isJCRALL = true;
                            }
                        }

                        if (ace instanceof JackrabbitAccessControlEntry) {
                            isAllow = ((JackrabbitAccessControlEntry) ace).isAllow();
                        }

                        if (isEveryone && isJCRALL && !isAllow) {
                            return true;
                        }
                    }
                }
            }
        } catch (RepositoryException e) {
        }

        return false;
    }
%>
