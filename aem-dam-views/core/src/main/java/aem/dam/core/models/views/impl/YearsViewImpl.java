package aem.dam.core.models.views.impl;

import aem.dam.core.models.views.View;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class YearsViewImpl extends AbstractViewImpl {
    private static final Logger LOGGER = LoggerFactory.getLogger(YearsViewImpl.class);

    private String yearKey;

    public YearsViewImpl(Resource requestedResource, Resource configResource,
            View parentView, String pathKey, ResourceResolver serviceResourceResolver) {
        super(requestedResource, configResource, parentView, serviceResourceResolver);
        this.yearKey = pathKey;
    }

    @Override
    protected Map<String, String> calculateViewFolders() {
        int specifiedNoOfYears = configResource.getValueMap().get("years", 10);
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        Map<String, String> childFolders = new HashMap<>();
        for (int i = 0; i < specifiedNoOfYears; i++) {
            String year = String.valueOf(currentYear - i);
            if (hasAtleastOneAsset(getYearFilter(year))) {
                childFolders.put(year, year);
            }
        }
        return childFolders;
    }

    @Override
    protected String getQueryFilterForThisLevelOnly() {
        return getYearFilter(yearKey);
    }

    private String getYearFilter(String year) {
        String filter = "[jcr:created] > CAST('%1$s' AS DATE) AND [jcr:created] < CAST('%2$s' AS DATE)";
        return String.format(filter, year + "-01-01T00:00:00.000Z", year + "-12-31T23:59:59.999Z");
    }

}
