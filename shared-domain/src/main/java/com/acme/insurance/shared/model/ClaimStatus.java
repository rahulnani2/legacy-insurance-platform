package com.acme.insurance.shared.model;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;

/** LANDMINE [JAXB]: @XmlEnum / @XmlEnumValue from javax.xml.bind. */
@XmlEnum
public enum ClaimStatus {
    @XmlEnumValue("OPEN") OPEN,
    @XmlEnumValue("IN_REVIEW") IN_REVIEW,
    @XmlEnumValue("APPROVED") APPROVED,
    @XmlEnumValue("DENIED") DENIED,
    @XmlEnumValue("CLOSED") CLOSED
}
