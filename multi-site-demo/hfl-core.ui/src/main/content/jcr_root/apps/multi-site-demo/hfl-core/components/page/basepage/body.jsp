<%@include file="/apps/multi-site-demo/hfl-core/global/global.jsp" %>
<%@page session="false" %>

<body class="page-template-${component.cellName}">
    <div class="page-header">
        <cq:include path="page-header-par" resourceType="foundation/components/iparsys"/>
    </div>

    <div class="page-content">
        <cq:include script="main.jsp"/>
    </div>

    <div class="page-footer">
        <cq:include path="page-footer-par" resourceType="foundation/components/iparsys"/>
    </div>
</body>
