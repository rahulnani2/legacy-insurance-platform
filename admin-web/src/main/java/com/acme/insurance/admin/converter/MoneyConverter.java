package com.acme.insurance.admin.converter;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import java.math.BigDecimal;

@FacesConverter("moneyConverter")
public class MoneyConverter implements Converter<BigDecimal> {

    @Override
    public BigDecimal getAsObject(FacesContext context, UIComponent component, String value) {
        return (value == null || value.isEmpty()) ? null : new BigDecimal(value);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, BigDecimal value) {
        return value == null ? "" : value.toString();
    }
}
