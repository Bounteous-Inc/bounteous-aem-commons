package com.hs2solutions.aem.ondeployexamples.core.execute;

import com.hs2solutions.aem.ondeploy.core.base.OnDeployScript;
import com.hs2solutions.aem.ondeploy.core.execute.OnDeployScriptProvider;
import com.hs2solutions.aem.ondeployexamples.core.scripts.OnDeployExampleScriptConfigureLocalReplicationAgents;
import com.hs2solutions.aem.ondeployexamples.core.scripts.OnDeployExampleScriptCreateBusinessSpecificUserGroups;
import com.hs2solutions.aem.ondeployexamples.core.scripts.OnDeployExampleScriptDeleteObsoleteGeometrixxData;
import com.hs2solutions.aem.ondeployexamples.core.scripts.OnDeployExampleScriptMigrateGeometrixxTitleToCustomTitle;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import java.util.Arrays;
import java.util.List;

/**
 * Provider for the OnDeployExecutor service.
 *
 * Specifies a list of OnDeployScript instances to run.
 */
@Component(immediate = true)
@Service
@Properties({
        @Property(name = "service.description", value = "Developer service that identifies code scripts to run on deployment via the OnDeployExecutor service")
})
public class OnDeployExampleScriptsProviderImpl implements OnDeployScriptProvider {
    @Override
    public List<OnDeployScript> getScripts() {
        return Arrays.asList(
                new OnDeployExampleScriptConfigureLocalReplicationAgents(),
                new OnDeployExampleScriptCreateBusinessSpecificUserGroups(),
                new OnDeployExampleScriptDeleteObsoleteGeometrixxData(),
                new OnDeployExampleScriptMigrateGeometrixxTitleToCustomTitle()
        );
    }
}
