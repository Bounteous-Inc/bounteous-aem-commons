<%@include file="/apps/multi-site-demo/hfl-core/global/global.jsp" %>
<%@page session="false" %>

<div>
    <c:choose>
        <c:when test="${empty sharedProperties.path or empty sharedProperties.message or empty sharedProperties.ctaLabel}">
            <p class="author-msg">
                Please configure all Shared Properties.
            </p>
        </c:when>
        <c:otherwise>
            <c:if test="${not empty globalProperties.logo}">
                <img class="logo" src="${globalProperties.logo}"/>
            </c:if>
            <h3>${mergedProperties.message}</h3>
            <a href="${sharedProperties.path}.html">${mergedProperties.ctaLabel}</a>
        </c:otherwise>
    </c:choose>
</div>