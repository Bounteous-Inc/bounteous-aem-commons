<%--
    Copied and modified from '/libs/dam/gui/coral/components/admin/contentrenderer/row/directory'.
--%>
<%@page import="org.apache.sling.api.resource.Resource"%>
<%@taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0"%>
<%@include file="/apps/dam-views/gui/components/contentrenderer/base/init/directoryBase.jsp"%>

<%
    String directoryActionRels = StringUtils.join(getDirectoryActionRels(hasJcrRead, hasModifyAccessControl, hasJcrWrite,
            hasReplicate, isMACShared, isCCShared, isRootMACShared, isMPShared, isRootMPShared), " ");

    request.setAttribute("actionRels", actionRels.concat(" " + directoryActionRels));

    navigationHref = request.getContextPath() + "/apps/dam/gui/content/damviews.html" + resource.getPath();
    attrs.add("data-foundation-collection-navigator-href", xssAPI.getValidHref(navigationHref));
    attrs.addClass("foundation-collection-navigator");
    attrs.add("is", "coral-table-row");
    attrs.add("data-item-title", resourceTitle);
    attrs.add("data-item-type", type);

    request.setAttribute("com.adobe.assets.meta.attributes", metaAttrs);
    PublicationStatus publicationStatus = getPublicationStatus(request, i18n);
%>

<tr <%= attrs %>>
    <td is="coral-table-cell" coral-table-rowselect>
        <coral-icon class="foundation-collection-item-thumbnail" icon="folder"></coral-icon>
    </td>

    <td is="coral-table-cell" value="<%= xssAPI.encodeForHTMLAttr(resource.getName()) %>">
        <%= xssAPI.encodeForHTML(resource.getName()) %>
    </td>

    <td class="foundation-collection-item-title" is="coral-table-cell" value="<%= xssAPI.encodeForHTMLAttr(resourceAbsTitle) %>"><%= xssAPI.encodeForHTML(resourceAbsTitle) %></td>
    <td is="coral-table-cell" value="<%= displayLanguage %>"><%= displayLanguage %></td>
    <td is="coral-table-cell" value="0"></td> <!--Adding a placeholder column for expiryStatus -->
    <td is="coral-table-cell" value="0"></td> <!--Adding a placeholder column for encodingStatus -->
    <td is="coral-table-cell" value="type"><%= i18n.get("FOLDER") %></td>
    <td is="coral-table-cell" value="0"></td>   <!--Adding a placeholder column for dimensions -->
    <td is="coral-table-cell" value="0"></td>   <!--Adding a placeholder column for size -->
    <td is="coral-table-cell" value="0"></td>   <!--Adding a placeholder column for rating -->
    <td is="coral-table-cell" value="0"></td>   <!--Adding a placeholder column for usagescore-->
    <td is="coral-table-cell" value="0"></td>   <!--Adding a placeholder column for impression score-->
    <td is="coral-table-cell" value="0"></td>   <!--Adding a placeholder column for click score-->

    <td is="coral-table-cell" value="<%= xssAPI.encodeForHTMLAttr(Long.toString(directoryLastModification)) %>">
        <% if (lastModified != null) { %>
            <foundation-time type="datetime" value="<%= xssAPI.encodeForHTMLAttr(lastModified) %>"></foundation-time>
            <% // Modified-after-publish indicator
            if (publishDateInMillis > 0 && publishDateInMillis < directoryLastModification) {
                String modifiedAfterPublishStatus = i18n.get("Modified since last publication"); %>
                <coral-icon icon="alert" style = "margin-left: 5px;" size="XS" title="<%= xssAPI.encodeForHTMLAttr(modifiedAfterPublishStatus) %>"></coral-icon>
            <% } %>
                <div class="foundation-layout-util-subtletext"><%= xssAPI.encodeForHTML(lastModifiedBy) %></div>
        <% } %>
    </td>

    <td is="coral-table-cell" value="<%= (!isDeactivated && publishedDate != null) ? xssAPI.encodeForHTMLAttr(Long.toString(publishDateInMillis)) : "0" %>">
    <%
        // Published date and status
        String icon = null;
        String briefStatus = "";
        String title = "";

        if (publicationStatus.getAction() != null) {
            icon = publicationStatus.getIcon();
            briefStatus = publicationStatus.getBriefStatus();
            title = publicationStatus.getDetailedStatus();
        } else {
            if (publishedDate != null) {
                icon = isDeactivated ? "globeStrike" : "globe";
            }
        }

        if (icon != null) { %>
            <coral-icon icon="<%= icon %>" style = "margin-left: 5px;" title = "<%= title %>" size="XS"%></coral-icon>
            <% if (briefStatus != null && briefStatus.length() > 0) { %>
                <label> <%= i18n.getVar(briefStatus) %> </label>
            <% } else { %>
                <foundation-time type="datetime" value="<%= xssAPI.encodeForHTMLAttr(publishedDate) %>"></foundation-time>
            <% }
        } else { %>
            <span><%= xssAPI.encodeForHTML(i18n.get("Not published")) %></span>
        <% }

        // Published by
        if (publishedBy != null) { %>
            <div class="foundation-layout-util-subtletext"><%= xssAPI.encodeForHTML(publishedBy) %></div>
        <% } %>

        <cq:include script = "applicableRelationships.jsp"/>
    </td>

    <td is="coral-table-cell"></td>  <!--Adding a placeholder column for workflow status-->
    <td is="coral-table-cell"></td> <!--Adding a placeholder column for checkout status-->
    <td is="coral-table-cell" value="0"></td>   <!--Adding a placeholder column for comments-->

    <% if (isProcessingProfileEntitled && !profileTitleList[0].trim().isEmpty()) { %>
        <td is="coral-table-cell" value="0"><%= xssAPI.encodeForHTML(profileTitleList[0].trim()) %></td>
    <% } else{ %>
        <td is="coral-table-cell" value="0"></td>
    <% } %>

    <% if (isProcessingProfileEntitled && !profileTitleList[1].trim().isEmpty()) { %>
        <td is="coral-table-cell" value="0"><%= xssAPI.encodeForHTML(profileTitleList[1].trim()) %></td>
    <% } else{ %>
        <td is="coral-table-cell" value="0"></td>
    <% } %>

    <% if (isProcessingProfileEntitled && !profileTitleList[2].trim().isEmpty()) { %>
        <td is="coral-table-cell" value="0"><%= xssAPI.encodeForHTML(profileTitleList[2].trim()) %></td>
    <% } else{ %>
        <td is="coral-table-cell" value="0"></td>
    <% } %>

    <cq:include script = "reorder.jsp"/>
    <cq:include script = "meta.jsp"/>
</tr>
