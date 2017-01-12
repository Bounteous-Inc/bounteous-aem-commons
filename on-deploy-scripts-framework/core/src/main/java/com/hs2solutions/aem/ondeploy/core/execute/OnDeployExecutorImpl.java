package com.hs2solutions.aem.ondeploy.core.execute;

import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.search.QueryBuilder;
import com.hs2solutions.aem.ondeploy.core.base.OnDeployScript;
import com.hs2solutions.aem.ondeploy.core.utils.ResourceUtil;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.osgi.framework.Constants.SERVICE_DESCRIPTION;
import static org.osgi.framework.Constants.SERVICE_VENDOR;

/**
 * A service that triggers scripts on deployment to an AEM server.
 * <p>
 * This class manages scripts so that they only run once (unless the script
 * fails).  Script execution statuses are stored in the JCR @
 * /var/hs2solutions/on-deploy-scripts-status.
 * <p>
 * Scripts are specified by implementing a OnDeployScriptProvider service that
 * returns a list of OnDeployScript instances.
 * <p>
 * NOTE: Since it's always a possibility that
 * /var/hs2solutions/on-deploy-scripts-status will be deleted in the JCR,
 * scripts should be written defensively in case they are actually run more
 * than once.  This also covers the scenario where a script is run a second
 * time after failing the first time.
 */
@Component(immediate = true)
@Service
@Properties({
        @Property(name = SERVICE_VENDOR, value = "HS2 Solutions"),
        @Property(name = SERVICE_DESCRIPTION, value = "Developer tool that triggers scripts (specified via an implementation of OnDeployScriptProvider) to execute on deployment.")
})
public class OnDeployExecutorImpl implements OnDeployExecutor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Reference
    private QueryBuilder queryBuilder;
    @Reference
    private ResourceResolverFactory resourceResolverFactory;
    @Reference
    private OnDeployScriptProvider scriptProvider;

    /**
     * Executes all on-deploy scripts on activation of this service.
     *
     * @param properties OSGi properties for this service (unused).
     */
    @Activate
    protected final void activate(final Map<String, String> properties) {
        List<OnDeployScript> scripts = scriptProvider.getScripts();
        if (scripts.size() == 0) {
            logger.trace("On-deploy scripts are not implemented.");
            return;
        }
        ResourceResolver resourceResolver = null;
        Session session = null;
        try {
            try {
                resourceResolver = logIn();
            } catch (LoginException le1) {
                logger.info("On-deploy scripts cannot log in with the appropriate service user...waiting 5 seconds to try again in case AEM is in the process of deploying the service user...");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    // no op
                }
                try {
                    resourceResolver = logIn();
                } catch (LoginException le2) {
                    logger.error("On-deploy scripts cannot be run because the system cannot log in with the appropriate service user");
                    throw new OnDeployEarlyTerminationException(le2);
                }
            }
            session = resourceResolver.adaptTo(Session.class);
            runScripts(resourceResolver, session, scripts);
        } finally {
            if (session != null) {
                try {
                    session.logout();
                } catch (Exception e) {
                    logger.warn("Failed session.logout()", e);
                }
            }
            if (resourceResolver != null) {
                try {
                    resourceResolver.close();
                } catch (Exception e) {
                    logger.warn("Failed resourceResolver.close()", e);
                }
            }
        }
    }

    protected Node getOrCreateStatusTrackingNode(Session session, String statusNodePath) {
        try {
            return JcrUtil.createPath(statusNodePath, "nt:unstructured", "nt:unstructured", session, false);
        } catch (RepositoryException re) {
            logger.error("On-deploy script cannot be run because the system could not find or create the script status node: {}", statusNodePath);
            throw new OnDeployEarlyTerminationException(re);
        }
    }

    protected String getScriptStatus(ResourceResolver resourceResolver, Node statusNode, String statusNodePath) {
        try {
            Resource resource = resourceResolver.getResource(statusNode.getPath());
            return ResourceUtil.getPropertyString(resource, "status");
        } catch (RepositoryException re) {
            logger.error("On-deploy script cannot be run because the system read the script status node: {}", statusNodePath);
            throw new OnDeployEarlyTerminationException(re);
        }
    }

    private ResourceResolver logIn() throws LoginException {
        Map<String, Object> userParams = new HashMap<>();
        userParams.put(ResourceResolverFactory.SUBSERVICE, "onDeployScripts");
        return resourceResolverFactory.getServiceResourceResolver(userParams);
    }

    protected void runScript(ResourceResolver resourceResolver, Session session, OnDeployScript script) {
        String statusNodePath = "/var/hs2solutions/on-deploy-scripts-status/" + script.getClass().getName();
        Node statusNode = getOrCreateStatusTrackingNode(session, statusNodePath);
        String status = getScriptStatus(resourceResolver, statusNode, statusNodePath);
        if (status == null || status.equals("fail")) {
            trackScriptStart(session, statusNode, statusNodePath);
            try {
                script.execute(resourceResolver, queryBuilder);
                logger.info("On-deploy script completed successfully: {}", statusNodePath);
                trackScriptEnd(session, statusNode, statusNodePath, "success");
            } catch (Exception e) {
                logger.error("On-deploy script failed: {}", statusNodePath, e);
                trackScriptEnd(session, statusNode, statusNodePath, "fail");
            }
        } else if (!status.equals("success")) {
            logger.warn("Skipping on-deploy script as it may already be in progress: {} - status: {}", statusNodePath, status);
        } else {
            logger.debug("Skipping on-deploy script, as it is already complete: {}", statusNodePath);
        }
    }

    protected void runScripts(ResourceResolver resourceResolver, Session session, List<OnDeployScript> scripts) {
        for (OnDeployScript script : scripts) {
            runScript(resourceResolver, session, script);
        }
    }

    protected void trackScriptEnd(Session session, Node statusNode, String statusNodePath, String status) {
        try {
            statusNode.setProperty("status", status);
            statusNode.setProperty("endDate", Calendar.getInstance());
            session.save();
        } catch (RepositoryException e) {
            logger.warn("On-deploy script status node could not be updated: {} - status: {}", statusNodePath, status);
        }
    }

    protected void trackScriptStart(Session session, Node statusNode, String statusNodePath) {
        logger.info("Starting on-deploy script: {}", statusNodePath);
        try {
            statusNode.setProperty("status", "running");
            statusNode.setProperty("startDate", Calendar.getInstance());
            statusNode.setProperty("endDate", (Calendar) null);
            session.save();
        } catch (RepositoryException e) {
            logger.error("On-deploy script cannot be run because the system could not write to the script status node: {}", statusNodePath);
            throw new OnDeployEarlyTerminationException(e);
        }
    }
}
