package com.garaio.jira.plugins.vertec.soap;

import com.garaio.jira.plugins.vertec.entities.*;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by IntelliJ IDEA.
 * User: mjr0177
 * Date: Sep 28, 2011
 * Time: 2:38:07 PM
 */
@XmlRootElement(name = "Update")
public class VertecSoapUpdate<T>
{
    private T item;

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
    @XmlAnyElement
    public void setItem(T item)
    {
        this.item = item;
    }
}
