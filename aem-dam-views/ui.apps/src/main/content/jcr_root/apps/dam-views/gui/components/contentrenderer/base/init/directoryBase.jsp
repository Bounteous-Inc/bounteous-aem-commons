<%--
    Copied and modified from '/libs/dam/gui/coral/components/admin/contentrenderer/base/init'.
--%>
<%@page import="java.util.Date,
                javax.jcr.Node" %>
<%@taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0" %>
<%@include file="/libs/dam/gui/coral/components/admin/contentrenderer/base/init/base.jsp" %>
<%@include file="/apps/dam-views/gui/components/contentrenderer/base/directoryBase.jsp" %>

<%--###
    Directory Base Initializer
    =========

    This JSP is initializing all the attributes expected by '../directoryBase.jsp', it is expected to evaluate asset properties
    & perform performance incentive tasks at this JSP, hence it should not be triggered more than once per resource.
###--%>

<%
    String context = i18n.get("Folder");
    attrs.add("data-timeline", true);

    //MAC & CC Sharing
    boolean isMACShared = false;
    boolean isCCShared = false;
    boolean isMPShared = false;
    String sharedParentPath = null;
    boolean isRootMACShared = false;
    boolean isRootMPShared = false;

    // Directory last modification
    Calendar mod = null;
    if (resourceNode.hasProperty("jcr:lastModified")) {
        mod = resourceNode.getProperty("jcr:lastModified").getDate();
    }

    long directoryLastModification = (null != mod) ? mod.getTimeInMillis() : 0;
    if (directoryLastModification == 0 && resourceNode.hasProperty("jcr:created")) {
        Calendar created = resourceNode.getProperty("jcr:created").getDate();
        directoryLastModification = (null != created) ? created.getTimeInMillis() : 0;
    }

    String lastModified = xssAPI.encodeForHTML(new Date(directoryLastModification).toInstant().toString());
    String lastModifiedBy = "";
    if (resourceNode.hasProperty("jcr:lastModifiedBy")) {
        lastModifiedBy = resourceNode.getProperty("jcr:lastModifiedBy").getString();
        if (StringUtils.isNotBlank(lastModifiedBy)) {
            lastModifiedBy = AuthorizableUtil.getFormattedName(resourceResolver, lastModifiedBy);
        }
    }

    if (resourceNode.hasProperty("dam:ccTeamMembers")) {
        isCCShared = true;
    } else {
        Node sharedCCParent = findSharedParent(resourceNode, "dam:ccTeamMembers");
        if (sharedCCParent != null) {
            isCCShared = true;
        }
    }

    request.setAttribute(IS_CC_SHARED, isCCShared);
    if (resourceNode.hasProperty("macConfig")) {
        isMACShared = true;
    } else {
        Node sharedParent = findSharedParent(resourceNode, "macConfig");
        if (sharedParent != null) {
            sharedParentPath = sharedParent.getPath();
            isRootMACShared = true;
        }
    }

    request.setAttribute(IS_MAC_SHARED, isMACShared);
    request.setAttribute(IS_ROOT_MAC_SHARED, isRootMACShared);
    if (resourceNode.hasProperty("mpConfig")) {
        isMPShared = true;
    } else {
        Node sharedParent = findSharedParent(resourceNode, "mpConfig");
        if (sharedParent != null) {
            sharedParentPath = sharedParent.getPath();
            isRootMPShared = true;
        }
    }

    request.setAttribute(IS_MP_SHARED, isMPShared);
    request.setAttribute(IS_ROOT_MP_SHARED, isRootMPShared);

    // Processing profile details
    boolean isProcessingProfileEntitled = false;
    if (featureManager == null) {
        log.error("Cannot find the required parameter that is defined in the include from the parent jsp class.");
    } else {
        if (featureManager.getFeature(EntitlementConstants.ASSETS_PROCESSINGPROFILE_FEATURE_FLAG_PID) != null
            && featureManager.isEnabled(EntitlementConstants.ASSETS_PROCESSINGPROFILE_FEATURE_FLAG_PID)) {
            isProcessingProfileEntitled = true;
            request.setAttribute(IS_PROCESSING_PROFILE_ENTITLED, isProcessingProfileEntitled);
        }
    }

    // Processing profile details
    String[] profileTitleList = new String[] { "", "", "" };
    String[] profilePropertyList = new String[] { "jcr:content/metadataProfile", "jcr:content/imageProfile", "jcr:content/videoProfile" };
    String[] profileNamePropertyList = new String[] { "jcr:content/jcr:title", "name", "jcr:content/jcr:title" };

    for (int i = 0; i < 3; i++) {
        if (resourceNode.hasProperty(profilePropertyList[i])) {
            String profilePath = resourceNode.getProperty(profilePropertyList[i]).getValue().getString();
            if (profilePath.trim().isEmpty()) {
                continue;
            }

            Resource res = resourceResolver.getResource(profilePath);
            if (null != res) {
                Node node = res.adaptTo(Node.class);

                if (node != null) {
                    String jcrTitle = "";
                    profileTitleList[i] = node.getName();

                    if (node.hasProperty(profileNamePropertyList[i])) {
                        jcrTitle = node.getProperty(profileNamePropertyList[i]).getValue().getString();
                    } else {
                        // if the first pass doesnt work, this might be a profile/preset created in 6.3 which does not have a jcr:content node
                        if (!node.hasNode("jcr:content")) {
                            if (node.hasProperty("jcr:title")) {
                                jcrTitle = node.getProperty("jcr:title").getValue().getString();
                            }
                        }
                    }

                    // store jcr:title value if available, as that is always preferable over name
                    if (jcrTitle != null && !jcrTitle.trim().isEmpty()) {
                        profileTitleList[i] = jcrTitle;
                    }
                }
            }
        }
    }

    request.setAttribute(PROFILE_TITLE_LIST, profileTitleList);

    //CC quickaction
    boolean showCCQuickAction = false;
    if (hasJcrWrite && hasReplicate) {
        showCCQuickAction = true;
    }

    metaAttrs.add("data-is-mac-shared", isMACShared);
    metaAttrs.add("data-is-cc-shared", isCCShared);
    metaAttrs.add("data-is-root-mac-shared", isRootMACShared);
    metaAttrs.add("data-shared-root", xssAPI.encodeForHTMLAttr(sharedParentPath));

    String navigationHref = request.getContextPath() + "/apps/dam/gui/content/damviews.html" + resource.getPath();
%>
