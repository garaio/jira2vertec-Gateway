package com.garaio.jira.plugins.configuration;

import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: mjr0177_2
 * Date: Sep 21, 2011
 * Time: 10:12:07 AM
 * To change this template use File | Settings | File Templates.
 */
public class JiraToVertecConfigurationImpl implements JiraToVertecConfiguration {
    private String vertecServiceUrl;
    private String vertecAuthServiceUrl;
    private String vertecServiceUser;
    private String vertecServicePassword;
    private boolean useVertecCloudAuth;
    private String defaultPhaseIdWennNichtZugeordnet;
    private String defaultPhaseIdWennNachbearbeitungNoetig;
    private boolean isCachingEnabled;
    private boolean zeigeProjektbeschrieb;
    private int projektbeschriebMaxLaenge;

    public JiraToVertecConfigurationImpl()
    {
        Properties properties = PropertyLoader.loadProperties("JiraToVertec");
        vertecServiceUrl = properties.getProperty("vertecServiceUrl");
        vertecAuthServiceUrl = properties.getProperty("vertecAuthServiceUrl");
        vertecServiceUser = properties.getProperty("vertecServiceUser");
        vertecServicePassword = properties.getProperty("vertecServicePassword");
        useVertecCloudAuth = Boolean.parseBoolean(properties.getProperty("useVertecCloudAuth"));
        defaultPhaseIdWennNichtZugeordnet = properties.getProperty("defaultPhaseIdWennNichtZugeordnet");
        defaultPhaseIdWennNachbearbeitungNoetig = properties.getProperty("defaultPhaseIdWennNachbearbeitungNoetig");
        isCachingEnabled = Boolean.parseBoolean(properties.getProperty("enableCaching"));
        zeigeProjektbeschrieb = Boolean.parseBoolean(properties.getProperty("zeigeProjektbeschrieb"));
        projektbeschriebMaxLaenge = Integer.parseInt(properties.getProperty("projektbeschriebMaxLaenge"));
    }

    public String getVertecServiceUrl() {
        return vertecServiceUrl;
    }

    public String getVertecAuthServiceUrl() {
        return vertecAuthServiceUrl;
    }

    public String getVertecServiceUser() {
        return vertecServiceUser;
    }

    public String getVertecServicePassword() {
        return vertecServicePassword;
    }

    public boolean getUseVertecCloudAuth() {
        return useVertecCloudAuth;
    }

    public String getDefaultPhaseIdWennNichtZugeordnet() {
        return defaultPhaseIdWennNichtZugeordnet;
    }

    public String getDefaultPhaseIdWennNachbearbeitungNoetig() {
        return defaultPhaseIdWennNachbearbeitungNoetig;
    }

    public boolean zeigeProjektbeschrieb() {
        return zeigeProjektbeschrieb;
    }

    public int getProjektbeschriebMaxLaenge() {
        if (zeigeProjektbeschrieb && projektbeschriebMaxLaenge <= 0) {
            return 25;
        }
        
        return projektbeschriebMaxLaenge;
    }

    public boolean isCachingEnabled()
    {
        return isCachingEnabled;
    }
}

