package com.garaio.jira.plugins.vertec.service;

import com.garaio.jira.plugins.vertec.soap.VertecSoapBody;
import com.garaio.jira.plugins.vertec.soap.VertecSoapEnvelope;
import com.garaio.jira.plugins.vertec.soap.VertecSoapFault;
import com.garaio.jira.plugins.vertec.soap.VertecSoapQueryResponse;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: mjr0177_2
 * Date: Sep 20, 2011
 * Time: 2:08:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class VertecSerializerImpl implements VertecSerializer
{
    private JAXBContext jaxbContext;

    private final static Logger logger = Logger.getLogger(VertecSerializerImpl.class);

    public VertecSerializerImpl() throws JAXBException
    {
        jaxbContext = JAXBContext.newInstance("com.garaio.jira.plugins.vertec.soap", this.getClass().getClassLoader());
    }

    private Marshaller getMarshaller() throws JAXBException
    {
        Marshaller marshaller = jaxbContext.createMarshaller();

        // XML declaration mit utf-8 setzen.
        // siehe https://www.vertec.com/ch/support/kb/technik-und-datenmodell/vertecservice/xml/xmlschnittstelle/
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "utf-8");
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.FALSE);

        return marshaller;
    }

    private Unmarshaller getUnmarshaller() throws JAXBException
    {
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        return unmarshaller;
    }

    public String SerializeObject(Object obj) throws JAXBException
    {
        StringWriter writer = new StringWriter();
        getMarshaller().marshal(obj, writer);

        return writer.toString();
    }

    public <T> List<T> DeserializeResponseList(String response) throws JAXBException, VertecServiceException {
        StringReader reader = null;
        try {
            logResponse(response);
            reader = new StringReader(response);
            VertecSoapEnvelope envelope = (VertecSoapEnvelope) getUnmarshaller().unmarshal(reader);
            VerifyResponse(envelope);

            VertecSoapBody<VertecSoapQueryResponse<T>> body = envelope.getBody();
            if (body.getContent() != null && body.getContent().getItems() != null)
            {
                List<T> items = body.getContent().getItems();
                return items;
            }
        } finally{
            if (reader != null)
            {
                reader.close();
            }
        }
        return Collections.emptyList();
    }

    public <T> T DeserializeResponse(String response) throws JAXBException, VertecServiceException
    {
        StringReader reader = null;
        try {
            reader = new StringReader(response);
            VertecSoapEnvelope envelope = (VertecSoapEnvelope) getUnmarshaller().unmarshal(reader);
            VerifyResponse(envelope);
            VertecSoapBody<T> body = envelope.getBody();

            return body.getContent();
        }  finally{
            if (reader != null)
            {
                reader.close();
            }
        }
    }

     private void logResponse(String response) {
         InputStream is = null;
           try {
               if (logger.isDebugEnabled()) {
                   logger.debug("[JiraToVertec] Antwort von Vertec: " + response);
               }
           } finally{
               try {
                   if (is != null)
                    is.close();
               } catch (IOException e) {
                    logger.debug("[JiraToVertec] Fehler beim Verarbeiten der Antwort von Vertec (Stream schliessen) zur Ausgabe im Log: " + e);
               }
           }
    }

    private void VerifyResponse(VertecSoapEnvelope envelope) throws VertecServiceException
    {
        if (envelope == null || envelope.getBody() == null)
        {
            throw new VertecServiceException("Eine Antwort vom Vertec-Service konnte nicht deserialisiert werden!");
        }

        VertecSoapBody body = envelope.getBody();
        VertecSoapFault fault = body.getFault();

        if (fault != null)
        {
            String message = fault.getDescription();
            throw new VertecServiceException(fault);
        }
    }


}
