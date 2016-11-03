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
import com.jira.plugins.vertec.entities.VertecPhase;
import com.jira.plugins.vertec.entities.VertecProject;
import com.jira.plugins.vertec.soap.VertecSoapExpression;
import com.jira.plugins.vertec.soap.VertecSoapResultDef;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.text.Collator;
import java.util.Locale;

@XmlRootElement(name = "ProjektPhase")
public class RestPhase extends RestBaseEntity implements Comparable<RestPhase> {

    public RestPhase(VertecPhase vertecPhase, boolean zeigeProjektbeschrieb, int maxLength) {
        if (vertecPhase == null) {
            return;
        }
        
        String projektName = vertecPhase.getProjektName();

        if (zeigeProjektbeschrieb) {
            String beschrieb = vertecPhase.getProjektBeschrieb();
            projektBezeichnung =  String.format("%1s (%1s)", beschrieb.length() > maxLength ? beschrieb.substring(0, maxLength) :  beschrieb, projektName);
        } else {
            projektBezeichnung = projektName;
        }

        setId(vertecPhase.getId());
        setObjref(vertecPhase.getObjref());
        this.name = vertecPhase.getName();
        this.projektId = vertecPhase.getProjektId(); 
    }

    private String projektId;

    private String name;

    private String projektBezeichnung;

    @XmlElement(name = "bezeichnung")
    public String getName() {
        return name;
    }

    public String getProjektId() {
        return projektId;
    }

    public String getProjektBezeichnung() {
        return projektBezeichnung;
    }

    public int compareTo(RestPhase phase) {
        return Collator.getInstance(Locale.getDefault()).compare(name, phase.getName());
    }
}
