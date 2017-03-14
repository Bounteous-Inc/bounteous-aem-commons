package com.hs2solutions.aem.hfl.core.servlets;

import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Servlet for fetching a team's schedule.
 */
@SlingServlet(paths = "/bin/hs2/tbd", methods = "GET")
public class Schedule extends SlingSafeMethodsServlet {
    // TODO Create servlet

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        response.getWriter().write("Hello World");
    }
}
