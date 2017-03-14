<%@include file="/apps/multi-site-demo/hfl-core/global/global.jsp" %>
<%@page session="false" %>

<body class="page-template-${component.cellName}">
    <cq:include script="page-header.jsp"/>

    <div class="page-content">
        <cq:include script="main.jsp"/>
    </div>

    <cq:include script="page-footer.jsp"/>
</body>
