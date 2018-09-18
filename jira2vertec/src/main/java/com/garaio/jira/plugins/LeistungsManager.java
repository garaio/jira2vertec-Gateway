package com.garaio.jira.plugins;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.worklog.Worklog;
import com.garaio.jira.plugins.configuration.JiraToVertecConfiguration;
import com.garaio.jira.plugins.vertec.VertecRepository;
import com.garaio.jira.plugins.vertec.entities.*;
import com.garaio.jira.plugins.vertec.service.VertecServiceException;
import org.apache.log4j.Logger;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: mjr0177
 * Date: Sep 27, 2011
 * Time: 8:30:16 AM
 */
public class LeistungsManager
{

    // Es sollte eigentlich das Interface CacheFactory verwendet werden, dieses ist aber aktuell buggy...
    // See https://jac-new.atlassian.com/browse/CONF-22424
    private CacheManager cacheFactory;
    private JiraToVertecConfiguration configuration;

    private VertecRepository repository;
    private static final String BENUTZER_CACHE_KEY = "BENUTZER_CACHE_KEY";

    private final static Logger logger = Logger.getLogger(LeistungsManager.class);

    public LeistungsManager(VertecRepository repository, CacheManager cacheFactory, JiraToVertecConfiguration configuration)
    {
        this.repository = repository;
        this.cacheFactory = cacheFactory;
        this.configuration = configuration;
    }

    private String getUserId(String jiraLogin)
    {
        Cache<String, VertecProjektBearbeiter> benutzerCache = null;
        VertecProjektBearbeiter benutzer = null;
        if (configuration.isCachingEnabled())
        {
            benutzerCache = cacheFactory.getCache(BENUTZER_CACHE_KEY, String.class, VertecProjektBearbeiter.class);
            benutzer = benutzerCache.get(jiraLogin);
        }
        if (benutzer == null)
        {
            benutzer = repository.getBenutzer(jiraLogin);
            if (benutzer != null)
            {
                if (configuration.isCachingEnabled() && benutzerCache != null)
                {
                    benutzerCache.put(jiraLogin, benutzer);
                }
            }
            else
            {
                return null;
            }
        }
        return benutzer.getId();
    }

    private void neueLeitungErstellen(int minutesSpent, String worklogId, String worklogAuthor, String comment, Date worklogDate, String vertecPhaseId) throws VertecServiceException
    {
       String benutzerId = getUserId(worklogAuthor);

       if (benutzerId != null) {
        String bebuchbarePhaseId = getBuchbarePhase(vertecPhaseId, worklogDate, worklogAuthor);
        Date worklogDateOnly = getDateOnly(worklogDate);
        repository.createLeistung(minutesSpent, worklogId, benutzerId, bebuchbarePhaseId, worklogDateOnly, comment);
       }
    }

    private void updateLeistung(String leistungId, int minuten, Date date, String vertecPhaseId, String worklogText)
    {
        if (vertecPhaseId == null)
            vertecPhaseId = configuration.getDefaultPhaseIdWennNichtZugeordnet();

        repository.updateLeistung(leistungId, minuten, date, vertecPhaseId, worklogText);
    }

    public String erstelleWorklogText(Worklog worklog)
    {
        Issue issue = worklog.getIssue();
        Issue parent = issue.getParentObject();
        String worklogComment = worklog.getComment();

        StringBuilder text = new StringBuilder(parent == null ? "" : parent.getKey() + " : ");
        text.append(String.format("%s : %s", issue.getKey(), issue.getSummary()));

        if ( worklogComment != null && worklogComment.length() > 0) {
            text.append(" : ").append(worklogComment);
        }

        return text.toString();
    }

    public void leistungAktualisieren(Worklog worklog, String vertecPhaseId) throws VertecServiceException
    {
        logDebug("Beginne Worklow zum Aktualisieren/Erstellen der folgenden Leistung", worklog, vertecPhaseId);
        leistungAktualisieren(worklog, vertecPhaseId, worklog.getTimeSpent().intValue());
    }

    /**
     * Aktualisiert eine Leistung, wobei die zu verbuchende Zeit mitgegeben werden kann.
     * 
     * @param worklog
     * @param vertecPhaseId
     * @param timeSpent
     * @throws VertecServiceException
     */
    private void leistungAktualisieren(Worklog worklog, String vertecPhaseId, int timeSpent) throws VertecServiceException
    {
        List<VertecLeistung> leistungen = getLeistungen(worklog.getId().toString());
        //Gibt es eine Leistung die angepasst werden kann?
        VertecLeistung offeneLeistung = getFirstOffeneLeistung(leistungen, worklog.getAuthorObject().getUsername());

        //Unterscheiden sich die Phasen der offenen und mutierten Leistungen muss sichergestellt werden, dass letztere buchbar ist
        if (offeneLeistung != null && !offeneLeistung.getPhaseId().equals(vertecPhaseId)) {
            vertecPhaseId = getBuchbarePhase(vertecPhaseId, offeneLeistung.getDatum(), worklog.getAuthorObject().getUsername());
        }
        
        leistungAktualisieren(worklog, vertecPhaseId, timeSpent, leistungen, offeneLeistung);
    }

