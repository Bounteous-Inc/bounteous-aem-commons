package aem.dam.core.datasource;

import com.adobe.granite.ui.components.ds.DataSource;
import com.day.cq.dam.commons.sort.ResourceSorter;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.jcr.Session;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

public class AssetsQueryDataSource implements DataSource {
    private static final String ORDER_BY = "orderby";
    private static final String ORDER_BY_SORT = "orderby.sort";


    protected final QueryBuilder queryBuilder;
    private final ResourceSorter resourceSorter;
    protected final Session session;
    protected final BaseParameters baseParams;
    private final QueryParameters queryParams;
    protected final Resource folder;

    public AssetsQueryDataSource(QueryBuilder queryBuilder, ResourceSorter resourceSorter,
            Resource folder, BaseParameters baseParams, QueryParameters queryParams) {

        this.queryBuilder = queryBuilder;
        this.resourceSorter = resourceSorter;
        this.session = folder.getResourceResolver().adaptTo(Session.class);
        this.folder = folder;
        this.baseParams = baseParams;
        this.queryParams = queryParams;
    }

    @Override
    public Iterator<Resource> iterator() {
        Comparator<Resource> comparator = null;
        String sortPredicate = null;
        boolean customTypePredicate = false;

        Map<String, String> map = new HashMap<>();
        if ((this.queryParams.map != null) && (!this.queryParams.map.isEmpty())) {
            for (Map.Entry<String, String[]> entry : this.queryParams.map.entrySet()) {
                map.put(entry.getKey(), entry.getValue()[0]);
                if (entry.getKey().equals("type")) {
                    customTypePredicate = true;
                }
            }
        } else {
            if (StringUtils.isNotBlank(this.queryParams.type)) {
                map.put("type", this.queryParams.type);
            } else if (this.queryParams.useTypeAsyncIndex) {
                map.put("group.1_type", "dam:Asset");
                map.put("group.2_type", "nt:folder");
                if (this.baseParams.includeCollections) {
                    map.put("group.3_type", "nt:unstructured");
                }
                map.put("group.p.or", "true");
            }
            map.put("path", this.folder.getPath());
            if (this.queryParams.directChildrenOnly) {
                map.put("path.flat", "true");
            }

            if (StringUtils.isNoneBlank(this.queryParams.propertyName, this.queryParams.propertyValue)) {
                map.put("property", "jcr:content/" + this.queryParams.propertyName);
                map.put("property.value", this.queryParams.propertyValue);
            }
            comparator = this.resourceSorter.getComparator(this.queryParams.orderBy);
            if (comparator != null) {
                sortPredicate = "--sort-" + this.queryParams.orderBy;
                map.put(ORDER_BY, sortPredicate);
                if ("desc".equalsIgnoreCase(this.queryParams.sort)) {
                    map.put(ORDER_BY_SORT, "desc");
                }
            } else {
                setOrderByClause(map, this.queryParams);
            }
        }
        map.put("boolproperty", "hidden");
        map.put("boolproperty.value", "false");
        if (!customTypePredicate) {
            map.put("--assetfolderitem", "true");
            if (this.baseParams.includeCollections) {
                map.put("--assetfolderitem.collections", "true");
            }
            if ((this.baseParams.exclude != null) && (!this.baseParams.exclude.isEmpty())) {
                map.put("--assetfolderitem.exclude", this.baseParams.exclude);
            }
            if ((this.baseParams.include != null) && (!this.baseParams.include.isEmpty())) {
                map.put("--assetfolderitem.include", this.baseParams.include);
            }
        }
        boolean solution = StringUtils.isNotBlank(this.baseParams.solution);
        boolean mimetype = ArrayUtils.isNotEmpty(this.baseParams.mimeTypes);

        if ((solution) || (mimetype)) {
            if (!customTypePredicate) {
                map.put("group.type", "nt:folder");
                map.put("group.p.or", "true");
                map.put("excludepaths", "(.*)?(jcr:content|rep:policy)(/.*)");
            }
            map.put("group.1_group.type", "dam:Asset");
        }
        if (solution) {
            map.put("group.1_group.1_property", "jcr:content/metadata/@dam:solutionContext");
            map.put("group.1_group.1_property.value", this.baseParams.solution);
        }
        if (mimetype) {
            map.put("group.1_group.2_group.p.or", "true");

            String group3 = "group.1_group.2_group.%d_property%s";

            for (int idx = 0; idx < this.baseParams.mimeTypes.length; idx++) {
                String mimeType = this.baseParams.mimeTypes[idx];

                map.put(String.format(group3, (idx+1), ""), "jcr:content/metadata/@dc:format");

                String mimeTypeLike = convertWildcardsToGlobPattern(mimeType);
                map.put(String.format(group3, (idx+1), ".value"), mimeTypeLike);
                if (!mimeType.equals(mimeTypeLike)) {
                    map.put(String.format(group3, (idx+1), ".operation"), "like");
                }
            }
        }
        map.put("p.guessTotal", "true");

        return query(map, comparator, sortPredicate);
    }

    private Iterator<Resource> query(Map<String, String> map, Comparator<Resource> comparator, String sortPredicate) {
        PredicateGroup root = PredicateGroup.create(map);
        Query query = this.queryBuilder.createQuery(root, this.session);

        query.registerPredicateEvaluator("--assetfolderitem", new AssetFolderItemPredicateEvaluator());
        if (comparator != null) {
            query.registerPredicateEvaluator(sortPredicate, new ComparatorPredicateEvaluator(comparator));
        }
        query.setStart(this.baseParams.offset);
        query.setHitsPerPage(this.baseParams.limit);

        SearchResult result = query.getResult();

        return result.getResources();
    }

    protected String convertWildcardsToGlobPattern(String term) {
        return StringUtils.replace(StringUtils.replace(term, "*", "%"),"?", "_");
    }

    private void setOrderByClause(Map<String, String> map, QueryParameters queryParams) {
        boolean invert = false;
        if ("desc".equalsIgnoreCase(queryParams.sort)) {
            map.put(ORDER_BY_SORT, "desc");
        }
        String columnName = queryParams.orderBy;
        if ("name".equals(columnName)) {
            map.put(ORDER_BY, "nodename");
        } else if ("expirystatus".equals(columnName)) {
            map.put(ORDER_BY, "@jcr:content/metadata/prism:expirationDate");
        } else if ("rating".equals(columnName)) {
            map.put(ORDER_BY, "@jcr:content/averageRating");
            invert = true;
        } else {
            map.put(ORDER_BY, "@jcr:created");
            map.put(ORDER_BY_SORT, "desc");
        }
        if (invert) {
            if ("desc".equalsIgnoreCase(queryParams.sort)) {
                map.remove(ORDER_BY_SORT);
            } else {
                map.put(ORDER_BY_SORT, "desc");
            }
        }
    }
}

