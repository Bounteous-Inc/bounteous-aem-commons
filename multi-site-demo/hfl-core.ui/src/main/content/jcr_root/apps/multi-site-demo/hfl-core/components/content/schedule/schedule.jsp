<%@include file="/apps/multi-site-demo/hfl-core/global/global.jsp" %>
<%@page session="false" %>

<div>
    <c:if test="${properties.allowFilter}">
        <c:if test="${empty sharedProperties.copyFilterAll or empty sharedProperties.copyFilterHome or empty sharedProperties.copyFilterAway}">
            <p class="author-msg">
                Please configure the filter labels in Shared Properties.
            </p>
        </c:if>
        <div class="schedule-filter">
            <label>
                <input type="radio" name="filter" value="all" checked/><span>${sharedProperties.copyFilterAll}</span>
            </label>
            <label>
                <input type="radio" name="filter" value="home"/><span>${sharedProperties.copyFilterHome}</span>
            </label>
            <label>
                <input type="radio" name="filter" value="away"/><span>${sharedProperties.copyFilterAway}</span>
            </label>
        </div>
    </c:if>
    <c:if test="${empty sharedProperties.copyLabelHome or empty sharedProperties.copyLabelAway}">
        <p class="author-msg">
            Please configure the home/away labels in Shared Properties.
        </p>
    </c:if>
    <div class="schedule-games"
         data-resource="${resource.path}"
         data-label-home="${sharedProperties.copyLabelHome}"
         data-label-away="${sharedProperties.copyLabelAway}">
        <!-- Content populated via JavaScript -->
    </div>
</div>