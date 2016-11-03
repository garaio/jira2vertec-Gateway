/**
 * Copyright 2013 GARAIO AG <www.garaio.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.jira.plugins.vertec.service;

import com.jira.plugins.configuration.JiraToVertecConfiguration;
import com.jira.plugins.vertec.soap.VertecSoapBody;
import com.jira.plugins.vertec.soap.VertecSoapEnvelope;
import com.jira.plugins.vertec.soap.VertecSoapQueryResponse;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.Collections;
import java.util.List;

public class VertecSerializerImpl implements VertecSerializer
{
    private JAXBContext jaxbContext;

    private final static Logger logger = Logger.getLogger(VertecSerializerImpl.class);

    public VertecSerializerImpl() throws JAXBException
    {
        jaxbContext = JAXBContext.newInstance("com.jira.plugins.vertec.soap",this.getClass().getClassLoader());
    }

    private Marshaller getMarshaller() throws JAXBException
    {
        Marshaller marshaller = jaxbContext.createMarshaller();
        // Ohne setzen des Encoding Properties wird UTF-8 verwendet, was zu Problemen mit Umlauten f�hrt.
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "");
        //marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
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

    public <T> List<T> DeserializeResponseList(InputStream stream) throws JAXBException, VertecServiceException {
        InputStream inputStream = null;
        try {
            inputStream = logResponse(stream);
            VertecSoapEnvelope envelope = (VertecSoapEnvelope) getUnmarshaller().unmarshal(inputStream);
            VerifyResponse(envelope);

            VertecSoapBody<VertecSoapQueryResponse<T>> body = envelope.getBody();
            if (body.getContent() != null && body.getContent().getItems() != null)
            {
                List<T> items = body.getContent().getItems();
                return items;
            }
        } finally{
           try {
               if (inputStream != null)
                inputStream.close();
           } catch (IOException e) {
                logger.error("[JiraToVertec] Fehler beim Verarbeiten der Antwort von Vertec (Stream schliessen): " + e);
           }
       }
        return Collections.emptyList();
    }

    public <T> T DeserializeResponse(InputStream stream) throws JAXBException, VertecServiceException
    {
        InputStream inputStream = null;
        try {
            inputStream = logResponse(stream);
            VertecSoapEnvelope envelope = (VertecSoapEnvelope) getUnmarshaller().unmarshal(inputStream);
            VerifyResponse(envelope);
            VertecSoapBody<T> body = envelope.getBody();

            return body.getContent();
        }  finally{
            try {
               if (inputStream != null)
                inputStream.close();
           } catch (IOException e) {
                logger.error("[JiraToVertec] Fehler beim Verarbeiten der Antwort von Vertec (Stream schliessen): " + e);
           }
       }
    }

     private InputStream logResponse(InputStream stream) {
         InputStream is = null;
           try {
               if (logger.isDebugEnabled()) {
                   byte[] bytes = IOUtils.toByteArray(stream);
                   is =  new ByteArrayInputStream(bytes);
                   Writer writer = new StringWriter();
                   char[] buffer = new char[1024];
                   Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                   int n;
                   while ((n = reader.read(buffer)) != -1) {
                       writer.write(buffer, 0, n);
                   }
                   logger.debug("[JiraToVertec] Antwort von Vertec: " + writer.toString());
                   return new ByteArrayInputStream(bytes);
               }
           }
           catch (IOException e) {
                logger.debug("[JiraToVertec] Fehler beim Verarbeiten der Antwort von Vertec zur Ausgabe im Log: " + e);
           } finally{
               try {
                   if (is != null)
                    is.close();
               } catch (IOException e) {
                    logger.debug("[JiraToVertec] Fehler beim Verarbeiten der Antwort von Vertec (Stream schliessen) zur Ausgabe im Log: " + e);
               }
           }
         return stream;
    }

    private void VerifyResponse(VertecSoapEnvelope envelope) throws VertecServiceException
    {
        if (envelope == null || envelope.getBody() == null)
        {
            throw new VertecServiceException("Eine Antwort vom Vertec-Service konnte nicht deserialisiert werden!");
        }

        VertecSoapBody body = envelope.getBody();
        if (body.getFault() != null)
        {
            throw new VertecServiceException(body.getFault());
        }
    }


}
