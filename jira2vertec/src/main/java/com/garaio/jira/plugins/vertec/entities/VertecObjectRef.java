package com.garaio.jira.plugins.vertec.entities;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by IntelliJ IDEA.
 * User: mjr0177
 * Date: Sep 27, 2011
 * Time: 8:15:29 AM
 */

@XmlRootElement
public class VertecObjectRef {

    public String getId() {
        return id;
    }

    @XmlElement(name="objref")
    public void setId(String id) {
        this.id = id;
    }

    private String id;
}
