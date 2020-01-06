package aem.dam.core.datasource;

import com.adobe.granite.ui.components.ds.DataSource;

import java.util.Iterator;

import org.apache.sling.api.resource.Resource;

public class FolderListingDataSource implements DataSource {
    private final Resource resource;

    public FolderListingDataSource(Resource resource) {
        this.resource = resource;
    }

    @Override
    public Iterator<Resource> iterator() {
        return this.resource.listChildren();
    }
}
