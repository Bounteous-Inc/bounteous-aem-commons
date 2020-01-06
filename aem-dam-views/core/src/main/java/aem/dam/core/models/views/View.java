package aem.dam.core.models.views;

import org.apache.sling.api.resource.Resource;

import java.util.List;

public interface View {
    List<Resource> getItems();

    String getQueryFilter();
}
