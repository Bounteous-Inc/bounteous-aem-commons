package aem.dam.core.models.views.impl;

import aem.dam.core.models.views.View;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class TagViewImpl extends AbstractViewImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(TagViewImpl.class);

    private String tagKey;
    private LinkedHashMap<String, Tag> tagsMap;

    public TagViewImpl(Resource requestedResource, Resource configResource,
            View parentView, String pathKey, ResourceResolver serviceResourceResolver) {

        super(requestedResource, configResource, parentView, serviceResourceResolver);
        this.tagKey = pathKey;
        calculateTagsMap();
    }

    @Override
    protected Map<String, String> calculateViewFolders() {
        Map<String, String> childFolders = new HashMap<>();
        for (Tag configuredTag : tagsMap.values()) {
            if (hasAtleastOneAsset(getTagFilter(configuredTag.getTagID()))) {
                childFolders.put(configuredTag.getName(), configuredTag.getTitle());
            }
        }
        return childFolders;
    }

    @Override
    protected String getQueryFilterForThisLevelOnly() {
        Tag queryTag = tagsMap.get(tagKey);
        String tagId;
        if (queryTag != null) {
            tagId = queryTag.getTagID();
        } else {
            tagId = "#invalidtag#";
            LOGGER.warn("Path token cannot be mapped to a configured tag: {}", tagKey);
        }
        return getTagFilter(tagId);
    }

    private String getTagFilter(String tagId) {
        return "[jcr:content/metadata/cq:tags] = '" + tagId + "'";
    }

    private void calculateTagsMap() {
        TagManager tagManager = serviceResourceResolver.adaptTo(TagManager.class);
        tagsMap = new LinkedHashMap<>();
        String[] configuredTagIds = configResource.getValueMap().get("tags", new String[]{});
        for (String configuredTagId : configuredTagIds) {
            Tag configuredTag = tagManager.resolve(configuredTagId);
            if (configuredTag != null) {
                Iterator<Tag> tagIterator = configuredTag.listChildren();
                if (tagIterator.hasNext()) {
                    while (tagIterator.hasNext()) {
                        addTagToMap(tagIterator.next());
                    }
                } else {
                    addTagToMap(configuredTag);
                }
            }
        }
    }

    private void addTagToMap(Tag tag) {
        String key = tag.getName();
        if (tagsMap.keySet().contains(key)) {
            if (key.equals(tagKey)) {
                LOGGER.warn("Tag key '{}' matches multiple configured tags but will only map to a single one: {}", tagKey, tagsMap.get(key).getTagID());
            } else {
                LOGGER.debug("Configured tag '{}' conflicts with another tag of same name: {} - only the latter will have a view folder", tag.getTagID(), tagsMap.get(key).getTagID());
            }
        } else {
            tagsMap.put(key, tag);
        }
    }
}

