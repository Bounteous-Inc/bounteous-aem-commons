package com.bounteous.aem.compgenerator.javacodemodel;

import com.bounteous.aem.compgenerator.Constants;
import com.bounteous.aem.compgenerator.models.GenerationConfig;
import com.bounteous.aem.compgenerator.models.Property;
import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import com.bounteous.aem.compgenerator.utils.CommonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class JavaCodeBuilder {
    private static final Logger LOG = LogManager.getLogger(JavaCodeBuilder.class);

    protected final JCodeModel codeModel;
    protected final GenerationConfig generationConfig;
    protected final List<Property> globalProperties;
    protected final List<Property> sharedProperties;
    protected final List<Property> privateProperties;

    protected JavaCodeBuilder(JCodeModel codeModel, GenerationConfig generationConfig) {
        this.codeModel = codeModel;
        this.generationConfig = generationConfig;

        Set<Property> occurredProperties = new HashSet<>();

        this.globalProperties = filterProperties(occurredProperties, generationConfig.getOptions().getGlobalProperties());
        occurredProperties.addAll(this.globalProperties);

        this.sharedProperties = filterProperties(occurredProperties, generationConfig.getOptions().getSharedProperties());
        occurredProperties.addAll(this.sharedProperties);

        this.privateProperties = filterProperties(occurredProperties, generationConfig.getOptions().getProperties());
        occurredProperties.addAll(this.privateProperties);
    }

    /**
     * Filters the given properties for invalid fields and returns all that are not contained in occurredProperties.
     *
     * @param occurredProperties
     * @param originalProperties
     * @return filtered properties
     */
    private static List<Property> filterProperties(Set<Property> occurredProperties, List<Property> originalProperties) {
        List<Property> properties;
        if (originalProperties != null) {
            properties = originalProperties.stream()
                    .filter(Objects::nonNull)
                    .filter(property -> StringUtils.isNotBlank(property.getField()))
                    .filter(property -> StringUtils.isNotBlank(property.getType()))
                    .filter(property -> !(occurredProperties.contains(property)))
                    .collect(Collectors.toList());
        } else {
            properties = Collections.emptyList();
        }
        return properties;
    }
}
