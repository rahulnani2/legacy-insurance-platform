package com.acme.insurance.shared.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/** LANDMINE [JAXB]: javax.xml.bind annotations on a shared model. */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "party", propOrder = {"partyId", "fullName", "email"})
public class Party {

    @XmlElement(required = true)
    private String partyId;
    @XmlElement
    private String fullName;
    @XmlElement
    private String email;

    public String getPartyId() { return partyId; }
    public void setPartyId(String partyId) { this.partyId = partyId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
