package com.acme.insurance.admin.bean;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import java.io.Serializable;

@Named("policyAdmin")
@RequestScoped
public class PolicyAdminBean implements Serializable {

    private String productCode = "AUTO-STD";

    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }
}
