<%--
    Copied and modified from '/libs/dam/gui/coral/components/admin/contentrenderer/column/directory'.
--%>
<%@page import="org.apache.sling.api.resource.Resource,
                java.util.Iterator" %>
<%@taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0" %>
<%@include file="/apps/dam-views/gui/components/contentrenderer/base/init/directoryBase.jsp" %>
<%@include file="/libs/dam/gui/coral/components/admin/contentrenderer/column/common/common.jsp" %>

<%
    String directoryActionRels = StringUtils.join(getDirectoryActionRels(hasJcrRead, hasModifyAccessControl, hasJcrWrite,
            hasReplicate, isMACShared, isCCShared, isRootMACShared, isMPShared, isRootMPShared), " ");
    String name = resource.getName();

    request.setAttribute("actionRels", actionRels.concat(" " + directoryActionRels));

    attrs.add("itemscope", "itemscope");
    attrs.add("data-item-title", resourceTitle);
    attrs.add("data-item-type", type);
    attrs.add("variant", "drilldown");

    request.setAttribute("com.adobe.assets.meta.attributes", metaAttrs);
%>

<coral-columnview-item <%= attrs %>>
    <cq:include script="meta.jsp"/>

    <coral-columnview-item-thumbnail>
        <coral-icon icon="folder"></coral-icon>
    </coral-columnview-item-thumbnail>

    <coral-columnview-item-content>
        <div class="foundation-collection-item-title" itemprop="title" title="<%= xssAPI.encodeForHTMLAttr(resourceTitle) %>">
            <%= xssAPI.encodeForHTML(resourceTitle) %>
        </div>

        <% if (name != null && !name.equals(resourceTitle)) { %>
            <div class="foundation-layout-util-subtletext">
                <%= xssAPI.encodeForHTML(name) %>
            </div>
        <% } %>
    </coral-columnview-item-content>

    <cq:include script="applicableRelationships.jsp"/>
    <cq:include script="link.jsp"/>
</coral-columnview-item>

<%!
    //Add private methods here
%>
