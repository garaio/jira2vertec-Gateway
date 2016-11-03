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

package com.jira.plugins.vertec;

import com.jira.plugins.vertec.entities.*;
import com.jira.plugins.vertec.service.VertecServiceException;

import java.util.Date;
import java.util.List;

public interface VertecRepository {
    List<VertecProject> getAllProjectsAktiv();

    VertecProject getProjekt(String phaseId);

    List<VertecPhase> getProjektPhasenAktiv(String projektId);

    List<VertecPhase> getProjektPhasenAktivMitProjektbeschrieb(String projektId);

    VertecPhase getPhase(String phaseId);

    VertecPhase getPhaseMitProjektbeschrieb(String phaseId);

    List<VertecLeistung> getLeistungen(String jiraReferenz);
    
    List<VertecOffeneLeistung> getOffeneLeistungen(String jiraReferenz);

    VertecProjektBearbeiter getBenutzer(String loginName);

    VertecDateTimeProperty getSperrdatum();

    VertecFreigabe getPersFreigabedatum(String user);

    void updateLeistung(String leistungId, int minuten, Date date, String vertecPhaseId, String comment);

    void createLeistung(int minuten, String jiraReferenz, String benutzerId, String vertecPhaseId, Date datum, String comment) throws VertecServiceException;

    void deleteLeistung(String leistungId) throws VertecServiceException;
}
