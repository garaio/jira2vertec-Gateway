package com.garaio.jira.plugins.vertec.entities;

import com.garaio.jira.plugins.vertec.soap.VertecSoapResultDef;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Comparator;
import java.util.Date;

public abstract class VertecLeistung extends VertecBaseEntity
{
    public static final Comparator<VertecLeistung> NEUSTE_LEISTUNG_ZUERST_COMPARATOR = new Comparator<VertecLeistung>()
    {
        public int compare(VertecLeistung l1, VertecLeistung l2)
        {
            return l2.getDatum().compareTo(l1.getDatum());
        }
    };

    private String jiraReferenz;

    private int minuten;

    private Date datum;

    private String text;

    @XmlElement(name = "phase")
    private VertecObjectRef phaseRef;

    @XmlElement(name = "bearbeiter")
    private VertecObjectRef bearbeiterRef;

    @XmlElement(name = "referenz")
    public void setJiraReferenz(String jiraReferenz)
    {
        this.jiraReferenz = jiraReferenz;
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

    public String getJiraReferenz()
    {
        return jiraReferenz;
    }

    public Date getDatum()
    {
        return datum;
    }

    public abstract boolean istVerrechnet();

    public static VertecSoapResultDef createResultDef()
    {
        VertecSoapResultDef resultDef = new VertecSoapResultDef();

        //Bezeichnung der Phase
        resultDef.getMembers().add("referenz");
        resultDef.getMembers().add("datum");
        resultDef.getMembers().add("minutenInt");
        resultDef.getMembers().add("text");
        resultDef.getMembers().add("phase");


        return resultDef;
    }
}
