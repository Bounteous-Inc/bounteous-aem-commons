package aem.dam.core.datasource;

import com.adobe.granite.ui.components.ds.DataSource;

import java.util.Iterator;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.NotNullPredicate;
import org.apache.commons.collections.iterators.ArrayIterator;
import org.apache.commons.collections.iterators.FilterIterator;
import org.apache.commons.collections.iterators.TransformIterator;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

public class FixedPathsDataSource implements DataSource {
    private final ResourceResolver resolver;
    private final String[] paths;

    public FixedPathsDataSource(ResourceResolver resolver, String[] paths) {
        this.resolver = resolver;
        this.paths = paths;
    }

    public Iterator<Resource> iterator() {
        return new FilterIterator(new TransformIterator(new ArrayIterator(this.paths), new Transformer() {
            public Object transform(Object input) {
                String path = (String) input;
                return FixedPathsDataSource.this.resolver.getResource(path);
            }
        }), NotNullPredicate.getInstance());
    }
}

