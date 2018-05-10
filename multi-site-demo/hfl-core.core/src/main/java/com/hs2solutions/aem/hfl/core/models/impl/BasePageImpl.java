package com.hs2solutions.aem.hfl.core.models.impl;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.wcm.core.components.models.NavigationItem;
import com.adobe.cq.wcm.core.components.models.Page;
import com.day.cq.wcm.api.designer.Design;
import com.hs2solutions.aem.hfl.core.models.BasePage;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.via.ForcedResourceType;
import org.apache.sling.models.annotations.via.ResourceSuperType;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Calendar;
import java.util.Map;


@Model(
        adaptables = {Resource.class, SlingHttpServletRequest.class},
        adapters = {Page.class, BasePage.class},
        resourceType = "multi-site-demo/hfl-core/components/page/basepage"
)
public class BasePageImpl implements BasePage {
    @ScriptVariable(injectionStrategy = InjectionStrategy.OPTIONAL)
    private Design currentDesign;

    @Self
    @Via(type = ForcedResourceType.class, value = "core/wcm/components/page/v2/page")
    private Page superTypePage;

    @Override
    public String getLanguage() {
        return superTypePage.getLanguage();
    }

    @Override
    public Calendar getLastModifiedDate() {
        return superTypePage.getLastModifiedDate();
    }

    @Override
    public String[] getKeywords() {
        return superTypePage.getKeywords();
    }

    @Override
    public String getDesignPath() {
        return superTypePage.getDesignPath();
    }

    @Override
    public String getStaticDesignPath() {
        return superTypePage.getStaticDesignPath();
    }

    @Override
    public Map<String, String> getFavicons() {
        return superTypePage.getFavicons();
    }

    @Override
    public String getTitle() {
        return superTypePage.getTitle();
    }

    @Override
    public String[] getClientLibCategories() {
        // FIXME: Custom hack for grabbing the clientlib from the design.  This should
        // FIXME: e updated to latest standard practices.
        String clientlib = currentDesign.getContentResource().getValueMap().get("clientLib", String.class);
        return StringUtils.isNotBlank(clientlib) ? new String[] { clientlib } : new String[0];
    }

    @Override
    public String getTemplateName() {
        return superTypePage.getTemplateName();
    }

    @Override
    public String getAppResourcesPath() {
        // FIXME: This is a complete hack to set the path for favicon icons that are not
        // FIXME: currently stored within the clientlibs in use.
        return currentDesign.getPath();
    }

    @Override
    public String getCssClassNames() {
        return superTypePage.getCssClassNames();
    }

    @Override
    public NavigationItem getRedirectTarget() {
        return superTypePage.getRedirectTarget();
    }

    @Override
    public boolean hasCloudconfigSupport() {
        return superTypePage.hasCloudconfigSupport();
    }

    @Override
    public String[] getExportedItemsOrder() {
        return superTypePage.getExportedItemsOrder();
    }

    @Override
    public Map<String, ? extends ComponentExporter> getExportedItems() {
        return superTypePage.getExportedItems();
    }

    @Override
    public String getExportedType() {
        return superTypePage.getExportedType();
    }
}
