package com.garaio.jira.plugins.vertec.entities;

import com.garaio.jira.plugins.vertec.soap.VertecSoapResultDef;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: sha0247
 * Date: Mrz 12, 2012
 * Time: 8:15:29 AM
 */

@XmlRootElement(name = "Freigabe")
public class VertecFreigabe extends VertecBaseEntity {
    private Date bis;

    public Date getBis() {
        return bis;
    }

    @XmlElement(name="bis")
    public void setBis(Date bis) {
        this.bis = bis;
    }

    public static VertecSoapResultDef createResultDef()
    {
        VertecSoapResultDef resultDef = new VertecSoapResultDef();
        resultDef.getMembers().add("bis");
        return resultDef;
    }
}
