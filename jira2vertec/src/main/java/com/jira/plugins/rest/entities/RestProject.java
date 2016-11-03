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

package com.jira.plugins.rest.entities;

import com.jira.plugins.vertec.entities.VertecBaseEntity;
import com.jira.plugins.vertec.entities.VertecProject;
import com.jira.plugins.vertec.soap.VertecSoapResultDef;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.text.Collator;
import java.util.Locale;

@XmlRootElement(name = "Projekt")
public class RestProject extends RestBaseEntity implements Comparable<RestProject>
{
    public RestProject(VertecProject vertecProject, boolean zeigeProjektbeschrieb, int maxLength) {
        if (vertecProject == null) {
            return;    
        }

        String name = vertecProject.getName();
        
        if (zeigeProjektbeschrieb) {
            String beschrieb = vertecProject.getBeschrieb();
            bezeichnung =  String.format("%1s (%1s)", beschrieb.length() > maxLength ? beschrieb.substring(0, maxLength) :  beschrieb, name);
        } else {
            bezeichnung = name;
        }

        setId(vertecProject.getId());
        setObjref(vertecProject.getObjref());
    }

    @XmlElement(name = "bezeichnung")
    private String bezeichnung;

    public String getBezeichnung() {
        return bezeichnung;
    }

    public int compareTo(RestProject projekt) {
        return Collator.getInstance(Locale.getDefault()).compare(bezeichnung, projekt.getBezeichnung());          
    }
}
