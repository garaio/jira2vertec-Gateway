package com.garaio.jira.plugins.rest.entities;

import com.garaio.jira.plugins.vertec.entities.VertecBaseEntity;
import com.garaio.jira.plugins.vertec.entities.VertecPhase;
import com.garaio.jira.plugins.vertec.entities.VertecProject;
import com.garaio.jira.plugins.vertec.soap.VertecSoapExpression;
import com.garaio.jira.plugins.vertec.soap.VertecSoapResultDef;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.text.Collator;
import java.util.Locale;

@XmlRootElement(name = "ProjektPhase")
public class RestPhase extends RestBaseEntity implements Comparable<RestPhase> {

    public RestPhase(VertecPhase vertecPhase, boolean zeigeProjektbeschrieb, int maxLength) {
        if (vertecPhase == null) {
            return;
        }
        
        String projektName = vertecPhase.getProjektName();

        if (zeigeProjektbeschrieb) {
            String beschrieb = vertecPhase.getProjektBeschrieb();
            projektBezeichnung =  String.format("%1s (%1s)", beschrieb.length() > maxLength ? beschrieb.substring(0, maxLength) :  beschrieb, projektName);
        } else {
            projektBezeichnung = projektName;
        }

        setId(vertecPhase.getId());
        setObjref(vertecPhase.getObjref());
        this.name = vertecPhase.getName();
        this.projektId = vertecPhase.getProjektId(); 
    }

    private String projektId;

    private String name;

    private String projektBezeichnung;

    @XmlElement(name = "bezeichnung")
    public String getName() {
        return name;
    }

    public String getProjektId() {
        return projektId;
    }

    public String getProjektBezeichnung() {
        return projektBezeichnung;
    }

    public int compareTo(RestPhase phase) {
        return Collator.getInstance(Locale.getDefault()).compare(name, phase.getName());
    }
}
