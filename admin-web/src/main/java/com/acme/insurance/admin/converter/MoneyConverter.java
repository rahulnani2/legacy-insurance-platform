package com.acme.insurance.admin.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import java.math.BigDecimal;

/**
 * LANDMINE [JSF]: javax.faces.convert.Converter is a RAW type here (Faces 2 style).
 * Faces 4 is jakarta.faces.convert.Converter<T> (generic). The package move AND the
 * generic signature both have to change, or you get raw-type/override mismatches.
 */
@FacesConverter("moneyConverter")
public class MoneyConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        return (value == null || value.isEmpty()) ? null : new BigDecimal(value);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return value == null ? "" : value.toString();
    }
}
