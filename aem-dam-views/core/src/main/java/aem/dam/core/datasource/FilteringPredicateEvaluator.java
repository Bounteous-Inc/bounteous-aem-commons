package aem.dam.core.datasource;

import com.day.cq.search.Predicate;
import com.day.cq.search.eval.AbstractPredicateEvaluator;
import com.day.cq.search.eval.EvaluationContext;

public class FilteringPredicateEvaluator extends AbstractPredicateEvaluator {
    @Override
    public boolean canXpath(Predicate predicate, EvaluationContext context) {
        return false;
    }

    @Override
    public boolean canFilter(Predicate predicate, EvaluationContext context) {
        return true;
    }
}
