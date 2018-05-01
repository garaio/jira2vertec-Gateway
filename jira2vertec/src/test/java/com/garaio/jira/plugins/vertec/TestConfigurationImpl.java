package com.garaio.jira.plugins.vertec;

import com.garaio.jira.plugins.configuration.JiraToVertecConfiguration;
import org.junit.Ignore;

@Ignore
public class TestConfigurationImpl implements JiraToVertecConfiguration {

    public String getVertecServiceUrl() {
        return "http://erptest1:8090/xml";
    }

    public String getVertecAuthServiceUrl() { return "http://erptest1:8090/auth/xml"; }

    public String getVertecServiceUser() {
        return "administrator";
    }

    public String getVertecServicePassword() {
        return "";
    }

    public boolean getUseVertecCloudAuth() {
        return true;
    }

    public String getDefaultPhaseIdWennNichtZugeordnet() {
        return "phaseIdNichtZugeordnet";
    }

    public String getDefaultPhaseIdWennNachbearbeitungNoetig() {
        return "phaseIdNachbearbeitungNoetig";
    }

    public boolean isCachingEnabled()
    {
        return true;
    }

    public boolean zeigeProjektbeschrieb() {
        return false;
    }

    public int getProjektbeschriebMaxLaenge() {
        return 25;
    }
}
