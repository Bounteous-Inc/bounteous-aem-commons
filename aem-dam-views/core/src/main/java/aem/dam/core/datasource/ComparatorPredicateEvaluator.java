package aem.dam.core.datasource;

import com.day.cq.search.Predicate;
import com.day.cq.search.eval.AbstractPredicateEvaluator;
import com.day.cq.search.eval.EvaluationContext;

import java.util.Comparator;
import javax.jcr.query.Row;

import org.apache.sling.api.resource.Resource;

public class ComparatorPredicateEvaluator extends AbstractPredicateEvaluator {
    private final Comparator<Resource> resourceComparator;

    public ComparatorPredicateEvaluator(Comparator<Resource> resourceComparator) {
        this.resourceComparator = resourceComparator;
    }

    @Override
    public Comparator<Row> getOrderByComparator(Predicate predicate, final EvaluationContext context) {
        return  (o1, o2) -> ComparatorPredicateEvaluator.this.resourceComparator.compare(context.getResource(o1), context.getResource(o2));
    }
}

