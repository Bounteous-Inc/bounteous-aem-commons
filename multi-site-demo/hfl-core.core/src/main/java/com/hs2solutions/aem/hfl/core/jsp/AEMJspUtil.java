package com.hs2solutions.aem.hfl.core.jsp;

import com.adobe.acs.commons.wcm.PageRootProvider;
import com.day.cq.wcm.api.Page;
import com.hs2solutions.aem.hfl.core.util.ServiceUtil;
import org.apache.sling.api.resource.Resource;
import tldgen.Function;

/**
 * Utility EL functions specific to AEM.
 *
 * This class should contain very basic coding utils, not in any way related
 * to client business rules.
 */
public abstract class AEMJspUtil {
    /**
     * Get the Home Page for a given resource.
     */
    @Function
    public static Page getHomePagePath(Resource resource) {
        PageRootProvider pageRootProvider = ServiceUtil.getService(PageRootProvider.class);
        return pageRootProvider != null ? pageRootProvider.getRootPage(resource) : null;
    }
}
