package com.hs2solutions.aem.hfl.core.util;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utils for OSGi services.
 */
public abstract class ServiceUtil {
    protected static Logger logger = LoggerFactory.getLogger(ServiceUtil.class);

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
}