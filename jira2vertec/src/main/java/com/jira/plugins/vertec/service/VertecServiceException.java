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

import com.jira.plugins.vertec.soap.VertecSoapFault;

import java.util.List;

public class VertecServiceException extends Throwable
{
    public String getMessage()
    {
        return message;
    }

    private String message;

    public VertecServiceException(String message, Object ... args){
        this.message = String.format(message, args);
    }

    public VertecServiceException(VertecSoapFault fault)
    {
        String newline = System.getProperty("line.separator");

        StringBuilder sb = new StringBuilder();
        sb.append(fault.getDescription());

        List<String> details = fault.getDetails();
        for (int i = 0; i < details.size(); i++)
        {
            sb.append(newline);
            sb.append(" - ");
            sb.append(details.get(i));
        }

        this.message = sb.toString();
    }
}
