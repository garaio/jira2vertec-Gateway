package com.garaio.jira.plugins.vertec.soap;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by IntelliJ IDEA.
 * User: mjr0177
 * Date: Sep 28, 2011
 * Time: 4:09:18 PM
 */
@XmlRootElement(name="DeleteResponse")
public class VertecSoapDeleteResponse
{
    public String getText()
    {
        return text;
    }

    private String text;

    @XmlElement(name = "text")
    public void setText(String text)
    {
        this.text = text;
    }
}