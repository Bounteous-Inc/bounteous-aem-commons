package aem.dam.core.datasource;

import com.adobe.granite.ui.components.ExpressionResolver;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;

public class ExValueMap extends ValueMapDecorator {
    private final SlingHttpServletRequest request;
    private final ExpressionResolver resolver;

    public ExValueMap(SlingHttpServletRequest request, ExpressionResolver resolver, ValueMap base) {
        super(base);

        this.request = request;
        this.resolver = resolver;
    }

    public <T> T getEx(String property, Class<T> expectedType) {
        String value = get(property, String.class);
        if (value == null) {
            return null;
        }
        return this.resolver.resolve(value, this.request.getLocale(), expectedType, this.request);
    }

    public <T> T getEx(String property, String defaultExpression, Class<T> expectedType) {
        String value = get(property, defaultExpression);
        return this.resolver.resolve(value, this.request.getLocale(), expectedType, this.request);
    }
}

