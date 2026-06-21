package com.acme.insurance.policy.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

/**
 * LANDMINE [CXF/JAX-WS]: javax.jws.WebService SEI.
 * On Java 17 + CXF 4: javax.jws.* -> jakarta.jws.*, javax.xml.ws.* -> jakarta.xml.ws.*.
 */
@WebService(targetNamespace = "http://ws.policy.insurance.acme.com/")
public interface PolicyService {

    @WebMethod
    @WebResult(name = "policyQuoteResponse")
    PolicyQuoteResponse quote(@WebParam(name = "policyQuoteRequest") PolicyQuoteRequest request);
}
