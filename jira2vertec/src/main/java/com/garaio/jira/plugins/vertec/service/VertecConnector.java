package com.garaio.jira.plugins.vertec.service;

import com.garaio.jira.plugins.vertec.soap.VertecSoapEnvelope;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: mjr0177_2
 * Date: Sep 21, 2011
 * Time: 10:46:05 AM
 * To change this template use File | Settings | File Templates.
 */
public interface VertecConnector {
    String Query(VertecSoapEnvelope envelope) throws IOException, JAXBException;
}
