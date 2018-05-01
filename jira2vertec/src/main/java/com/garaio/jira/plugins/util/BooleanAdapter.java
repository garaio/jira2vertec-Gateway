package com.garaio.jira.plugins.util;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Created by IntelliJ IDEA.
 * User: mjr0177
 * Date: Sep 29, 2011
 * Time: 8:04:31 AM
 */
public class BooleanAdapter extends XmlAdapter<Integer, Boolean>
{
    @Override
    public Boolean unmarshal(Integer s)
    {
        return s == null ? null : s == 1;
    }

    @Override
    public Integer marshal(Boolean c)
    {
        return c == null ? null : c ? 1 : 0;
    }
}
