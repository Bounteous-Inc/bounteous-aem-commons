package com.hs2solutions.aem.ondeploy.core.execute;

public class OnDeployEarlyTerminationException extends RuntimeException {
    public OnDeployEarlyTerminationException(Throwable cause) {
        super("On-deploy scripts terminated due to a fatal error. One or more on-deploy scripts have not been run, and will not be run again until next deployment.", cause);
    }
}
