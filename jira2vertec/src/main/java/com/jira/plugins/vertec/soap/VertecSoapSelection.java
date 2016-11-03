/**
 * Copyright 2013 GARAIO AG <www.garaio.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.jira.plugins.vertec.soap;

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
