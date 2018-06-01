package com.garaio.jira.plugins.vertec.entities;

import com.garaio.jira.plugins.vertec.soap.VertecSoapResultDef;
import com.sun.org.apache.xerces.internal.dom.ElementNSImpl;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.namespace.QName;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public abstract class VertecLeistung extends VertecBaseEntity
{
    public static final Comparator<VertecLeistung> NEUSTE_LEISTUNG_ZUERST_COMPARATOR = new Comparator<VertecLeistung>()
    {
        public int compare(VertecLeistung l1, VertecLeistung l2)
        {
            return l2.getDatum().compareTo(l1.getDatum());
        }
    };

    private List<JAXBElement<String>> otherProperties = new ArrayList<>();

    private int minuten;

    private Date datum;

    private String text;

    @XmlElement(name = "phase")
    private VertecObjectRef phaseRef;

    @XmlElement(name = "bearbeiter")
    private VertecObjectRef bearbeiterRef;

    @XmlAnyElement
    public void setOthers(List<JAXBElement<String>> otherProperties) {
        this.otherProperties = otherProperties;
    }

    public void setJiraReferenz(String fieldName, String jiraReferenz)
    {
        if(fieldName == null)
        {
            return;
        }
        try {
            for (Object property : otherProperties)
            {
                Method getTagName = property.getClass().getMethod("getTagName");
                if(getTagName.invoke(property).equals(fieldName))
                {
                    Method setNodeValue = property.getClass().getMethod("setNodeValue", String.class);

                    // element update
                    setNodeValue.invoke(property, jiraReferenz);
                    return;
                }
            }
        } catch (NoSuchMethodException e) {
            return;
        } catch (IllegalAccessException e) {
            return;
        } catch (InvocationTargetException e) {
            return;
        }

        JAXBElement<String> value = new JAXBElement<>(new QName("", fieldName), String.class, jiraReferenz);
        otherProperties.add(value);
    }

    @XmlElement(name = "minutenInt")
    public void setMinuten(int minuten)
    {
        this.minuten = minuten;
    }

    @XmlElement(name = "text")
    public void setText(String text)
    {
        this.text = text;
    }

    @XmlElement(name = "datum")
    public void setDatum(Date datum)
    {
        this.datum = datum;
    }

    @XmlTransient
    public void setPhaseId(String phaseId)
    {
        if (phaseRef == null)
        {
            phaseRef = new VertecObjectRef();
        }
        this.phaseRef.setId(phaseId);
    }

    @XmlTransient
    public void setBearbeiterId(String bearbeiterId)
    {
        if (bearbeiterRef == null)
        {
            bearbeiterRef = new VertecObjectRef();
        }
        this.bearbeiterRef.setId(bearbeiterId);
    }

    public String getPhaseId()
    {
        return phaseRef == null ? null : phaseRef.getId();
    }

    public String getBearbeiterId()
    {
        return bearbeiterRef == null ? null : bearbeiterRef.getId();
    }

    public int getMinuten()
    {
        return minuten;
    }

    public String getText()
    {
        return text;
    }

    public String getJiraReferenz(String fieldName)
    {
        try {
            for (Object property: otherProperties) {
                Method getTagName = property.getClass().getMethod("getTagName");
                if(fieldName.equals(getTagName.invoke(property))){
                    Method getTextContent = property.getClass().getMethod("getTextContent");

                    Object value = getTextContent.invoke(property);
                    if(value != null) {
                        return value.toString();
                    }
                }
            }
        } catch (NoSuchMethodException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        } catch (InvocationTargetException e) {
            return null;
        }

        return null;
    }

    public List<JAXBElement<String>> getOthers()
    {
        return otherProperties;
    }

    public Date getDatum()
    {
        return datum;
    }

    public abstract boolean istVerrechnet();

    public static VertecSoapResultDef createResultDef(String refrenzFieldName)
    {
        VertecSoapResultDef resultDef = new VertecSoapResultDef();

        //Bezeichnung der Phase
        resultDef.getMembers().add(refrenzFieldName);
        resultDef.getMembers().add("datum");
        resultDef.getMembers().add("minutenInt");
        resultDef.getMembers().add("text");
        resultDef.getMembers().add("phase");


        return resultDef;
    }
}
