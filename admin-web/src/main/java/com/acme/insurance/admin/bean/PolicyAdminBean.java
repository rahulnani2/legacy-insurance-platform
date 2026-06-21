package com.acme.insurance.admin.bean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import java.io.Serializable;

/**
 * LANDMINE [JSF]: javax.faces.bean.ManagedBean + RequestScoped (both removed in Faces 4).
 */
@ManagedBean(name = "policyAdmin")
@RequestScoped
public class PolicyAdminBean implements Serializable {

    private String productCode = "AUTO-STD";

    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }
}
