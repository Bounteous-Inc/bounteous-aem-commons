package com.hs2solutions.aem.ondeploy.core.execute;

import com.hs2solutions.aem.ondeploy.core.base.OnDeployScript;

import java.util.List;

/**
 * Provider interface to supply a list of scripts to be run by the OnDeployExecutor.
 */
public interface OnDeployScriptProvider {
    /**
     * Get the list of scripts to run.
     *
     * Ideally, all scripts will remain in the list indefinitely, so that a new AEM
     * server can run all scripts from the first to the last to be
     * completely up to date w/no manual intervention.  As a reminder, scripts will
     * run only once, so it is safe to preserve the entire list of scripts.
     *
     * @return List of OnDeployScript instances.
     */
    List<OnDeployScript> getScripts();
}
