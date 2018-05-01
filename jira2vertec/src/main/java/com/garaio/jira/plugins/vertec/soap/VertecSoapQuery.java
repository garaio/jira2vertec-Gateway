package com.garaio.jira.plugins.vertec.soap;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {"selection", "resultDef"})
@XmlRootElement(name = "Query")
public class VertecSoapQuery
{
    private VertecSoapResultDef resultDef;

    private VertecSoapSelection selection;

    public VertecSoapSelection getSelection()
    {
        return selection;
    }

    @XmlElement(name = "Selection")
    public void setSelection(VertecSoapSelection selection)
    {
        this.selection = selection;
    }

    @XmlElement(name = "resultdef")
    public void setResultDef(VertecSoapResultDef resultDef)
    {
        this.resultDef = resultDef;
    }

    public VertecSoapResultDef getResultDef()
    {
        return resultDef;
    }

}

