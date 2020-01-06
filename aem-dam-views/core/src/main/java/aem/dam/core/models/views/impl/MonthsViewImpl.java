package aem.dam.core.models.views.impl;

import aem.dam.core.models.views.View;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Month;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MonthsViewImpl extends AbstractViewImpl {
    private static final Logger LOGGER = LoggerFactory.getLogger(MonthsViewImpl.class);
    private static final String[] MONTH_STRINGS = new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" };

    private String monthKey;
    private String yearKey;

    public MonthsViewImpl(Resource requestedResource, Resource configResource,
            View parentView, String pathKey, String yearKey, ResourceResolver serviceResourceResolver) {

        super(requestedResource, configResource, parentView, serviceResourceResolver);
        this.monthKey = pathKey;
        this.yearKey = yearKey;
    }

    @Override
    protected Map<String, String> calculateViewFolders() {
        Map<String, String> childFolders = new HashMap<>();
        for (String monthStr : MONTH_STRINGS) {
            if (hasAtleastOneAsset(getMonthFilter(monthStr))) {
                childFolders.put(monthStr, getMonthName(Integer.parseInt(monthStr)));
            }
        }
        return childFolders;
    }

    @Override
    protected String getQueryFilterForThisLevelOnly() {
        return getMonthFilter(monthKey);
    }

    private String getMonthFilter(String month) {
        String filter = "[jcr:created] > CAST('%1$s' AS DATE) AND [jcr:created] < CAST('%2$s' AS DATE)";
        String monthEndDay = getEndDateValue(Integer.valueOf(month));
        return String.format(filter, yearKey + "-" + month + "-01T00:00:00.000Z", yearKey + "-" + month + "-" + monthEndDay + "T23:59:59.999Z");
    }

    private String getEndDateValue(Integer month) {
        return "" + YearMonth.of(Integer.parseInt(yearKey), month).lengthOfMonth();
    }

    private String getMonthName(Integer month) {
        return Month.of(month).getDisplayName(TextStyle.FULL, Locale.getDefault());
    }
}
