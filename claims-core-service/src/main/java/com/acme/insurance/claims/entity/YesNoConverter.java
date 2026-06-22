package com.acme.insurance.claims.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Replaces Hibernate 5 {@code @Type(type="yes_no")} which was removed in Hibernate 6.
 * Maps {@code boolean} to a single-char {@code "Y"}/{@code "N"} column value.
 */
@Converter
public class YesNoConverter implements AttributeConverter<Boolean, String> {

    @Override
    public String convertToDatabaseColumn(Boolean attribute) {
        return attribute != null && attribute ? "Y" : "N";
    }

    @Override
    public Boolean convertToEntityAttribute(String dbData) {
        return "Y".equalsIgnoreCase(dbData);
    }
}
