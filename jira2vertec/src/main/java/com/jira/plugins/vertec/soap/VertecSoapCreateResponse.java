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

package com.jira.plugins.vertec.soap;

import com.jira.plugins.vertec.entities.*;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;

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
