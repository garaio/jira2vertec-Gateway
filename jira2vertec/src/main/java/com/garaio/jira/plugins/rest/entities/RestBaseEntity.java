package com.garaio.jira.plugins.rest.entities;

import com.garaio.jira.plugins.util.BooleanAdapter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * Created by IntelliJ IDEA.
 * User: mjr0177
 * Date: Sep 28, 2011
 * Time: 4:30:50 PM
 */
public class RestBaseEntity
{
    private Boolean isValid;

    private String id;

    @XmlElement(name = "objref")
    public String getObjref()
    {
        return objref;
    }

    public void setObjref(String objref)
    {
        this.objref = objref;
    }

    private String objref;

    @XmlElement(name = "objid")
    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }
}
