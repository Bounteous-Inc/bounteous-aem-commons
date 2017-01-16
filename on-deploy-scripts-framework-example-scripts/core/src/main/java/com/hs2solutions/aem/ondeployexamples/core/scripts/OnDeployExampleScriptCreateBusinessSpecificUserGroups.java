package com.hs2solutions.aem.ondeployexamples.core.scripts;

import com.hs2solutions.aem.ondeploy.core.base.OnDeployScriptBase;
import com.hs2solutions.aem.ondeploy.core.utils.ServiceUtil;
import org.apache.jackrabbit.api.security.user.Group;

/**
 * Create business-specific user groups and add as members to appropriate groups
 * needed to do their job.
 *
 * NOTE: This script will execute its tasks only on an author server due to the
 * check for ServiceUtil.runModeIsAuthor().  The script will "run" on the publish
 * server, but skip the actual steps that create the user groups.
 */
public class OnDeployExampleScriptCreateBusinessSpecificUserGroups extends OnDeployScriptBase {
    @Override
    protected void execute() throws Exception {
        if (ServiceUtil.runModeIsAuthor()) {
            Group regionalApprovers = createUserGroup("regionalapprover", "mybiz");
            if (regionalApprovers != null) {
                addUserGroupMember("content-authors", "regionalapprover");
                addUserGroupMember("workflow-users", "regionalapprover");
            }
            Group globalApprovers = createUserGroup("globalapprover", "mybiz");
            if (globalApprovers != null) {
                addUserGroupMember("regionalapprover", "globalapprover");
            }
        }
    }
}
