package com.garaio.jira.plugins.vertec.entities;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by IntelliJ IDEA.
 * User: mjr0177
 * Date: Sep 26, 2011
 * Time: 4:47:13 PM
 */
@XmlRootElement(name="OffeneLeistung")
public class VertecOffeneLeistung extends VertecLeistung {

    @Override
    public boolean istVerrechnet() {
        return false;
    }
}
