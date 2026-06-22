package com.acme.insurance.admin.dwr;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Replaces the DWR @RemoteProxy with a plain servlet.
 * DWR has no jakarta release — this is a REWRITE, not an upgrade.
 */
public class ClaimLookupRemote extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        String claimNumber = (pathInfo != null && pathInfo.length() > 1)
                ? pathInfo.substring(1) : null;

        resp.setContentType("text/plain");
        if (claimNumber == null || claimNumber.trim().isEmpty()) {
            resp.getWriter().write("unknown");
        } else {
            resp.getWriter().write("Claim " + claimNumber + " is OPEN");
        }
    }
}
