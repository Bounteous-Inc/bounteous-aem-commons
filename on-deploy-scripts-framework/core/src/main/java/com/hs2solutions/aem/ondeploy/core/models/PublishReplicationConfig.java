package com.hs2solutions.aem.ondeploy.core.models;

import java.util.List;

/**
 * A publish replication configuration that applies to a server that
 * matches all specified run modes.
 */
public class PublishReplicationConfig {
    private List<String> runModes;
    private String publishHost;
    private boolean enabled;

    /**
     * Standard ctor.
     *
     * @param runModes The run modes required on the server for this config to be applied.
     * @param publishHost The host of the publish server, including the http:// prefix and
     *                    port (e.g. http://localhost:4503).
     * @param enabled Whether the replication agent should be enabled or not.
     */
    public PublishReplicationConfig(List<String> runModes, String publishHost, boolean enabled) {
        this.runModes = runModes;
        this.publishHost = publishHost;
        this.enabled = enabled;
    }

    /**
     * Get the host of the publish server.
     * @return Host of the publish server.
     */
    public String getPublishHost() {
        return publishHost;
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
