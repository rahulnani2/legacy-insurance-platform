package com.acme.insurance.admin.dwr;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * Replaces the DWR @RemoteProxy with a JAX-RS REST endpoint.
 * DWR has no jakarta release — this is a REWRITE, not an upgrade.
 */
@Path("/api/lookup/claim")
public class ClaimLookupRemote {

    @GET
    @Path("/{claimNumber}")
    @Produces(MediaType.TEXT_PLAIN)
    public String describe(@PathParam("claimNumber") String claimNumber) {
        if (claimNumber == null || claimNumber.trim().isEmpty()) {
            return "unknown";
        }
        return "Claim " + claimNumber + " is OPEN";
    }
}
