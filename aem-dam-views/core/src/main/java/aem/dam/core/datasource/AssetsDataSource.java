package aem.dam.core.datasource;

import com.adobe.granite.ui.components.PagingIterator;
import com.adobe.granite.ui.components.ds.DataSource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.functors.AllPredicate;
import org.apache.commons.collections.iterators.FilterIterator;
import org.apache.sling.api.resource.Resource;

public class AssetsDataSource implements DataSource {
    private final DataSource inDataSource;
    private final BaseParameters params;

    public AssetsDataSource(DataSource inDataSource, BaseParameters params) {
        this.inDataSource = inDataSource;
        this.params = params;
    }

    public Iterator<Resource> iterator() {
        AllPredicate allPredicates = new AllPredicate(new Predicate[]{
                new HiddenItemPredicate(),
                new AssetFolderItemPredicate(this.params.includeCollections, this.params.exclude, this.params.include),
                new MimeTypePredicate(this.params.mimeTypes),
                new SolutionPredicate(this.params.solution)
        });
        Iterator filteredIter = new FilterIterator(
                this.inDataSource.iterator(),
                allPredicates
        );
        List<Resource> filteredResources = new ArrayList<>();
        while (filteredIter.hasNext()) {
            Object obj = filteredIter.next();
            if ((obj instanceof Resource)) {
                filteredResources.add((Resource)obj);
            }
        }

        return new PagingIterator<>(filteredResources.iterator(), this.params.offset, this.params.limit);
    }
}

