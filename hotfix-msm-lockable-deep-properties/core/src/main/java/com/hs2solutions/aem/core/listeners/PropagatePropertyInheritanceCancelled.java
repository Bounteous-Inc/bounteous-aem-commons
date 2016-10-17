package com.hs2solutions.aem.core.listeners;

import com.day.cq.commons.jcr.JcrUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.ObservationManager;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This is an event listener to work around a limitation in AEM where
 * the MSM cq:propertyInheritanceCancelled property on a page does not
 * work for "deep" properties (properties with a name that has one or
 * more '/' in it).  This workaround propagates the
 * cq:propertyInheritanceCancelled values for deep properties down to
 * the appropriate child node so that breaking inheritance will work
 * on those properties.
 *
 * @author HS2 Solutions, Inc
 */
@Component(immediate = true)
@Service
public class PropagatePropertyInheritanceCancelled implements EventListener {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private final String CANCELLED_INHERITANCE_PROPERTY = "cq:propertyInheritanceCancelled";
    private final int CANCELLED_INHERITANCE_PROPERTY_LEN = CANCELLED_INHERITANCE_PROPERTY.length();

    @Reference
    private SlingRepository repository;

    private Session session;

    private ObservationManager observationManager;

    protected void activate(ComponentContext ctx) {
        try {
            session = repository.loginService("serviceUser", null);
            observationManager = session.getWorkspace().getObservationManager();

            String[] nodeTypes = {"cq:PageContent"};
            int eventTypes = Event.PROPERTY_ADDED | Event.PROPERTY_CHANGED | Event.PROPERTY_REMOVED;
            observationManager.addEventListener(this, eventTypes, "/content", true, null, nodeTypes, true);

            log.info("Activated JCR event listener");
        } catch (LoginException le) {
            log.error("Error activating JCR event listener: {}", le.getMessage());
        } catch (Exception e) {
            log.error("Error activating JCR event listener", e);
        }
    }

    protected void deactivate(ComponentContext componentContext) {
        try {
            if (observationManager != null) {
                observationManager.removeEventListener(this);
                log.info("Deactivated JCR event listener");
            }
        }
        catch (RepositoryException re) {
            log.error("Error deactivating JCR event listener", re);
        }
        finally {
            if (session != null) {
                session.logout();
                session = null;
            }
        }
    }

    private Set<String> collectDeepProps(Value[] vals) throws RepositoryException {
        Set<String> deepProps = new HashSet<String>();
        if (vals != null) {
            for (Value val : vals) {
                String prop = val.getString();
                // Ignoring properties that start with '/' because that represents
                // an absolute path and this listener currently does not handle
                // that use case.
                if (!prop.startsWith("/") && prop.lastIndexOf("/") > 0) {
                    deepProps.add(prop);
                }
            }
        }
        return deepProps;
    }

    private Value[] getPropertyValues(Node node, String propertyName) throws RepositoryException {
        if (node.hasProperty(propertyName)) {
            Property prop = node.getProperty(propertyName);
            Value[] values;

            // This check is necessary to ensure a multi-valued field is applied...
            if (prop.isMultiple()) {
                values = prop.getValues();
            } else {
                values = new Value[1];
                values[0] = prop.getValue();
            }

            return values;
        }

        return new Value[0];
    }

    private void addOrRemoveProp(Node pageContentNode, String cancelInheritancePropName, boolean addToCancel) throws RepositoryException {
        int propPos = cancelInheritancePropName.lastIndexOf("/");
        String childNodePath = cancelInheritancePropName.substring(0, propPos);
        String childPropName = cancelInheritancePropName.substring(propPos + 1);

        log.debug("{} property '{}' for {} on child node '{}'", (addToCancel ? "Adding" : "Removing"), childPropName, CANCELLED_INHERITANCE_PROPERTY, childNodePath);

        // Create the child node if it does not yet exist
        Node childNode = JcrUtil.createPath(pageContentNode, childNodePath, false, "nt:unstructured", "nt:unstructured", session, false);

        Value[] existingChildCancelInheritanceProp = getPropertyValues(childNode, CANCELLED_INHERITANCE_PROPERTY);

        List<String> updatedChildCancelInheritanceFields = new ArrayList<String>();
        for (Value existingCancelFieldValue : existingChildCancelInheritanceProp) {
            String existingCancelFieldString = existingCancelFieldValue.getString();
            if (!existingCancelFieldString.equals(childPropName)) {
                updatedChildCancelInheritanceFields.add(existingCancelFieldString);
            }
        }

        if (addToCancel) {
            updatedChildCancelInheritanceFields.add(childPropName);
        }

        if (updatedChildCancelInheritanceFields.size() != existingChildCancelInheritanceProp.length) {
            if (updatedChildCancelInheritanceFields.size() > 0) {
                childNode.setProperty(CANCELLED_INHERITANCE_PROPERTY, updatedChildCancelInheritanceFields.toArray(new String[updatedChildCancelInheritanceFields.size()]));
            } else {
                childNode.setProperty(CANCELLED_INHERITANCE_PROPERTY, (String[]) null);
            }
            session.save();
        }
    }

    @Override
    public void onEvent(EventIterator itr) {
        while (itr.hasNext()) {
            Event event = itr.nextEvent();
            try {
                if (event.getPath().endsWith("/" + CANCELLED_INHERITANCE_PROPERTY)) {
                    Map eventInfo = event.getInfo();

                    Set<String> deepPropsBefore = collectDeepProps((Value[]) eventInfo.get("beforeValue"));
                    Set<String> deepPropsAfter = collectDeepProps((Value[]) eventInfo.get("afterValue"));

                    // De-dupe the lists so we can tell which props are added and which are removed
                    for (String propAfter : deepPropsAfter.toArray(new String[deepPropsAfter.size()])) {
                        if (deepPropsBefore.contains(propAfter)) {
                            deepPropsBefore.remove(propAfter);
                            deepPropsAfter.remove(propAfter);
                        }
                    }

                    if (deepPropsAfter.size() > 0 || deepPropsBefore.size() > 0) {
                        String parentPath = StringUtils.substring(event.getPath(), 0, -1 * (CANCELLED_INHERITANCE_PROPERTY_LEN + 1));
                        session.refresh(true);
                        Node pageContentNode = session.getNode(parentPath);

                        for (String propAdded : deepPropsAfter) {
                            addOrRemoveProp(pageContentNode, propAdded, true);
                        }

                        for (String propRemoved : deepPropsBefore) {
                            addOrRemoveProp(pageContentNode, propRemoved, false);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Error propagating cq:propertyInheritanceCancelled for deep properties to child nodes", e);
            }
        }
    }
}