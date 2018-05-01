package com.garaio.jira.plugins.vertec.soap;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Header")
public class VertecSoapHeader {

    @XmlElement(name = "BasicAuth")
    private VertecSoapBasicAuth basicAuth;

    protected VertecSoapHeader() {
    }

    public VertecSoapHeader(String token) {
        this.basicAuth = new VertecSoapBasicAuth(token);
    }

    public VertecSoapHeader(String username, String password) {
        this.basicAuth = new VertecSoapBasicAuth(username, password);
    }
}
