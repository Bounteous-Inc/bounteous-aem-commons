package com.hs2solutions.aem.ondeployexamples.core.scripts;

import com.hs2solutions.aem.ondeploy.core.base.OnDeployScriptBase;
import org.apache.jackrabbit.api.security.user.Group;

/**
 * Create business-specific user groups and add as members to appropriate groups
 * needed to do their job.
 */
public class OnDeployExampleScriptCreateBusinessSpecificUserGroups extends OnDeployScriptBase {
    @Override
    protected void execute() throws Exception {
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
