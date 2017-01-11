package com.hs2solutions.aem.ondeploy.core.utils;

import org.apache.sling.settings.SlingSettingsService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * Utils for OSGi services.
 */
public abstract class ServiceUtil {
    protected static Logger logger = LoggerFactory.getLogger(ServiceUtil.class);
    /**
     * Get the run modes for the current AEM server.
     *
     * @return List of run modes
     */
    public static Set<String> getRunModes() {
        SlingSettingsService settings = getServiceRequired(SlingSettingsService.class);
        return settings.getRunModes();
    }

    /**
     * Get an OSGi service reference.
     *
     * Returns null if service implementation cannot be found.
     *
     * @param serviceInterface Service interface
     * @param <T> Service interface class
     * @return Service reference
     */
    @SuppressWarnings("unchecked")
    public static <T> T getService(Class<T> serviceInterface) {
        BundleContext bundleContext = FrameworkUtil.getBundle(serviceInterface).getBundleContext();
        ServiceReference factoryRef = bundleContext.getServiceReference(serviceInterface.getName());

        T service = null;
        if (factoryRef != null) {
            service = (T) bundleContext.getService(factoryRef);
        } else {
            logger.debug("No active references for service {} can be found", serviceInterface.getName());
        }

        return service;
    }

    /**
     * Get an OSGi service reference.
     *
     * Throws a runtime exception if service implementation cannot be found.
     *
     * @param serviceInterface Service interface
     * @param <T> Service interface class
     * @return Service reference
     */
    public static <T> T getServiceRequired(Class<T> serviceInterface) {
        T service = getService(serviceInterface);
        if (service == null) {
            throw new RuntimeException("No active references for service " + serviceInterface.getName() + " can be found");
        }
        return service;
    }

    /**
     * Determine if the current AEM server is running as an author instance.
     *
     * @return true if server is running as an AEM author server, else false
     */
    public static boolean runModeIsAuthor() {
        return getRunModes().contains("author");
    }

    /**
     * Determine if the current AEM server is running as an publish instance.
     *
     * @return true if server is running as an AEM publish server, else false
     */
    public static boolean runModeIsPublish() {
        return getRunModes().contains("publish");
    }
}