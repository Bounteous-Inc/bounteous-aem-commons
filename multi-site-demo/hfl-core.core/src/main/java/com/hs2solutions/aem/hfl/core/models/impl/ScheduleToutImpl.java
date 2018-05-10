package com.hs2solutions.aem.hfl.core.models.impl;

import com.adobe.acs.commons.models.injectors.annotation.SharedValueMapValue;
import com.adobe.cq.wcm.core.components.models.Page;
import com.hs2solutions.aem.hfl.core.models.BasePage;
import com.hs2solutions.aem.hfl.core.models.ScheduleTout;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Model(
        adaptables = {Resource.class, SlingHttpServletRequest.class},
        adapters = {ScheduleTout.class},
        resourceType = "multi-site-demo/hfl-core/components/content/schedule-tout"
)
public class ScheduleToutImpl implements ScheduleTout {
    @SharedValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String logo;

    @SharedValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String message;

    @SharedValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String ctaLabel;

    @SharedValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String path;

    @Override
    public String getLogoSrc() {
        return logo;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getCtaUrl() {
        return StringUtils.isNotBlank(path) ? (path + ".html") : null;
    }

    @Override
    public String getCtaLabel() {
        return ctaLabel;
    }
}
