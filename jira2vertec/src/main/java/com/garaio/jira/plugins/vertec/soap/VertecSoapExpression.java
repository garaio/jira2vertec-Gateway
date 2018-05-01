package com.garaio.jira.plugins.vertec.soap;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by IntelliJ IDEA.
 * User: mjr0177_2
 * Date: Sep 22, 2011
 * Time: 12:30:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class VertecSoapExpression {

    private String alias;

    private String ocl;

    public String getAlias() {
        return alias;
    }

    @XmlElement(name = "alias")
    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getOcl() {
        return ocl;
    }

    @XmlElement(name = "ocl")
    public void setOcl(String ocl) {
        this.ocl = ocl;
    }
}
