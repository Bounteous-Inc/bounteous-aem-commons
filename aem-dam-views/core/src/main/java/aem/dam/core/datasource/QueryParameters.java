package aem.dam.core.datasource;

import java.util.Map;

public class QueryParameters {
    public Map<String, String[]> map;
    public boolean directChildrenOnly;
    public String propertyName;
    public String propertyValue;
    public String orderBy;
    public String sort;
    public String type;
    public boolean useTypeAsyncIndex;
}
