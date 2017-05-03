<%@include file="/apps/multi-site-demo/hfl-core/global/global.jsp" %>
<%@page session="false"%>

<cq:includeClientLib categories="cq.foundation-main"/>

<c:if test="${not empty currentDesign.contentResource.valueMap.clientLib}">
    <cq:includeClientLib categories="${currentDesign.contentResource.valueMap.clientLib}" />
</c:if>