package com.garaio.jira.plugins.vertec.entities;

import com.garaio.jira.plugins.vertec.soap.VertecSoapExpression;
import com.garaio.jira.plugins.vertec.soap.VertecSoapResultDef;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ProjektPhase")
public class VertecPhase extends VertecBaseEntity implements Comparable<VertecPhase> {
    private String projektAktiv;

    @XmlElement(name = "code")
    public String getName() {
        return name;
    }

    @XmlElement(name = "projektId")
    public String getProjektId() {
        return projektId;
    }

    @XmlElement(name = "projektAktiv")
    public String getProjektAktiv() {
        return projektAktiv;
    }

    @XmlElement(name = "aktiv")
    public int getAktiv() {
        return aktiv;
    }

    public boolean isAktivInklusiveProjekt() {
        return (aktiv == 1) && ("Y".equalsIgnoreCase(projektAktiv));
    }

    @XmlElement(name = "projektName")
    public String getProjektName() {
        return projektName;
    }

    @XmlElement(name = "projektBeschrieb", required = false)
    public String getProjektBeschrieb() {
        return projektBeschrieb;
    }

    private int aktiv;

    private String projektId;

    private String projektName;

    private String projektBeschrieb;

    private String name;

    public void setAktiv(int aktiv) {
        this.aktiv = aktiv;
    }

    public void setProjektId(String projektId) {
        this.projektId = projektId;
    }

    public void setProjektAktiv(String projektAktiv) {
        this.projektAktiv = projektAktiv;
    }

    public void setProjektName(String projektName) {
        this.projektName = projektName;
    }

    public void setProjektBeschrieb(String projektBeschrieb) {
        this.projektBeschrieb = projektBeschrieb;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int compareTo(VertecPhase phase) {
        return name.compareTo(phase.getName());
    }

    public static VertecSoapResultDef createResultDef(boolean mitBeschrieb) {
        VertecSoapResultDef resultDef = new VertecSoapResultDef();

        //Projekt ID auf der Phase ablegen
        VertecSoapExpression expProjektId = new VertecSoapExpression();
        expProjektId.setAlias("projektId");
        expProjektId.setOcl("projekt.boldid");
        resultDef.getExpressions().add(expProjektId);

        //Ist das Projekt aktiv?
        VertecSoapExpression expProjektAktiv = new VertecSoapExpression();
        expProjektAktiv.setAlias("projektAktiv");
        expProjektAktiv.setOcl("projekt.aktiv");
        resultDef.getExpressions().add(expProjektAktiv);

        //Projekt-Bezeichnung auf der Phase ablegen
        VertecSoapExpression expProjektCode = new VertecSoapExpression();
        expProjektCode.setAlias("projektName");
        expProjektCode.setOcl("projekt.code");
        resultDef.getExpressions().add(expProjektCode);

        if (mitBeschrieb) {
            VertecSoapExpression expProjektBeschrieb = new VertecSoapExpression();
            expProjektBeschrieb.setAlias("projektBeschrieb");
            expProjektBeschrieb.setOcl("projekt.beschrieb");
            resultDef.getExpressions().add(expProjektBeschrieb);
        }

        //Bezeichnung der Phase
        resultDef.getMembers().add("code");

        //Ist die Phase aktiv?
        resultDef.getMembers().add("aktiv");

        return resultDef;
    }
}
