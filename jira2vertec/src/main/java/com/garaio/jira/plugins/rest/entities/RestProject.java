package com.garaio.jira.plugins.rest.entities;

import com.garaio.jira.plugins.vertec.entities.VertecBaseEntity;
import com.garaio.jira.plugins.vertec.entities.VertecProject;
import com.garaio.jira.plugins.vertec.soap.VertecSoapResultDef;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.text.Collator;
import java.util.Locale;

@XmlRootElement(name = "Projekt")
public class RestProject extends RestBaseEntity implements Comparable<RestProject>
{
    public RestProject(VertecProject vertecProject, boolean zeigeProjektbeschrieb, int maxLength) {
        if (vertecProject == null) {
            return;    
        }

        String name = vertecProject.getName();
        
        if (zeigeProjektbeschrieb) {
            String beschrieb = vertecProject.getBeschrieb();
            bezeichnung =  String.format("%1s (%1s)", beschrieb.length() > maxLength ? beschrieb.substring(0, maxLength) :  beschrieb, name);
        } else {
            bezeichnung = name;
        }

        setId(vertecProject.getId());
        setObjref(vertecProject.getObjref());
    }

    @XmlElement(name = "bezeichnung")
    private String bezeichnung;

    public String getBezeichnung() {
        return bezeichnung;
    }

    public int compareTo(RestProject projekt) {
        return Collator.getInstance(Locale.getDefault()).compare(bezeichnung, projekt.getBezeichnung());          
    }
}
