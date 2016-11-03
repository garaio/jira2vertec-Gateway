package com.jira.plugins.vertec;

import com.jira.plugins.configuration.JiraToVertecConfiguration;
import org.junit.Ignore;

@Ignore
public class TestConfigurationImpl implements JiraToVertecConfiguration {

    public String getVertecServiceUrl() {
        return "http://erptest1:8090/xml";
    }

    public String getVertecServiceUser() {
        return "{username}";
    }

    public String getVertecServicePassword() {
        return "{password}";
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
