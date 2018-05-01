package com.garaio.jira.plugins.vertec;

import com.garaio.jira.plugins.vertec.entities.*;
import com.garaio.jira.plugins.vertec.service.VertecServiceException;

import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: mjr0177_2
 * Date: Sep 21, 2011
 * Time: 10:51:38 AM
 * To change this template use File | Settings | File Templates.
 */
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
