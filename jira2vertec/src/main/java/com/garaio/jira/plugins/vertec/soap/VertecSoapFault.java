package com.garaio.jira.plugins.vertec.soap;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: mjr0177_2
 * Date: Sep 21, 2011
 * Time: 9:23:22 AM
 * To change this template use File | Settings | File Templates.
 */
public class VertecSoapFault {

    @XmlElement(name = "faultcode")
    private String code;

    @XmlElement(name = "faultstring")
    private String description;

    @XmlElement(name = "detailitem")
    @XmlElementWrapper(name = "details")
    private List<String> details;

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getDetails() {
        return details;
    }
}
