package com.hs2solutions.aem.hfl.core.models.impl;

import com.adobe.acs.commons.models.injectors.annotation.SharedValueMapValue;
import com.adobe.acs.commons.wcm.PageRootProvider;
import com.hs2solutions.aem.hfl.core.models.Header;
import com.hs2solutions.aem.hfl.core.models.Schedule;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(
        adaptables = {Resource.class, SlingHttpServletRequest.class},
        adapters = {Schedule.class},
        resourceType = "multi-site-demo/hfl-core/components/content/schedule"
)
public class ScheduleImpl implements Schedule {
    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private boolean allowFilter;

    @SharedValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String copyFilterAll;

    @SharedValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String copyFilterHome;

    @SharedValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String copyFilterAway;

    @SharedValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String copyLabelHome;

    @SharedValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String copyLabelAway;

    @Override
    public boolean getAllowFilter() {
        return allowFilter;
    }

    @Override
    public String getCopyFilterAll() {
        return copyFilterAll;
    }

    @Override
    public String getCopyFilterHome() {
        return copyFilterHome;
    }

    @Override
    public String getCopyFilterAway() {
        return copyFilterAway;
    }

    public String getCopyLabelHome() {
        return copyLabelHome;
    }

    public String getCopyLabelAway() {
        return copyLabelAway;
    }

}
