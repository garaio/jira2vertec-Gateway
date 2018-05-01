package com.garaio.jira.plugins.vertec.entities;

import com.garaio.jira.plugins.vertec.soap.VertecSoapResultDef;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by IntelliJ IDEA.
 * User: mjr0177_2
 * Date: Sep 20, 2011
 * Time: 12:36:20 PM
 */
@XmlRootElement(name = "Projekt")
public class VertecProject extends VertecBaseEntity implements Comparable<VertecProject>
{    
    @XmlElement(name = "code")
    private String name;

    @XmlElement(name = "beschrieb", required = false)
    private String beschrieb;

    public String getName() {
        return name;
    }

    public String getBeschrieb() {
        return beschrieb;
    }

    public int compareTo(VertecProject projekt) {
       return name.compareTo(projekt.getName());
    }

    public static VertecSoapResultDef createResultDef(boolean mitBeschrieb) {
        VertecSoapResultDef resultDef = new VertecSoapResultDef();
        resultDef.getMembers().add("code");
        if (mitBeschrieb) {
            resultDef.getMembers().add("beschrieb");
        }
        return resultDef;
    }

}
