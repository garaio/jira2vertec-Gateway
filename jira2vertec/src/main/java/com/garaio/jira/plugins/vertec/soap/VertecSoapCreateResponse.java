package com.garaio.jira.plugins.vertec.soap;

import com.garaio.jira.plugins.vertec.entities.*;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by IntelliJ IDEA.
 * User: mjr0177
 * Date: Sep 28, 2011
 * Time: 4:09:18 PM
 */
@XmlRootElement(name="CreateResponse")
public class VertecSoapCreateResponse<T>
{
    public T getItem()
    {
        return item;
    }

    @XmlElementRefs({
            @XmlElementRef(name = "OffeneLeistung", type = VertecOffeneLeistung.class),
            @XmlElementRef(name = "VerrechneteLeistung", type = VertecVerrechneteLeistung.class),
            @XmlElementRef(name = "ProjektBearbeiter", type = VertecProjektBearbeiter.class),
            @XmlElementRef(name = "Projekt", type = VertecProject.class),
            @XmlElementRef(name = "ProjektPhase", type = VertecPhase.class)})
    private T item;
}
