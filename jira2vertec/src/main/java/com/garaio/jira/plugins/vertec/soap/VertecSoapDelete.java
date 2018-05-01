package com.garaio.jira.plugins.vertec.soap;

import javax.xml.bind.annotation.*;

/**
 * Created by IntelliJ IDEA.
 * User: mjr0177
 * Date: Sep 28, 2011
 * Time: 2:38:07 PM
 */
@XmlRootElement(name = "Delete")
public class VertecSoapDelete
{
    private String objref;

    public String getObjref()
    {
        return objref;
    }

    @XmlElement(name = "objref")
    public void setObjref(String objref)
    {
        this.objref = objref;
    }
}
