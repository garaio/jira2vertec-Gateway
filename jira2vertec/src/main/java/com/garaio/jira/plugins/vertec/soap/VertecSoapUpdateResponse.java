package com.garaio.jira.plugins.vertec.soap;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by IntelliJ IDEA.
 * User: mjr0177
 * Date: Sep 29, 2011
 * Time: 11:13:10 AM
 */
@XmlRootElement(name="UpdateResponse")
public class VertecSoapUpdateResponse
{
    @XmlElement(name="text")
    private String text;

    public String getText()
    {
        return text;
    }
}