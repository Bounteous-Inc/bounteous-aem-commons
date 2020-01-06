package aem.dam.core.datasource;

import com.day.cq.search.Predicate;
import com.day.cq.search.eval.EvaluationContext;

import javax.jcr.query.Row;

public class AssetFolderItemPredicateEvaluator extends FilteringPredicateEvaluator {
    private static final String COLLECTIONS = "collections";
    private static final String EXCLUDE = "exclude";
    private static final String INCLUDE = "include";
    private AssetFolderItemPredicate pred = new AssetFolderItemPredicate(false, null, null);

    @Override
    public boolean includes(Predicate p, Row row, EvaluationContext context) {
        this.pred.setIncludeCollections(p.getBool(COLLECTIONS));
        this.pred.setExclude(p.get(EXCLUDE));
        this.pred.setInclude(p.get(INCLUDE));

        return this.pred.evaluate(context.getResource(row));
    }
}
