package com.hs2solutions.aem.ondeployexamples.core.scripts;

import com.hs2solutions.aem.ondeploy.core.base.OnDeployScriptBase;
import com.hs2solutions.aem.ondeploy.core.models.DispatcherFlushReplicationConfig;
import com.hs2solutions.aem.ondeploy.core.models.PublishReplicationConfig;

import java.util.Arrays;
import java.util.Collections;

/**
 * Configure replication agents on local environment for purpose of
 * automated developer and QA setup.
 *
 * NOTE: These will only work if you have a run mode of "local"
 * on your local AEM servers.
 */
public class OnDeployExampleScriptConfigureLocalReplicationAgents extends OnDeployScriptBase {

    @Override
    protected void execute() throws Exception {
        // Set the URL for the publish agent on author and enable it
        configureReplicationPublishAgent(Collections.singletonList(
                new PublishReplicationConfig(Arrays.asList("local", "author"), "http://mylocalpublish:4503", true)
        ));
        // Enable the dispatcher flush agent on author, disable on publish
        configureReplicationDispatcherFlushAgent(Arrays.asList(
                new DispatcherFlushReplicationConfig(Arrays.asList("local", "author"), "http://mydispatch:80", true),
                new DispatcherFlushReplicationConfig(Arrays.asList("local", "publish"), "http://mydispatch:80", false)
        ));
    }
}
