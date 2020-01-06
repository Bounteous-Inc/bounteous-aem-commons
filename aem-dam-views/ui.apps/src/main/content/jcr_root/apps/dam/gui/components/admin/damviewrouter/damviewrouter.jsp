
<%--
  ADOBE CONFIDENTIAL

  Copyright 2015 Adobe Systems Incorporated
  All Rights Reserved.

  NOTICE:  All information contained herein is, and remains
  the property of Adobe Systems Incorporated and its suppliers,
  if any.  The intellectual and technical concepts contained
  herein are proprietary to Adobe Systems Incorporated and its
  suppliers and may be covered by U.S. and Foreign Patents,
  patents in process, and are protected by trade secret or copyright law.
  Dissemination of this information or reproduction of this material
  is strictly forbidden unless prior written permission is obtained
  from Adobe Systems Incorporated.
--%><%
%><%@page session="false"%><%
%><%@page import=" org.apache.sling.api.resource.Resource,
                 com.adobe.granite.security.user.UserPropertiesService,
                 org.apache.sling.tenant.Tenant,
                 org.apache.sling.api.resource.ValueMap,
                 org.apache.sling.featureflags.Features,
                 com.day.cq.dam.api.DamConstants,
                 com.adobe.granite.ui.components.Config,
                 com.day.cq.dam.api.DamEventRecorder"%><%
%><%@page import="org.apache.jackrabbit.api.security.user.Authorizable"%><%
%><%@include file="/libs/granite/ui/global.jsp"%><%
    Config cfg = new Config(resource);
    String assetsVanity = cfg.get("assetsVanityPath", "/assets.html");
    String assetsHome = "/aem/assetshome.html";
    String propertyName = "enableHomePage";
	String damViewsRoot = "/content/dam-views";
    // Checking for global flag
    
    Tenant tenant = resourceResolver.adaptTo(Tenant.class);
    String mountPoint = (null != tenant)? (String)tenant.getProperty("dam:assetsRoot") : damViewsRoot;
    if(null == mountPoint || mountPoint.trim().isEmpty()){
        mountPoint = damViewsRoot;
    }
    String contextPath = request.getContextPath();
    String contentPath = slingRequest.getRequestPathInfo().getSuffix();
    Authorizable user = resourceResolver.adaptTo(Authorizable.class);
    DamEventRecorder eventRecorder = sling.getService(DamEventRecorder.class);
    final Features featureManager = sling.getService(Features.class);
    final String assetHomePageFrag = "com.adobe.dam.asset.homepage";

    // Checking if event recording service is enabled and if feature manager is also enabled - if any one is not enabled we will not check user preference and send user to /assets.html
    if (!eventRecorder.isEnabled() || !(featureManager.getFeature(assetHomePageFrag)!=null && featureManager.isEnabled(assetHomePageFrag))) {
        response.sendRedirect(contextPath + assetsVanity + mountPoint);
        return;
    }
    if (user!=null){
        String userPath = user.getPath();
        Resource preferenceNode = null;
        preferenceNode = resourceResolver.getResource(userPath + "/" + UserPropertiesService.PREFERENCES_PATH);
        if (preferenceNode != null) {
            ValueMap conf = preferenceNode.adaptTo(ValueMap.class);
            String recordingEnabled = conf.get(propertyName,"false");
            if ("true".equals(recordingEnabled)) {
                response.sendRedirect(contextPath + assetsHome + mountPoint);
                return ;
            }
        }
    }

    response.sendRedirect(contextPath + assetsVanity + mountPoint);
%>