package com.garaio.jira.plugins.vertec.soap;

import javax.xml.bind.annotation.XmlElement;

public class VertecSoapSelection {
    public String getOcl() {
        return ocl;
    }

    public String getSqlWhere() {
        return sqlWhere;
    }

    public String getSqlOrder() {
        return sqlOrder;
    }

    String ocl;
    String sqlWhere;
    String sqlOrder;
    String objRef;

    public String getObjRef() {
        return objRef;
    }

    @XmlElement(name = "objref")
    public void setObjRef(String objRef) {
        this.objRef = objRef;
    }

    @XmlElement(name = "ocl")
    public void setOcl(String ocl) {
        this.ocl = ocl;
    }

    @XmlElement(name = "sqlwhere")
    public void setSqlWhere(String sqlWhere) {
        this.sqlWhere = sqlWhere;
    }

    @XmlElement(name = "sqlorder")
    public void setSqlOrder(String sqlOrder) {
        this.sqlOrder = sqlOrder;
    }
}
