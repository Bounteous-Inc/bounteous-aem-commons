package aem.dam.core.models.views;

import aem.dam.core.models.views.impl.AssetsViewImpl;
import aem.dam.core.models.views.impl.MonthsViewImpl;
import aem.dam.core.models.views.impl.TagViewImpl;
import aem.dam.core.models.views.impl.YearsViewImpl;
import java.util.HashMap;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
        service = ViewService.class
)
public class ViewService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ViewService.class);

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    public List<Resource> getItems(Resource requestedResource, LinkedHashMap<String, Resource> viewConfigs) {
        Map<String, Object> param = new HashMap<>();
        param.put(ResourceResolverFactory.SUBSERVICE, "dam-views-service");
        ResourceResolver serviceResourceResolver = null;
        try {
            serviceResourceResolver = resourceResolverFactory.getServiceResourceResolver(param);
            View view = buildView(requestedResource, viewConfigs, serviceResourceResolver);
            return view.getItems();
        } catch (LoginException e) {
            throw new RuntimeException("Could not get ResourceResolver from configured service user - aborting.", e);
        } finally {
            if (serviceResourceResolver != null) {
                serviceResourceResolver.close();
            }
        }
    }

    private View buildView(Resource requestedResource, LinkedHashMap<String, Resource> viewConfigs, ResourceResolver serviceResourceResolver) {
        View currentView = null;
        String yearKey = null;

        for (Map.Entry<String, Resource> viewConfigEntry : viewConfigs.entrySet()) {
            String pathKey = viewConfigEntry.getKey().replace("/", "");
            Resource viewConfig = viewConfigEntry.getValue();
            ValueMap viewConfigMap = viewConfig.getValueMap();
            String resultType = viewConfigMap.get("resultType", "");
            String groupType = viewConfigMap.get("groupType", "");

            if (StringUtils.equalsIgnoreCase(resultType, "asset")) {
                currentView = new AssetsViewImpl(requestedResource, viewConfig, currentView, serviceResourceResolver);
            } else if (StringUtils.equalsIgnoreCase(resultType, "folders")) {
                if (StringUtils.equalsIgnoreCase(groupType, "tag")) {
                    currentView = new TagViewImpl(requestedResource, viewConfig, currentView, pathKey, serviceResourceResolver);
                } else if (StringUtils.equalsIgnoreCase(groupType, "year")) {
                    yearKey = pathKey;
                    currentView = new YearsViewImpl(requestedResource, viewConfig, currentView, pathKey, serviceResourceResolver);
                } else if (StringUtils.equalsIgnoreCase(groupType, "month")) {
                    if (yearKey == null) {
                        throw new UnsupportedOperationException("Invalid view config. Year filter must precede month filter.");
                    }

                    currentView = new MonthsViewImpl(requestedResource, viewConfig, currentView, pathKey, yearKey, serviceResourceResolver);
                } else {
                    throw new UnsupportedOperationException("Invalid view config. (groupType: '" + groupType + "', resultType: '" + resultType + "') combination is not supported.");
                }
            } else {
                throw new UnsupportedOperationException("Invalid view config. (Result: '" + resultType + "') is not supported.");
            }
        }

        if (currentView == null) {
            throw new UnsupportedOperationException("Invalid view config. No configs found.");
        } else {
            LOGGER.debug("View path: {} type: {}", requestedResource.getPath(), currentView.getClass().getName());
        }

        return currentView;
    }
}
