package com.acme.insurance.admin.dwr;

import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;

/**
 * LANDMINE [DWR — REWRITE, not bump]: org.directwebremoting annotations expose this
 * to browser JS via dwr/engine.js. No jakarta-compatible DWR exists. The migration is
 * a REWRITE: turn this into a REST controller (e.g. @GetMapping("/api/lookup/claim/{n}"))
 * and replace the generated DWR JS client calls in the xhtml with fetch()/AJAX.
 */
@RemoteProxy(name = "ClaimLookup")
public class ClaimLookupRemote {

    @RemoteMethod
    public String describe(String claimNumber) {
        if (claimNumber == null || claimNumber.trim().isEmpty()) {
            return "unknown";
        }
        return "Claim " + claimNumber + " is OPEN";
    }
}
