package com.hs2solutions.aem.hfl.core.servlets;

import com.adobe.acs.commons.wcm.PageRootProvider;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.wcm.api.Page;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Servlet for fetching a team's schedule.
 *
 * This servlet is meant to be demonstrative of the multi-site pattern
 * of using resource types instead of hard-coded paths in order to allow
 * a component to call a servlet with the context of the site it lives on,
 * allowing content for that specific site and no others to be considered.
 */
@SlingServlet(
        description = "HFL Servlet to fetch Scheduled Games for a team.",
        resourceTypes = {"multi-site-demo/hfl-core/components/content/schedule"},
        methods = "GET",
        extensions = {"json"}
)
public class ScheduleServlet extends SlingSafeMethodsServlet {
    private static Logger logger = LoggerFactory.getLogger(ScheduleServlet.class);

    @Reference
    private PageRootProvider pageRootProvider;

    @Reference
    private QueryBuilder queryBuilder;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        Resource resource = request.getResource();
        Page homePage = pageRootProvider.getRootPage(resource);

        JSONArray jsonGames = new JSONArray();

        // Homepage should only be null of PageRootProvider is not configured with the
        // correct path to be able to find the homepage.
        if (homePage != null) {
            Session session = resource.getResourceResolver().adaptTo(Session.class);

            Map<String, String> queryParams = new HashMap<>();
            // Path is relative to the homepage of this component, so that we grab the games
            // for the correct team.
            queryParams.put("path", homePage.getPath() + "/data/games");
            queryParams.put("p.limit", "-1");
            queryParams.put("1_property", "sling:resourceType");
            queryParams.put("1_property.value", "multi-site-demo/hfl-core/components/page/datapage");
            queryParams.put("orderby", "@date");

            Query query = queryBuilder.createQuery(PredicateGroup.create(queryParams), session);
            Iterator<Node> results = query.getResult().getNodes();
            while (results.hasNext()) {
                Node game = results.next();

                boolean isHomeGame = false;
                String name = null;
                Calendar date = null;

                try {
                    PropertyIterator gameProps = game.getProperties();
                    while (gameProps.hasNext()) {
                        Property gameProp = gameProps.nextProperty();
                        if (gameProp.getName().equals("jcr:title")) {
                            name = gameProp.getString();
                        }
                        if (gameProp.getName().equals("isHomeGame") && gameProp.getBoolean()) {
                            isHomeGame = true;
                        } else if (gameProp.getName().equals("date")) {
                            date = gameProp.getDate();
                        }
                    }
                } catch (RepositoryException e) {
                    logger.error("Error reading game properties", e);
                }

                if (name != null && date != null) {
                    JSONObject jsonGame = new JSONObject();
                    try {
                        jsonGame.put("name", name);
                        jsonGame.put("date", date.getTimeInMillis());
                        jsonGame.put("isHomeGame", isHomeGame);
                        jsonGames.put(jsonGame);
                    } catch (JSONException e) {
                        logger.error("Error creating JSON object", e);
                    }
                } else {
                    logger.warn("Game found w/o date");
                }
            }
        }

        response.getWriter().write(jsonGames.toString());
    }
}
