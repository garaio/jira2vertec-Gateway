package com.garaio.jira.plugins.vertec.soap;

import com.garaio.jira.plugins.vertec.entities.*;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: mjr0177
 * Date: Sep 28, 2011
 * Time: 4:09:33 PM
 */
@XmlRootElement(name = "QueryResponse")
public class VertecSoapQueryResponse<T>
{
    @XmlElementRefs({
            @XmlElementRef(name = "OffeneLeistung", type = VertecOffeneLeistung.class),
            @XmlElementRef(name = "VerrechneteLeistung", type = VertecVerrechneteLeistung.class),
            @XmlElementRef(name = "ProjektBearbeiter", type = VertecProjektBearbeiter.class),
            @XmlElementRef(name = "Projekt", type = VertecProject.class),
            @XmlElementRef(name = "ProjektPhase", type = VertecPhase.class),
            @XmlElementRef(name = "DateTimeProperty", type = VertecDateTimeProperty.class),
            @XmlElementRef(name = "Freigabe", type = VertecFreigabe.class)})
    private List<T> items;

    public List<T> getItems()
    {
        return items;
    }
}
