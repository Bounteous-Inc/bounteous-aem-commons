package aem.dam.core.datasource;

import com.day.cq.dam.api.Asset;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.Predicate;
import org.apache.sling.api.resource.Resource;

public class MimeTypePredicate implements Predicate {
    private final Pattern[] mimeTypePatterns;

    public MimeTypePredicate(String[] mimeTypes) {
        if ((mimeTypes == null) || (mimeTypes.length == 0)) {
            this.mimeTypePatterns = null;
        } else {
            this.mimeTypePatterns = new Pattern[mimeTypes.length];
            for (int i = 0; i < mimeTypes.length; i++) {
                if (mimeTypes[i].isEmpty()) {
                    this.mimeTypePatterns[i] = null;
                } else {
                    this.mimeTypePatterns[i] = Pattern.compile(convertWildcardToRegex(mimeTypes[i]));
                }
            }
        }
    }

    private String convertWildcardToRegex(String term) {
        return term.replaceAll("[.]", "[$0]").replaceAll("[*]", ".*").replaceAll("[?]", ".");
    }

    @Override
    public boolean evaluate(Object object) {
        if (this.mimeTypePatterns == null) {
            return true;
        }
        Resource resource = (Resource) object;
        Asset asset = resource.adaptTo(Asset.class);
        if (asset == null) {
            return true;
        }
        String mimeType = asset.getMimeType();
        if (mimeType == null) {
            return false;
        }
        for (Pattern pattern : this.mimeTypePatterns) {
            if ((pattern == null) || (pattern.matcher(mimeType).matches())) {
                return true;
            }
        }
        return false;
    }
}

