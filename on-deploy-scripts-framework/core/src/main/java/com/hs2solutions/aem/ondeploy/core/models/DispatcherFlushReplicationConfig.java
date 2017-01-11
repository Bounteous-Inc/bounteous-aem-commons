package com.hs2solutions.aem.ondeploy.core.models;

import java.util.List;

/**
 * A dispatcher flush replication configuration that applies to a server that
 * matches all specified run modes.
 */
public class DispatcherFlushReplicationConfig {
    private List<String> runModes;
    private String dispatcherHost;
    private boolean enabled;

    /**
     * Standard ctor.
     *
     * @param runModes The run modes required on the server for this config to be applied.
     * @param dispatcherHost The host of the dispatcher server, including the http:// prefix and
     *                       port (e.g. http://localhost:8000).
     * @param enabled Whether the replication agent should be enabled or not.
     */
    public DispatcherFlushReplicationConfig(List<String> runModes, String dispatcherHost, boolean enabled) {
        this.runModes = runModes;
        this.dispatcherHost = dispatcherHost;
        this.enabled = enabled;
    }

    /**
     * Get the host of the dispatcher server.
     * @return Host of the dispatcher server.
     */
    public String getDispatcherHost() {
        return dispatcherHost;
    }

    /**
     * Get the required run modes for this configuration.
     * @return Required run modes for this configuration.
     */
    public List<String> getRunModes() {
        return runModes;
    }

    /**
     * Get whether or not the replication agent should be enabled.
     * @return Whether or not the replication agent should be enabled.
     */
    public boolean isEnabled() {
        return enabled;
    }
}
