package com.garaio.jira.plugins.vertec.service;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: mjr0177_2
 * Date: Sep 21, 2011
 * Time: 10:45:47 AM
 * To change this template use File | Settings | File Templates.
 */
public interface VertecSerializer {
    String SerializeObject(Object obj) throws JAXBException;

    <T> List<T> DeserializeResponseList(String response) throws JAXBException, VertecServiceException;
    
    <T> T DeserializeResponse(String response) throws JAXBException, VertecServiceException;
}