    /**
     * Aktualisiert alle offene Leistung nachdem bei einem Issue die Phase ge�ndert wurde.
     * Da hier andere �berpr�fungen n�tig sind als bei einer Mutation einer Leistung wird dies separat abgehandelt.
     *
     * @param worklog
     * @param vertecPhaseId
     * @throws VertecServiceException
     */
    public void leistungAktualisierenPhasenWechsel(Worklog worklog, String vertecPhaseId) throws VertecServiceException
    {
        logDebug("Beginne mit Aktualisierung der folgenden Leistung nach Mutation des Jira-Issues", worklog, vertecPhaseId);
        List<VertecOffeneLeistung> offeneLeistungen = getOffeneLeistungen(worklog.getId().toString());

        for (VertecOffeneLeistung offeneLeistung : offeneLeistungen) {
            if (isDatumVorSperrdatum(offeneLeistung.getDatum())) {
                //Wenn das Sperrdatum verletzt wird, wird der Phasenwechsel ignoriert
                logDebug("Leistung kann nicht aktualisiert werden, da das Sperrdatum verletzt wird", worklog, vertecPhaseId);

            } else {
                updateLeistung(offeneLeistung.getId(), offeneLeistung.getMinuten(), getDateOnly(worklog.getStartDate()), vertecPhaseId, offeneLeistung.getText());
            }
        }
    }

    /**
     * Liest alle Leistung anhand einer worklogId aus. Dabei spielt es keine Rolle, ob die Leistung bereits verrechnet wurde
     * @param worklogId Zur Identifikation der Leistungen
     * @return Die Liste aller gefundenen Leistungen
     */
    private List<VertecLeistung> getLeistungen(String worklogId)
    {
        List<VertecLeistung> leistungen = repository.getLeistungen(worklogId);

        Collections.sort(leistungen, VertecLeistung.NEUSTE_LEISTUNG_ZUERST_COMPARATOR);
        return leistungen;
    }
       
    /**
     * Liest alle offene (nicht verrechneten) Leistung anhand einer worklogId aus.
     * @param worklogId Zur Identifikation der Leistungen
     * @return Die Liste aller gefundenen Leistungen
     */
    private List<VertecOffeneLeistung> getOffeneLeistungen(String worklogId)
    {
        List<VertecOffeneLeistung> leistungen = repository.getOffeneLeistungen(worklogId);

        Collections.sort(leistungen, VertecLeistung.NEUSTE_LEISTUNG_ZUERST_COMPARATOR);
        return leistungen;
    }
    
    private void leistungAktualisieren(Worklog worklog, String vertecPhaseId, int timeSpent, List<VertecLeistung> leistungen, VertecLeistung offeneLeistung) throws VertecServiceException
    {
        int minutesSpent = timeSpent / 60;
        Date worklogDate = getDateOnly(worklog.getStartDate());

        int bereitsErfassteMinuten = summeDerLeistungen(leistungen);
        int differenzMinuten = minutesSpent - bereitsErfassteMinuten;

        String worklogText = erstelleWorklogText(worklog);

        //Gibt es eine Leistung die angepasst werden kann?
        if (offeneLeistung != null) {
            int neueAnzahlMinuten = offeneLeistung.getMinuten() + differenzMinuten;

            if (neueAnzahlMinuten == 0) {
                repository.deleteLeistung(offeneLeistung.getId());
            } else if (!offeneLeistung.getText().equals(worklogText) || !offeneLeistung.getDatum().equals(worklogDate)
                            || differenzMinuten != 0  || !offeneLeistung.getPhaseId().equals(vertecPhaseId)) {
                updateLeistung(offeneLeistung.getId(), neueAnzahlMinuten, worklogDate, vertecPhaseId, worklogText);
            }
        }
        else {
            neueLeitungErstellen(differenzMinuten, worklog.getId().toString(), worklog.getAuthorObject().getUsername(), worklogText, worklogDate, vertecPhaseId);
        }

        //Alle offenen Leistungen m�ssen das neue Datum sowie den neuen Kommentar haben...
        for (VertecLeistung leistung : leistungen)
        {
            if (leistung != offeneLeistung && !leistung.istVerrechnet() && (!leistung.getDatum().equals(worklogDate) || !leistung.getText().equals(worklogText))) {
                logDebug(String.format("Aktualisiere zusaetzliche offene Leistung mit ID '%s' und", leistung.getId()), worklog, vertecPhaseId);
                updateLeistung(leistung.getId(), leistung.getMinuten(), worklogDate, vertecPhaseId, worklogText);
            }
        }
    }

