package com.acme.insurance.shared.xml;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * LANDMINE [JAXB]: direct javax.xml.bind.JAXBContext / Marshaller / Unmarshaller usage.
 * This is the runtime entry point; if the API package is migrated but no runtime impl
 * is on the Java 17 classpath, this compiles but throws at runtime ("no implementation
 * of JAXB-API found"). A favourite silent failure to test your skill against.
 */
public final class JaxbSupport {

    private JaxbSupport() { }

    public static <T> String toXml(T object) {
        try {
            JAXBContext ctx = JAXBContext.newInstance(object.getClass());
            Marshaller marshaller = ctx.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            StringWriter writer = new StringWriter();
            marshaller.marshal(object, writer);
            return writer.toString();
        } catch (JAXBException e) {
            throw new IllegalStateException("Marshalling failed", e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T fromXml(String xml, Class<T> type) {
        try {
            JAXBContext ctx = JAXBContext.newInstance(type);
            Unmarshaller unmarshaller = ctx.createUnmarshaller();
            return (T) unmarshaller.unmarshal(new StringReader(xml));
        } catch (JAXBException e) {
            throw new IllegalStateException("Unmarshalling failed", e);
        }
    }
}
