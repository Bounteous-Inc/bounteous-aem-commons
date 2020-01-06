package aem.dam.core.datasource;

import com.day.cq.dam.api.Asset;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

public class SolutionPredicate implements Predicate {
    private final String solution;

    public SolutionPredicate(String solution) {
        this.solution = solution;
    }

    public boolean evaluate(Object object) {
        if (StringUtils.isBlank(this.solution)) {
            return true;
        }

        Resource resource = (Resource) object;
        Asset asset = resource.adaptTo(Asset.class);
        if (asset == null) {
            return true;
        }
        String solutionContext = (String) asset.getMetadata("dam:solutionContext");
        return StringUtils.equalsIgnoreCase(solutionContext, this.solution);
    }
}