    /**
     * Verifiziert, dass die gegebene Phase bebuchbar ist, und retourniert allenfalls eine andere phasenId
     * @param requestedPhaseId
     * @param worklogDate
     * @return
     */
    private String getBuchbarePhase(String requestedPhaseId, Date worklogDate, String user)
    {
        // Wird eine Arbeit vor dem Sperrdatum oder dem pers�nlichen Freigabedatum verbucht, dann wird konfigurierte Phase verwendet.
        VertecFreigabe persFreigabe = repository.getPersFreigabedatum(user);
        if (persFreigabe != null && persFreigabe.getBis() != null && !worklogDate.after(persFreigabe.getBis())) {
            if (logger.isInfoEnabled())
                logger.info(String.format("[JiraToVertec] Worklog vor persoenlicher Freigabe (Sperrdatum nicht relevant). Verwendete buchbare Phase: %s", configuration.getDefaultPhaseIdWennNachbearbeitungNoetig()));
            return configuration.getDefaultPhaseIdWennNachbearbeitungNoetig();
        }
        
        if (isDatumVorSperrdatum(worklogDate)) {
            if (logger.isInfoEnabled())
                logger.info(String.format("[JiraToVertec] Worklog vor Sperrdatum. Verwendete buchbare Phase: %s", configuration.getDefaultPhaseIdWennNachbearbeitungNoetig()));
            return configuration.getDefaultPhaseIdWennNachbearbeitungNoetig();
        }
        
        VertecPhase requestedPhase = repository.getPhase(requestedPhaseId);
        if (requestedPhase == null) {
            if (logger.isInfoEnabled())
                logger.info(String.format("[JiraToVertec] Keine Phase zugewiesen oder vorhanden. Verwendete buchbare Phase: %s", configuration.getDefaultPhaseIdWennNichtZugeordnet()));
            return configuration.getDefaultPhaseIdWennNichtZugeordnet();    
        } else if (!requestedPhase.isAktivInklusiveProjekt()) {
            if (logger.isInfoEnabled())
                logger.info(String.format("[JiraToVertec] Phase mit der ID '%s' ist nicht aktiv. Verwendete buchbare Phase: %s", requestedPhaseId, configuration.getDefaultPhaseIdWennNachbearbeitungNoetig()));
            return configuration.getDefaultPhaseIdWennNachbearbeitungNoetig();
        }
        return requestedPhaseId;
    }

    /**
     * �berpr�ft, ob das Datum vor dem Sperrdatum liegt
     * @param date das zu �berp�fende datum
     * @return true, wenn das Datum vor dem Sperrdatum ist, ansonsten false
     */
    private boolean isDatumVorSperrdatum(Date date) {
        VertecDateTimeProperty sperrdatum = repository.getSperrdatum();
        return sperrdatum != null && sperrdatum.getPropertyValue() != null && !date.after(sperrdatum.getPropertyValue());
    }

    private Date getDateOnly(Date date)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        return cal.getTime();
    }

    /**
     * Durchsucht eine Liste von Leistungen und retourniert die 1., welche nicht verrechnet ist.
     * Zus�tzlich wird sichergestellt, dass die diese das Sperrdatum nicht verletzt und auf eine g�ltige / aktive Phase zugewiesen hat.
     *
     * @param leistungen Die Liste der Leistungen
     * @param user K�rzel des Users
     * @return Die 1. gefundene offene Leistung, null wenn keine vorhanden ist
     */
    private VertecLeistung getFirstOffeneLeistung(List<VertecLeistung> leistungen, String user)
    {
        for (VertecLeistung leistung : leistungen)
        {
            // Leistung muss noch ver�nderbar sein
            if (!leistung.istVerrechnet() && getBuchbarePhase(leistung.getPhaseId(), leistung.getDatum(), user).equals(leistung.getPhaseId()))
                return leistung;
        }
        return null;
    }

    private int summeDerLeistungen(List<VertecLeistung> leistungen)
    {
        int summe = 0;
        for (VertecLeistung leistung : leistungen)
        {
            summe += leistung.getMinuten();
        }
        return summe;
    }

    public void leistungErstellen(Worklog worklog, String vertecPhaseId) throws VertecServiceException {
        logDebug("Beginne Workflow zum Erstellen der folgenden Leistung", worklog, vertecPhaseId);
        // dies ist ein Spezialfall des Aktualisierens (keine bestehende Leistung in vertec)
        leistungAktualisieren(worklog, vertecPhaseId);
    }

    public void leistungLoeschen(Worklog worklog, String vertecPhaseId) throws VertecServiceException {
        logDebug("Beginne Workflow zum Loeschen der folgenden Leistung", worklog, vertecPhaseId);
        // dies ist ein Spezialfall des Aktualisierens (keine bestehende Leistung in vertec)
        leistungAktualisieren(worklog, vertecPhaseId, 0);
    }

    private void logDebug(String logText, Worklog worklog, String vertecPhaseId) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("[JiraToVertec] %s: VertecPhasenId: '%s', IssueKey: '%s'", logText, vertecPhaseId, worklog.getIssue().getKey()));
        }
    }
}
