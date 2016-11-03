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

package com.jira.plugins.configuration;

import java.util.Properties;

class JiraToVertecConfigurationImpl implements JiraToVertecConfiguration {
    private String vertecServiceUrl;
    private String vertecServiceUser;
    private String vertecServicePassword;
    private String defaultPhaseIdWennNichtZugeordnet;
    private String defaultPhaseIdWennNachbearbeitungNoetig;
    private boolean isCachingEnabled;
    private boolean zeigeProjektbeschrieb;
    private int projektbeschriebMaxLaenge;

    public JiraToVertecConfigurationImpl()
    {
        Properties properties = PropertyLoader.loadProperties("JiraToVertec");
        vertecServiceUrl = properties.getProperty("vertecServiceUrl");
        vertecServiceUser = properties.getProperty("vertecServiceUser");
        vertecServicePassword = properties.getProperty("vertecServicePassword");
        defaultPhaseIdWennNichtZugeordnet = properties.getProperty("defaultPhaseIdWennNichtZugeordnet");
        defaultPhaseIdWennNachbearbeitungNoetig = properties.getProperty("defaultPhaseIdWennNachbearbeitungNoetig");
        isCachingEnabled = Boolean.parseBoolean(properties.getProperty("enableCaching"));
        zeigeProjektbeschrieb = Boolean.parseBoolean(properties.getProperty("zeigeProjektbeschrieb"));
        projektbeschriebMaxLaenge = Integer.parseInt(properties.getProperty("projektbeschriebMaxLaenge"));
    }

    public String getVertecServiceUrl() {
        return vertecServiceUrl;
    }

    public String getVertecServiceUser() {
        return vertecServiceUser;
    }

    public String getVertecServicePassword() {
        return vertecServicePassword;
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

