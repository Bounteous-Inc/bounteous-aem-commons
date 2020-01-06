<%--
    Copied and modified from '/libs/dam/gui/coral/components/admin/contentrenderer/card/directory'.
--%>
<%@include file="/libs/granite/ui/global.jsp" %>
<%@taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0" %>
<%@include file="/libs/dam/gui/coral/components/admin/contentrenderer/card/common/processAttributes.jsp" %>
<sling:include resourceType="dam/gui/components/s7dam/smartcroprenditions/rendercondition"/>
<%@include file="/apps/dam-views/gui/components/contentrenderer/base/directoryBase.jsp" %>

<% if (showQuickActions) { %>
    <coral-quickactions target="_prev" alignmy="left top" alignat="left top">
        <coral-quickactions-item icon="check" class="foundation-collection-item-activator">
            <%= xssAPI.encodeForHTML(i18n.get("Select")) %>
        </coral-quickactions-item>
    </coral-quickactions>
<% } %>
