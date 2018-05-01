package com.garaio.jira.plugins.vertec.soap;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "BasicAuth")
public class VertecSoapBasicAuth {

    /**
     * Does some thing in old style.
     *
     * @deprecated use {@link #VertecSoapBasicAuth(String token)} or {@link #VertecSoapBasicAuth(String username, String password)} instead.
     */
    @Deprecated
    public VertecSoapBasicAuth() {
    }

    public VertecSoapBasicAuth(String username, String password) {
        this.username = username;
        this.password = password;
        this.token = null;
    }

    public VertecSoapBasicAuth(String token) {
        this.username = null;
        this.password = null;
        this.token = token;
    }

    @XmlElement(name = "Name")
    private String username;

    public String getUsername() {
        return username;
    }

    @XmlElement(name = "Password")
    private String password;

    public String getPassword() {
        return password;
    }

    @XmlElement(name = "Token")
    private String token;

    public String getToken() {
        return token;
    }
}
