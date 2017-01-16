package com.hs2solutions.aem.ondeployexamples.core.scripts;

import com.hs2solutions.aem.ondeploy.core.base.OnDeployScriptBase;

/**
 * Update all instances of "geometrixx/components/title" under /content
 * to a new, custom resource type.
 */
public class OnDeployExampleScriptMigrateGeometrixxTitleToCustomTitle extends OnDeployScriptBase {
    @Override
    protected void execute() throws Exception {
        searchAndUpdateResourceType("geometrixx/components/title", "on-deploy-scripts-framework-example-scripts/components/customtitle");
    }
}
