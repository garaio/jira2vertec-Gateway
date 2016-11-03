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

package com.jira.plugins.vertec.service;

import com.jira.plugins.configuration.JiraToVertecConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

public class VertecConnectorImpl implements VertecConnector {
    private JiraToVertecConfiguration configuration;

    public VertecConnectorImpl(JiraToVertecConfiguration configuration) {
        this.configuration = configuration;
    }

    public InputStream Query(String message) throws IOException {
        // Send data
        URL url = new URL(configuration.getVertecServiceUrl());
        URLConnection conn = url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "text/html");
        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write(message);
        wr.flush();

        return conn.getInputStream();
    }
}
