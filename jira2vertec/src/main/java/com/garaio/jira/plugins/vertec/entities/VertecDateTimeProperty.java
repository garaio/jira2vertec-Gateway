package com.garaio.jira.plugins.vertec.entities;

import com.garaio.jira.plugins.vertec.soap.VertecSoapResultDef;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: sha0247
 * Date: 26.10.2011
 * Time: 13:18:56
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement(name = "DateTimeProperty")
public class VertecDateTimeProperty extends VertecBaseEntity
{
    private Date propertyvalue;

    @XmlElement(name = "propertyValue")
    public void setPropertyValue(Date propertyvalue) {
        this.propertyvalue = propertyvalue;
    }

    public Date getPropertyValue() {
        return propertyvalue;
    }
    
    public static VertecSoapResultDef createResultDef()
    {
        VertecSoapResultDef resultDef = new VertecSoapResultDef();
        resultDef.getMembers().add("propertyValue");
        return resultDef;              
    }
}
