package com.acme.insurance.shared.util;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * LANDMINE [JAXB]: javax.xml.bind.annotation.adapters.XmlAdapter.
 * Classic Java-8 pattern (java.time + JAXB needs a manual adapter).
 * Package must move to jakarta.xml.bind.* on Java 17.
 */
public class LocalDateAdapter extends XmlAdapter<String, LocalDate> {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    public LocalDate unmarshal(String value) {
        return value == null ? null : LocalDate.parse(value, FMT);
    }

    @Override
    public String marshal(LocalDate value) {
        return value == null ? null : value.format(FMT);
    }
}
