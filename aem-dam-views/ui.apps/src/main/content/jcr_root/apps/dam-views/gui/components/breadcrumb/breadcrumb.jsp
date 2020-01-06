<%--
    Copied and modified from '/libs/dam/gui/components/admin/idsprint/endor/breadcrumb'.
--%>
<%@page session="false" contentType="text/html; charset=utf-8"%>
<%@page import="com.adobe.granite.ui.components.ComponentHelper,
                com.adobe.granite.ui.components.Config,
                com.adobe.granite.ui.components.ds.DataSource,
                com.adobe.granite.ui.components.ds.SimpleDataSource,
                com.adobe.granite.ui.components.ds.ValueMapResource,
                com.day.cq.dam.commons.util.UIHelper,
                com.day.cq.i18n.I18n,
                java.util.ArrayList,
                java.util.HashMap,
                java.util.List,
                javax.jcr.Node,
                org.apache.sling.api.resource.Resource,
                org.apache.sling.api.resource.ResourceMetadata,
                org.apache.sling.api.resource.ValueMap,
                org.apache.sling.api.wrappers.ValueMapDecorator"%>
<%@taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0" %>
<cq:defineObjects />

<%
    Resource contentRsc = UIHelper.getCurrentSuffixResource(slingRequest);
    if (contentRsc.getPath().endsWith("/subassets") && contentRsc.getParent().isResourceType("dam:Asset")) {
        contentRsc = contentRsc.getParent();
    }

    ComponentHelper cmp = new ComponentHelper(pageContext);
    I18n i18n = cmp.getI18n();
    Config cfg = cmp.getConfig();

    String relativePath = cfg.get("relativePath", "/content/damviews");
    String baseUrl = cfg.get("dam:baseUrl", String.class);
    String rootTitle = cfg.get("rootTitle", "Catalogs");

    if (contentRsc == null) {
        return;
    }

    Node contentNode = contentRsc.adaptTo(Node.class);
    if (contentNode == null ) {
        return;
    }

    List<Resource> syntheticItemResources = new ArrayList<>();
    Resource temp = contentRsc;

    while (temp != null) {
        Node tempNode = temp.adaptTo(Node.class);
        ValueMap crumbVM = new ValueMapDecorator(new HashMap<>());
        boolean isRoot = temp.getPath().equals(relativePath);

        if (tempNode.isNodeType("dam:Asset")) {
            crumbVM.put("href", request.getContextPath() + baseUrl + temp.getPath()+"/subassets");
        }
        else {
            crumbVM.put("href", request.getContextPath() + baseUrl + temp.getPath());
        }

        if (isRoot) {
            crumbVM.put("title", i18n.get(rootTitle));
        }
        else {
            crumbVM.put("title", UIHelper.getTitle(temp));
        }

        syntheticItemResources.add(new ValueMapResource(resourceResolver, new ResourceMetadata(), "nt:unstructured", crumbVM));

        if (isRoot) {
            break;
        }
        temp = temp.getParent();
    }

    request.setAttribute(DataSource.class.getName(), new SimpleDataSource(syntheticItemResources.iterator()));
%>
