package com.acme.insurance.admin.bean;

import com.acme.insurance.shared.model.ClaimStatus;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * LANDMINE [JSF]: javax.faces.bean.ManagedBean + javax.faces.bean.ViewScoped.
 * Both annotations are REMOVED in Jakarta Faces 4. Migration = CDI:
 *   @javax.faces.bean.ManagedBean  -> @jakarta.inject.Named
 *   @javax.faces.bean.ViewScoped   -> @jakarta.faces.view.ViewScoped (note: different package!)
 */
@ManagedBean(name = "claimAdmin")
@ViewScoped
public class ClaimAdminBean implements Serializable {

    private String claimNumber;
    private ClaimStatus selectedStatus = ClaimStatus.OPEN;

    public List<ClaimStatus> getStatuses() {
        return Arrays.asList(ClaimStatus.values());
    }

    public String save() {
        // wired to DWR lookup in the xhtml; see claims.xhtml
        return "saved";
    }

    public String getClaimNumber() { return claimNumber; }
    public void setClaimNumber(String claimNumber) { this.claimNumber = claimNumber; }
    public ClaimStatus getSelectedStatus() { return selectedStatus; }
    public void setSelectedStatus(ClaimStatus selectedStatus) { this.selectedStatus = selectedStatus; }
}
