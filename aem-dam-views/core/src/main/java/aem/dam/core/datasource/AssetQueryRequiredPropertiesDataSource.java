package aem.dam.core.datasource;

import com.day.cq.dam.commons.sort.ResourceSorter;
import com.day.cq.search.QueryBuilder;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.result.SearchResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.sling.api.resource.Resource;

public class AssetQueryRequiredPropertiesDataSource extends AssetsQueryDataSource {

    public AssetQueryRequiredPropertiesDataSource(QueryBuilder queryBuilder, ResourceSorter resourceSorter,
            Resource folder, BaseParameters baseParams, QueryParameters queryParams) {

        super(queryBuilder, resourceSorter, folder, baseParams, queryParams);
    }

    public Iterator<Resource> iterator() {
        SearchResult result = null;
        ArrayList<Resource> assetsList = new ArrayList<>();

        int groupIdx = 1;

        Map<String, String> map = new HashMap<>();

        map.put("path", this.folder.getPath());
        map.put("path.flat", "true");
        map.put("type", "nt:folder");
        map.put("orderby", "@jcr:created");
        map.put("orderby.sort", "desc");

        map.put("boolproperty", "hidden");
        map.put("boolproperty.value", "false");

        map.put("p.guessTotal", "true");

        PredicateGroup root = PredicateGroup.create(map);
        Query query = this.queryBuilder.createQuery(root, this.session);

        query.setStart(this.baseParams.offset);
        query.setHitsPerPage(this.baseParams.limit);

        result = query.getResult();
        Resource res;
        if (result != null) {
            Iterator<Resource> it = result.getResources();
            while (it.hasNext()) {
                res = (Resource) it.next();
                assetsList.add(res);
            }
        }
        map = new HashMap();
        map.put("path", this.folder.getPath());
        map.put("path.flat", "true");
        map.put("type", "dam:Asset");
        map.put("p.limit", "-1");
        if ((this.baseParams.mimeTypes != null) && (this.baseParams.mimeTypes.length > 0)) {
            String dcFormatField = "metadata/@dc:format";
            if (this.baseParams.mimeTypes.length < 2) {
                this.baseParams.mimeTypes[0] = (dcFormatField + "=" + this.baseParams.mimeTypes[0]);

                this.baseParams.requiredProperties = (this.baseParams.requiredProperties == null ? this.baseParams.mimeTypes : (String[]) ArrayUtils.addAll(this.baseParams.requiredProperties, this.baseParams.mimeTypes));
            } else {
                map.put("group.p.or", "true");
                for (String mimeType : this.baseParams.mimeTypes) {
                    map.put("group." + groupIdx + "_property", "jcr:content/" + dcFormatField);

                    String mimeTypeLike = convertWildcardsToGlobPattern(mimeType);
                    map.put("group." + groupIdx + "_property.value", mimeTypeLike);
                    if (!mimeType.equals(mimeTypeLike)) {
                        map.put("group." + groupIdx + "_property.operation", "like");
                    }
                    groupIdx++;
                }
            }
        }
        if ((this.baseParams.requiredProperties != null) && (this.baseParams.requiredProperties.length > 0)) {
            groupIdx = 1;
            for (int i = 0; i < this.baseParams.requiredProperties.length; i++) {
                if (this.baseParams.requiredProperties[i] != null) {
                    String[] propInfo = this.baseParams.requiredProperties[i].split("=");
                    String prop = propInfo[0];
                    String propVal = propInfo.length > 1 ? propInfo[1] : "";
                    if (propVal.length() > 0) {
                        String propLike = convertWildcardsToGlobPattern(propVal);

                        map.put(groupIdx + "_property", "jcr:content/" + prop);
                        map.put(groupIdx + "_property.value", propLike);
                        if (!propVal.equals(propLike)) {
                            map.put(groupIdx + "_property.operation", "like");
                        }
                    } else {
                        map.put(groupIdx + "_property", "jcr:content/" + prop);
                        map.put(groupIdx + "_property.operation", "exists");
                        map.put(groupIdx + "_property.value", "true");
                    }
                    groupIdx++;
                }
            }
        }
        root = PredicateGroup.create(map);
        query = this.queryBuilder.createQuery(root, this.session);
        query.setStart(this.baseParams.offset);
        query.setHitsPerPage(this.baseParams.limit);
        result = query.getResult();
        if (result != null) {
            Iterator<Resource> it = result.getResources();
            while (it.hasNext()) {
                Resource resource1 = (Resource) it.next();
                assetsList.add(resource1);
            }
        }
        return assetsList.iterator();
    }
}

