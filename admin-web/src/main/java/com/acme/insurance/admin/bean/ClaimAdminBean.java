package com.acme.insurance.admin.bean;

import com.acme.insurance.shared.model.ClaimStatus;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Named("claimAdmin")
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
