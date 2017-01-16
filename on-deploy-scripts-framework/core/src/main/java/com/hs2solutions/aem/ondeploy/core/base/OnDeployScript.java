package com.hs2solutions.aem.ondeploy.core.base;

import com.day.cq.search.QueryBuilder;
import org.apache.sling.api.resource.ResourceResolver;

/**
 * A script that runs the first time it is deployed to an AEM server.
 */
public interface OnDeployScript {
    /**
     * Execute the script, passing in a resourceResolver and queryBuilder instance.
     *
     * @param resourceResolver Resource resolver.
     * @param queryBuilder Query builder.
     */
    void execute(ResourceResolver resourceResolver, QueryBuilder queryBuilder) throws Exception;
}
