package com.hs2solutions.aem.hfl.core.models.impl;

import com.adobe.acs.commons.models.injectors.annotation.SharedValueMapValue;
import com.adobe.acs.commons.wcm.PageRootProvider;
import com.hs2solutions.aem.hfl.core.models.Header;
import com.hs2solutions.aem.hfl.core.models.ScheduleTout;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

@Model(
        adaptables = {Resource.class, SlingHttpServletRequest.class},
        adapters = {Header.class},
        resourceType = "multi-site-demo/hfl-core/components/content/header"
)
public class HeaderImpl implements Header {
    @SlingObject
    private Resource resource;

    @OSGiService
    private PageRootProvider pageRootProvider;

    @SharedValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String logo;

    @SharedValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String slogan;

    @Override
    public String getHomePageUrl() {
        String homePagePath = pageRootProvider.getRootPagePath(resource.getPath());
        return StringUtils.isNotBlank(homePagePath) ? (homePagePath + ".html") : null;
    }

    @Override
    public String getLogoSrc() {
        return logo;
    }

    @Override
    public String getSlogan() {
        return slogan;
    }
}
