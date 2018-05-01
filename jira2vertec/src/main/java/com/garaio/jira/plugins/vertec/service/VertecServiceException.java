package com.garaio.jira.plugins.vertec.service;

import com.garaio.jira.plugins.vertec.soap.VertecSoapFault;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: mjr0177_2
 * Date: Sep 21, 2011
 * Time: 9:28:47 AM
 * To change this template use File | Settings | File Templates.
 */
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
