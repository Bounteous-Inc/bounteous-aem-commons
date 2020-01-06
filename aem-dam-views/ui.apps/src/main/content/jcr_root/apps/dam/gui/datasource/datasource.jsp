<%--
    Copied and modified from '/libs/dam/gui/coral/components/admin/catalogs/datasource'.
--%>
<%@page import="java.util.Collections" %>
<%@include file="/libs/granite/ui/global.jsp" %>
<%@page import="java.util.ArrayList,
                javax.jcr.Node,
                javax.jcr.Session,
                java.util.Iterator,
                java.util.Map,
                java.util.Arrays,
                java.util.Map.Entry,
                org.apache.commons.lang.StringUtils,
                org.apache.commons.lang.ArrayUtils,
                org.apache.sling.api.resource.Resource,
                org.apache.sling.api.resource.ValueMap,
                org.apache.sling.api.SlingHttpServletRequest,
                com.adobe.granite.ui.components.Config,
                com.adobe.granite.ui.components.ExpressionHelper,
                com.adobe.granite.ui.components.rendercondition.RenderCondition,
                org.apache.sling.api.resource.ResourceWrapper,
                com.day.cq.dam.commons.util.UIHelper,
                com.day.cq.dam.api.Asset,
                com.day.cq.dam.api.DamConstants,
                com.day.cq.commons.jcr.JcrConstants,
                java.util.List" %>
<%
    /**
     *   A datasource wrapping assetsdatasouce and adding linked assets
     *
     *   @datasource
     *   @name AssetsDatasource
     *   @location /libs/dam/gui/coral/components/admin/catalogs/datasource
     *
     *   @property {String}   itemResourceType Using which every resource in this datasource is rendered with
     *   @property {String[]} filters which is used to filter the resources based on node/mime type
     *   @property {String} limit an EL that specifies number of resources to be fetched
     *   @property {String} offset an EL that specifies the offset
     *   @property {String} &lt;other&gt; will be added as request attribute
     *   @property {String} exclude to exclude resources
     *
     *   @example
     *   + datasource
     *      - jcr:primaryType = "nt:unstructured"
     *      - itemResourceType = "dam/gui/components/admin/childasset"
     *      - sling:resourceType = "dam/gui/coral/components/admin/catalogs/datasource"
     *      - limit = "10"
     *      - offset = "${empty requestPathInfo.selectors[1] ? &quot;10&quot; : requestPathInfo.selectors[1]}"
     *      - filters = "[dam:Asset]"
     */

    final int ROWS_DEFAULT = 15;
    final int ROWS_NEXT_TIME_ONWARDS = 10;
    ExpressionHelper ex = cmp.getExpressionHelper();
    String contentPath = ex.get(resource.getValueMap().get("path", String.class), String.class);
    Resource contentRsc = resourceResolver.getResource(contentPath);

    if (contentRsc == null) {
        contentRsc = UIHelper.getCurrentSuffixResource(slingRequest);
    }

    Node contentRscNode = contentRsc.adaptTo(Node.class);
    if (!(
        (contentRscNode.isNodeType(JcrConstants.NT_FOLDER) || contentRscNode.isNodeType("cq:Page") || contentRscNode.isNodeType("cq:PageContent")) ||
            contentRscNode.isNodeType("dam:Asset") && contentRscNode.hasNode("subassets"))) {
        return;
    }

    Iterator<?> coverPagesItr = Collections.emptyIterator();
    ;
    com.adobe.granite.asset.api.Asset catalog;
    if ((catalog = contentRsc.getParent().adaptTo(com.adobe.granite.asset.api.Asset.class)) != null && catalog.listRelated("coverpage") != null) {
        coverPagesItr = catalog.listRelated("coverpage");
    }
    request.setAttribute("com.adobe.cq.assets.linked", coverPagesItr);
%>

<sling:include path="<%= resource.getPath () %>" resourceType="dam/gui/coral/components/commons/ui/shell/datasources/damviewdatasourceservletdatasource"/>
