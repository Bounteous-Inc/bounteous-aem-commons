<%@include file="/apps/multi-site-demo/hfl-core/global/global.jsp" %>
<%@page session="false" %>

<header>
    <c:if test="${not empty globalProperties.logo}">
        <a class="logo" href="${msd:getHomePagePath(resource).path}.html">
            <img src="${globalProperties.logo}"/>
        </a>
    </c:if>
    <c:if test="${not empty sharedProperties.slogan}">
        <p class="slogan">
            ${sharedProperties.slogan}
        </p>
    </c:if>
    <c:if test="${empty globalProperties.logo and empty sharedProperties.slogan}">
        <p class="author-msg">
            Please configure the header
        </p>
    </c:if>
</header>