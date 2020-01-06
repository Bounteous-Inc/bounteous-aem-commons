<%--
    Copied and modified from '/libs/dam/gui/coral/components/admin/contentrenderer/card/directory'.
--%>
<%@include file="/libs/granite/ui/global.jsp" %>
<%@page import="org.apache.sling.api.resource.Resource,
                javax.jcr.Node,
                com.day.cq.dam.api.Asset" %>
<%@taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0" %>
<%@include file="/libs/dam/gui/coral/components/admin/contentrenderer/base/base.jsp" %>
<%@include file="/apps/dam-views/gui/components/contentrenderer/base/directoryBase.jsp" %>

<%
    boolean isCCShared = request.getAttribute(IS_CC_SHARED) != null ? (boolean) request.getAttribute(IS_CC_SHARED) : false;
    boolean isMACShared = request.getAttribute(IS_MAC_SHARED) != null ? (boolean) request.getAttribute(IS_MAC_SHARED) : false;
    boolean isMPShared = request.getAttribute(IS_MP_SHARED) != null ? (boolean) request.getAttribute(IS_MP_SHARED) : false;
    boolean isRootMACShared = request.getAttribute(IS_ROOT_MAC_SHARED) != null ? (boolean) request.getAttribute(IS_ROOT_MAC_SHARED) : false;
    boolean isRootMPShared = request.getAttribute(IS_ROOT_MP_SHARED) != null ? (boolean) request.getAttribute(IS_ROOT_MP_SHARED) : false;
    boolean isProcessingProfileEntitled = request.getAttribute(IS_PROCESSING_PROFILE_ENTITLED) != null
            ? (boolean) request.getAttribute(IS_PROCESSING_PROFILE_ENTITLED) : false;

    String[] profileTitleList = request.getAttribute(PROFILE_TITLE_LIST) != null
            ? (String[]) request.getAttribute(PROFILE_TITLE_LIST) : new String[] { "", "", "" };
    long publishDateInMillis = request.getAttribute(PUBLISH_DATE_IN_MILLIS) != null ? (long) request.getAttribute(PUBLISH_DATE_IN_MILLIS) : -1;
    String publishedDate = request.getAttribute(PUBLISHED_DATE) != null ? (String) request.getAttribute(PUBLISHED_DATE) : null;
    boolean isDeactivated = request.getAttribute(IS_DEACTIVATED) != null ? (boolean) request.getAttribute(IS_DEACTIVATED) : false;
    String publishedBy = request.getAttribute(PUBLISHED_BY) != null ? (String) request.getAttribute(PUBLISHED_BY) : "";
    PublicationStatus publicationStatus = getPublicationStatus(request, i18n);
    JSONObject viewSettings = (JSONObject) request.getAttribute(VIEW_SETTINGS);
    String dateFormat = viewSettings.getString(VIEW_SETTINGS_PN_DATE_FORMAT);
%>

<coral-card-propertylist>
    <% if (isCCShared) { %>
        <coral-card-property icon="creativeCloud" title="<%= xssAPI.encodeForHTMLAttr(i18n.get("Shared with Creative Cloud")) %>"></coral-card-property>
    <% } else if (isMACShared || isRootMACShared) { %>
        <coral-card-property icon="shareCheck" title="<%= xssAPI.encodeForHTMLAttr(i18n.get("Shared with Marketing Cloud")) %>"></coral-card-property>
    <% } else if (isMPShared || isRootMPShared) { %>
        <coral-card-property icon="shareCheck" title="<%= xssAPI.encodeForHTMLAttr(i18n.get("Shared with Brand Portal")) %>"></coral-card-property>
    <% }
        String icon = null;
        String briefStatus = "";
        if (publicationStatus.getAction() != null) {
            icon = publicationStatus.getIcon();
            briefStatus = publicationStatus.getBriefStatus();
        } else {
            if (publishedDate != null) {
                icon = isDeactivated ? "globeStrike" : "globe";
            }
        }
        if (icon != null) { %>
            <coral-card-property icon="<%= icon %>" title="<%= xssAPI.encodeForHTMLAttr(i18n.get("Asset Publication Status")) %>">
                <% if (!briefStatus.equals("")) { %>
                    <label><%= i18n.getVar(briefStatus) %>
                    </label>
                <% } else { %>
                    <foundation-time type="datetime" format="<%= xssAPI.encodeForHTMLAttr(dateFormat) %>"
                                     value="<%= xssAPI.encodeForHTMLAttr(publishedDate) %>">
                    </foundation-time>
                <% } %>
            </coral-card-property>
    <%  } %>

    <% if (isPrivate(resource)) { %>
        <coral-card-property icon="key" title="<%= xssAPI.encodeForHTMLAttr(i18n.get("Private Folder")) %>"></coral-card-property>
    <% } %>

    <% if (isProcessingProfileEntitled) { %>
        <% if (!profileTitleList[0].trim().isEmpty()) { %>
            <coral-card-property icon="data" title="<%= xssAPI.encodeForHTMLAttr(i18n.get("Metadata Profile")) %>">
                <%= xssAPI.encodeForHTML(profileTitleList[0].trim()) %>
            </coral-card-property>
        <% } %>

        <% if (!profileTitleList[1].trim().isEmpty()) { %>
            <coral-card-property icon="image" title="<%= xssAPI.encodeForHTMLAttr(i18n.get("Image Profile")) %>">
                <%= xssAPI.encodeForHTML(profileTitleList[1].trim()) %>
            </coral-card-property>
        <% } %>

        <% if (!profileTitleList[2].trim().isEmpty()) { %>
            <coral-card-property icon="film" title="<%= xssAPI.encodeForHTMLAttr(i18n.get("Video Profile")) %>">
                <%= xssAPI.encodeForHTML(profileTitleList[2].trim()) %>
            </coral-card-property>
        <% } %>
    <% } %>
</coral-card-propertylist>
