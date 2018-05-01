package com.garaio.jira.plugins.vertec.entities;

import com.garaio.jira.plugins.util.BooleanAdapter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * Created by IntelliJ IDEA.
 * User: mjr0177
 * Date: Sep 28, 2011
 * Time: 4:30:50 PM
 */
public class VertecBaseEntity
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

    @XmlElement(name = "isValid")
    @XmlJavaTypeAdapter(BooleanAdapter.class)
    public Boolean isValid()
    {
        return isValid;
    }

    public void setValid(Boolean valid)
    {
        isValid = valid;
    }

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
