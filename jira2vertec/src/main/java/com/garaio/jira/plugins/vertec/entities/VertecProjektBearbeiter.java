package com.garaio.jira.plugins.vertec.entities;

import com.garaio.jira.plugins.vertec.soap.VertecSoapResultDef;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by IntelliJ IDEA.
 * User: mjr0177
 * Date: Sep 27, 2011
 * Time: 8:15:29 AM
 */

@XmlRootElement(name = "Projektbearbeiter")
public class VertecProjektBearbeiter extends VertecBaseEntity
{
    public String getLoginName()
    {
        return loginName;
    }
    public Boolean isActive()
    {
        return active;
    }

    private String loginName;
    private Boolean active;

    @XmlElement(name = "loginName")
    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }

    @XmlElement(name = "aktiv")
    public void setIsActive(Boolean active)
    {
        this.active = active;
    }

    public static VertecSoapResultDef createResultDef()
    {
        VertecSoapResultDef resultDef = new VertecSoapResultDef();

        resultDef.getMembers().add("loginName");
        resultDef.getMembers().add("aktiv");

        return resultDef;
    }
}
