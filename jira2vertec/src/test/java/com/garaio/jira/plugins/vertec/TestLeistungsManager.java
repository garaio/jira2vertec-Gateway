package com.garaio.jira.plugins.vertec;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.worklog.Worklog;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.LazyLoadingApplicationUser;
import com.garaio.jira.plugins.LeistungsManager;
import com.garaio.jira.plugins.configuration.JiraToVertecConfiguration;
import com.garaio.jira.plugins.vertec.entities.*;
import com.garaio.jira.plugins.vertec.service.VertecServiceException;
import junit.framework.TestCase;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by IntelliJ IDEA.
 * User: mjr0177
 * Date: Sep 27, 2011
 * Time: 10:24:34 AM
 */
public class TestLeistungsManager extends TestCase
{

    @Mock
    private VertecRepository repository;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache<String, VertecProjektBearbeiter> cache;

    @Mock
    private Issue issue;

    @Mock
    private Issue parentIssue;

    private JiraToVertecConfiguration config = new TestConfigurationImpl();
    private LeistungsManager leistungsManager;
    private VertecProjektBearbeiter bearbeiterMjr;
    private VertecPhase vertecPhase;
    private ArrayList<VertecLeistung> leistungen;
    private ArrayList<VertecOffeneLeistung> offeneLeistungen;
    private VertecOffeneLeistung leistung2;
    private VertecOffeneLeistung leistung1;
    private VertecVerrechneteLeistung verrechneteLeistung;
    private VertecDateTimeProperty sperrdatum;
    private VertecFreigabe persFreigabeDatum;
    private Date defaultDate;
    private Long defaultWorkLogId;
    private String defaultComment;

    private String worklogText;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        initMocks(this);

        when(cacheManager.getCache(anyString(), eq(String.class), eq(VertecProjektBearbeiter.class))).thenReturn(cache);

        leistungsManager = new LeistungsManager(repository, cacheManager, config);

        bearbeiterMjr = new VertecProjektBearbeiter();
        bearbeiterMjr.setId("147258369");
        bearbeiterMjr.setLoginName("mjr0177");
        bearbeiterMjr.setIsActive(true);

        vertecPhase = new VertecPhase();
        vertecPhase.setId("111222");
        vertecPhase.setAktiv(1);
        vertecPhase.setProjektAktiv("Y");

        defaultDate = getDate(2011, 10, 1);
        defaultWorkLogId = 4564654L;
        defaultComment = "Kommentar1";

        worklogText = "PRO-1 : PRO-10 : Summary 1 : " + defaultComment;

        leistungen = new ArrayList<VertecLeistung>();
        offeneLeistungen = new ArrayList<VertecOffeneLeistung>();
        leistung1 = new VertecOffeneLeistung();
        leistung1.setId("Leistung1");
        leistung1.setMinuten(10);
        leistung1.setPhaseId(vertecPhase.getId());
        leistung1.setDatum(defaultDate);
        leistung1.setText(worklogText);
        leistung1.setBearbeiterId("mjr0177");

        leistung2 = new VertecOffeneLeistung();
        leistung2.setId("Leistung2");
        leistung2.setMinuten(80);
        leistung2.setPhaseId(vertecPhase.getId());
        leistung2.setDatum(defaultDate);
        leistung2.setText(worklogText);
        leistung2.setBearbeiterId("mjr0177");

        verrechneteLeistung = new VertecVerrechneteLeistung();
        verrechneteLeistung.setId("VerrechneteLeistung");
        verrechneteLeistung.setMinuten(80);
        verrechneteLeistung.setPhaseId(vertecPhase.getId());
        verrechneteLeistung.setDatum(defaultDate);
        verrechneteLeistung.setText(worklogText);

        sperrdatum = new VertecDateTimeProperty();
        sperrdatum.setPropertyValue(getDate(2011, 9, 1));
        when(repository.getSperrdatum()).thenReturn(sperrdatum);

        persFreigabeDatum = new VertecFreigabe();
        persFreigabeDatum.setBis(getDate(2011, 8, 1));
        when(repository.getPersFreigabedatum(bearbeiterMjr.getLoginName())).thenReturn(persFreigabeDatum);

        when(issue.getParentObject()).thenReturn(parentIssue);
        when(parentIssue.getKey()).thenReturn("PRO-1");
        when(issue.getKey()).thenReturn("PRO-10");
        when(issue.getSummary()).thenReturn("Summary 1");

    }

    public void testNeueLeistungKannErfasstWerden() throws VertecServiceException
    {
        setupPhaseLeistungBearbeiter(bearbeiterMjr, vertecPhase);

        Date date = getDate(2012, 1, 1, 13, 25);
        Worklog worklog = CreateWorklog(bearbeiterMjr.getLoginName(), 57, defaultWorkLogId, date, defaultComment);
        leistungsManager.leistungAktualisieren(worklog, vertecPhase.getId());

        verify(repository).createLeistung(57, defaultWorkLogId.toString(), bearbeiterMjr.getId(), vertecPhase.getId(), getDate(2012, 1, 1), worklogText);
    }

    public void testNeueLeistungWirdNichtVerbuchtWennDerBenutzerUnbekanntIst() throws VertecServiceException
    {
        when(repository.getBenutzer(bearbeiterMjr.getLoginName())).thenReturn(null);
        when(repository.getPhase(vertecPhase.getId())).thenReturn(vertecPhase);

        Worklog worklog = CreateWorklog(bearbeiterMjr.getLoginName(), 57, defaultWorkLogId, defaultDate, defaultComment);
        leistungsManager.leistungErstellen(worklog, vertecPhase.getId());

        verify(repository, never()).createLeistung(anyInt(), anyString(), anyString(), anyString(), any(Date.class), anyString());
    }

    public void testNeueLeistungWirdNichtVerbuchtWennDerBenutzerDeaktviertIst() throws VertecServiceException
    {
        bearbeiterMjr.setIsActive(false);
        when(repository.getPhase(vertecPhase.getId())).thenReturn(vertecPhase);

        Worklog worklog = CreateWorklog(bearbeiterMjr.getLoginName(), 57, defaultWorkLogId, defaultDate, defaultComment);
        leistungsManager.leistungErstellen(worklog, vertecPhase.getId());

        verify(repository, never()).createLeistung(anyInt(), anyString(), anyString(), anyString(), any(Date.class), anyString());
    }

    public void testNeueLeistungWirdMitDefaultPhaseErfasstWennDiePhaseNichtAktivIst() throws VertecServiceException
    {
        vertecPhase.setAktiv(0);
        setupPhaseLeistungBearbeiter(bearbeiterMjr, vertecPhase);

        Worklog worklog = CreateWorklog(bearbeiterMjr.getLoginName(), 57, defaultWorkLogId, defaultDate, defaultComment);
        leistungsManager.leistungAktualisieren(worklog, vertecPhase.getId());

        verify(repository).createLeistung(57, defaultWorkLogId.toString(), bearbeiterMjr.getId(), config.getDefaultPhaseIdWennNachbearbeitungNoetig(), defaultDate, worklogText);
    }

    public void testNeueLeistungWirdMitDefaultPhaseErfasstWennDiePhaseNichtExistiert() throws VertecServiceException
    {
        when(repository.getBenutzer(bearbeiterMjr.getLoginName())).thenReturn(bearbeiterMjr);
        when(repository.getPhase(anyString())).thenReturn(null);

        Worklog worklog = CreateWorklog(bearbeiterMjr.getLoginName(), 57, defaultWorkLogId, defaultDate, defaultComment);
        leistungsManager.leistungAktualisieren(worklog, vertecPhase.getId());

        verify(repository).createLeistung(57, defaultWorkLogId.toString(), bearbeiterMjr.getId(), config.getDefaultPhaseIdWennNichtZugeordnet(), defaultDate, worklogText);
    }

    public void testNeueLeistungWirdMitDefaultPhaseErfasstWennKeinePhaseZugewiesen() throws VertecServiceException
    {
        when(repository.getBenutzer(bearbeiterMjr.getLoginName())).thenReturn(bearbeiterMjr);
        when(repository.getPhase(anyString())).thenReturn(null);

        Worklog worklog = CreateWorklog(bearbeiterMjr.getLoginName(), 57, defaultWorkLogId, defaultDate, defaultComment);
        leistungsManager.leistungAktualisieren(worklog, null);

        verify(repository).createLeistung(57, defaultWorkLogId.toString(), bearbeiterMjr.getId(), config.getDefaultPhaseIdWennNichtZugeordnet(), defaultDate, worklogText);
    }

    public void testLeistungWirdAktualisiertBeiMutationVerbuchterZeitWennKeinePhaseZugewiesen() throws VertecServiceException
    {
        leistung1.setPhaseId(config.getDefaultPhaseIdWennNichtZugeordnet());
        leistungen.add(leistung1);
        setupPhaseLeistungBearbeiter(bearbeiterMjr, vertecPhase);
        when(repository.getBenutzer(bearbeiterMjr.getLoginName())).thenReturn(bearbeiterMjr);

        Worklog worklog = CreateWorklog(bearbeiterMjr.getLoginName(), 120, defaultWorkLogId, defaultDate, defaultComment);
        leistungsManager.leistungAktualisieren(worklog, null);

        verify(repository).updateLeistung(leistung1.getId(), 120, defaultDate, config.getDefaultPhaseIdWennNichtZugeordnet(), worklogText);
    }

    public void testLeistungWirdAktualisiertBeiMutationDatumWennKeinePhaseZugewiesen() throws VertecServiceException
    {
        leistung1.setPhaseId(config.getDefaultPhaseIdWennNichtZugeordnet());
        leistung1.setMinuten(120);
        leistungen.add(leistung1);
        setupPhaseLeistungBearbeiter(bearbeiterMjr, vertecPhase);
        when(repository.getBenutzer(bearbeiterMjr.getLoginName())).thenReturn(bearbeiterMjr);

        Worklog worklog = CreateWorklog(bearbeiterMjr.getLoginName(), 120, defaultWorkLogId, defaultDate, "Ein anderer Text");
        leistungsManager.leistungAktualisieren(worklog, null);

        verify(repository).updateLeistung(leistung1.getId(), 120, defaultDate, config.getDefaultPhaseIdWennNichtZugeordnet(), "PRO-1 : PRO-10 : Summary 1 : Ein anderer Text");
    }

    public void testLeistungWirdAktualisiertBeiMutationKommentarWennKeinePhaseZugewiesen() throws VertecServiceException
    {
        leistung1.setPhaseId(config.getDefaultPhaseIdWennNichtZugeordnet());
        leistung1.setMinuten(120);                   
        leistungen.add(leistung1);
        setupPhaseLeistungBearbeiter(bearbeiterMjr, vertecPhase);
        when(repository.getBenutzer(bearbeiterMjr.getLoginName())).thenReturn(bearbeiterMjr);

        Calendar initialCalendar = Calendar.getInstance();

        Worklog worklog = CreateWorklog(bearbeiterMjr.getLoginName(), 120, defaultWorkLogId, initialCalendar.getTime(), defaultComment);
        leistungsManager.leistungAktualisieren(worklog, null);

        Calendar newCalendar = Calendar.getInstance();
        newCalendar.set(Calendar.YEAR, initialCalendar.get(Calendar.YEAR));
        newCalendar.set(Calendar.MONTH, initialCalendar.get(Calendar.MONTH));
        newCalendar.set(Calendar.DATE, initialCalendar.get(Calendar.DATE));
        newCalendar.set(Calendar.HOUR_OF_DAY, 0);
        newCalendar.set(Calendar.MINUTE, 0);
        newCalendar.set(Calendar.SECOND, 0);
        newCalendar.set(Calendar.MILLISECOND, 0);

        verify(repository).updateLeistung(leistung1.getId(), 120, newCalendar.getTime(), config.getDefaultPhaseIdWennNichtZugeordnet(), worklogText);
    }

    public void testNeueLeitungVerwendetDenCache() throws VertecServiceException
    {
        when(cache.get("xxx")).thenReturn(bearbeiterMjr);
        when(repository.getPhase(anyString())).thenReturn(null);

        Worklog worklog = CreateWorklog("xxx", 57, defaultWorkLogId, defaultDate, defaultComment);
        leistungsManager.leistungAktualisieren(worklog, "myPhase");

        verify(repository).createLeistung(57, defaultWorkLogId.toString(), bearbeiterMjr.getId(), config.getDefaultPhaseIdWennNichtZugeordnet(), defaultDate, worklogText);
        verify(repository, never()).getBenutzer(anyString());
    }

    public void testNichtVerrechneteLeistungKannVerlaengertWerden() throws VertecServiceException
    {
        leistungen.add(leistung1);
        setupPhaseLeistungBearbeiter(bearbeiterMjr, vertecPhase);

        Worklog worklog = CreateWorklog(bearbeiterMjr.getLoginName(), 100, defaultWorkLogId, defaultDate, defaultComment);
        leistungsManager.leistungAktualisieren(worklog, vertecPhase.getId());

        verify(repository).updateLeistung(leistung1.getId(), 100, defaultDate, vertecPhase.getId(), worklogText);
    }

    public void testNurDieNeusteLeistungWirdVerlaengert() throws VertecServiceException
    {
        leistung2.setDatum(getDate(2001, 1, 1));
        leistungen.add(leistung1);
        leistungen.add(leistung2);
        setupPhaseLeistungBearbeiter(bearbeiterMjr, vertecPhase);

        Worklog worklog = CreateWorklog(bearbeiterMjr.getLoginName(), 100, defaultWorkLogId, defaultDate, defaultComment);
        leistungsManager.leistungAktualisieren(worklog, vertecPhase.getId());

        verify(repository).updateLeistung(leistung1.getId(), 20, defaultDate, vertecPhase.getId(), worklogText);
    }

    public void testBereitsKorrekteLeistungWirdNichtAktualisiert() throws VertecServiceException
    {
        leistung1.setMinuten(20);
        leistungen.add(leistung1);
        leistungen.add(leistung2);

        setupPhaseLeistungBearbeiter(bearbeiterMjr, vertecPhase);

        Date date = getDate(2011, 10, 1, 3, 45);
        Worklog worklog = CreateWorklog(bearbeiterMjr.getLoginName(), 100, defaultWorkLogId, date, defaultComment);
        leistungsManager.leistungAktualisieren(worklog, vertecPhase.getId());

        verify(repository, never()).updateLeistung(anyString(), anyInt(), any(Date.class), anyString(), anyString());
    }

    public void testLeistungAktualisierenVerbuchteZeit() throws VertecServiceException
    {
        leistungen.add(leistung1);
        leistungen.add(leistung2);

        setupPhaseLeistungBearbeiter(bearbeiterMjr, vertecPhase);

        Worklog worklog = CreateWorklog(bearbeiterMjr.getLoginName(), 45, defaultWorkLogId, defaultDate, defaultComment);
        leistungsManager.leistungAktualisieren(worklog, vertecPhase.getId());

        verify(repository).updateLeistung(leistung1.getId(), -35, defaultDate, vertecPhase.getId(), worklogText);
    }

    public void testLeistungAktualisierenKommentar() throws VertecServiceException
    {
        leistung1.setText("Alter Text!");
        leistungen.add(leistung1);

        setupPhaseLeistungBearbeiter(bearbeiterMjr, vertecPhase);

        Worklog worklog = CreateWorklog(bearbeiterMjr.getLoginName(), leistung1.getMinuten(), defaultWorkLogId, defaultDate, defaultComment);
        leistungsManager.leistungAktualisieren(worklog, vertecPhase.getId());

        verify(repository).updateLeistung(leistung1.getId(), leistung1.getMinuten(), defaultDate, vertecPhase.getId(), worklogText);
    }

    public void testLeistungAktualisierenDatum() throws VertecServiceException
    {
        leistungen.add(leistung1);

        setupPhaseLeistungBearbeiter(bearbeiterMjr, vertecPhase);

        Date date = getDate(2011, 10, 11);
        Worklog worklog = CreateWorklog(bearbeiterMjr.getLoginName(), leistung1.getMinuten(), defaultWorkLogId, date, defaultComment);
        leistungsManager.leistungAktualisieren(worklog, vertecPhase.getId());

        verify(repository).updateLeistung(leistung1.getId(), leistung1.getMinuten(), date, vertecPhase.getId(), worklogText);
    }


    public void testLeistungAktualisierenPhase() throws VertecServiceException
    {
        leistungen.add(leistung1);

        VertecPhase vertecPhase9999 = new VertecPhase();
        vertecPhase9999.setId("9999");
        vertecPhase9999.setAktiv(1);
        vertecPhase9999.setProjektAktiv("Y");
        setupPhaseLeistungBearbeiter(bearbeiterMjr, vertecPhase9999);
        setupPhaseLeistungBearbeiter(bearbeiterMjr, vertecPhase);

        Worklog worklog = CreateWorklog(bearbeiterMjr.getLoginName(), leistung1.getMinuten(), defaultWorkLogId, defaultDate, defaultComment);
        leistungsManager.leistungAktualisieren(worklog, "9999");

        verify(repository).updateLeistung(leistung1.getId(), leistung1.getMinuten(), defaultDate, "9999", worklogText);
    }

    private void setupPhaseLeistungBearbeiter(VertecProjektBearbeiter bearbeiter, VertecPhase phase)
    {
        when(repository.getBenutzer(bearbeiter.getLoginName())).thenReturn(bearbeiter);
        when(repository.getLeistungen(defaultWorkLogId.toString())).thenReturn(leistungen);
        when(repository.getOffeneLeistungen(defaultWorkLogId.toString())).thenReturn(offeneLeistungen);
        if (phase == null)
            when(repository.getPhase(null)).thenReturn(null);
        else
            when(repository.getPhase(phase.getId())).thenReturn(phase);
    }

    public void testEsWirdDieOffeneLeistungAktualisiert() throws VertecServiceException
    {
        leistungen.add(leistung1);
        leistungen.add(verrechneteLeistung);
        setupPhaseLeistungBearbeiter(bearbeiterMjr, vertecPhase);

        Date newDate = getDate(2011, 12, 1);
        Worklog worklog = CreateWorklog(bearbeiterMjr.getLoginName(), 100, defaultWorkLogId, newDate, defaultComment);
        leistungsManager.leistungAktualisieren(worklog, vertecPhase.getId());

        verify(repository).updateLeistung(leistung1.getId(), 20, worklog.getStartDate(), vertecPhase.getId(), worklogText);
    }

    public void testEsWirdNachbearbeitungsPhaseAktualisiert() throws VertecServiceException
    {
        leistung1.setPhaseId(config.getDefaultPhaseIdWennNachbearbeitungNoetig());
        leistungen.add(leistung1);
        vertecPhase.setId(config.getDefaultPhaseIdWennNachbearbeitungNoetig());
        setupPhaseLeistungBearbeiter(bearbeiterMjr, vertecPhase);

        //Mutierte Phase ist inaktiv
        VertecPhase vertecPhaseInaktiv = new VertecPhase();
        vertecPhaseInaktiv.setId("1234");
        vertecPhaseInaktiv.setAktiv(0);
        vertecPhaseInaktiv.setProjektAktiv("Y");
        setupPhaseLeistungBearbeiter(bearbeiterMjr, vertecPhaseInaktiv);

        Worklog worklog = CreateWorklog(bearbeiterMjr.getLoginName(), 100, defaultWorkLogId, defaultDate, defaultComment);
        leistungsManager.leistungAktualisieren(worklog, "1234");

        verify(repository).updateLeistung(leistung1.getId(), 100, worklog.getStartDate(), config.getDefaultPhaseIdWennNachbearbeitungNoetig(), worklogText);
    }

    public void testEsWirdAktivePhaseAktualisiertObwohlOffeneLeistungsPhaseNachbearbeitenIst() throws VertecServiceException
    {
        leistung1.setPhaseId(config.getDefaultPhaseIdWennNachbearbeitungNoetig());
        leistungen.add(leistung1);
        vertecPhase.setId(config.getDefaultPhaseIdWennNachbearbeitungNoetig());
        setupPhaseLeistungBearbeiter(bearbeiterMjr, vertecPhase);

        //Mutierte Phase ist inaktiv
        VertecPhase vertecPhaseAktiv = new VertecPhase();
        vertecPhaseAktiv.setId("1234");
        vertecPhaseAktiv.setAktiv(1);
        vertecPhaseAktiv.setProjektAktiv("Y");
        setupPhaseLeistungBearbeiter(bearbeiterMjr, vertecPhaseAktiv);

        Worklog worklog = CreateWorklog(bearbeiterMjr.getLoginName(), 100, defaultWorkLogId, defaultDate, defaultComment);
        leistungsManager.leistungAktualisieren(worklog, "1234");

        verify(repository).updateLeistung(leistung1.getId(), 100, worklog.getStartDate(), vertecPhaseAktiv.getId(), worklogText);
    }

    public void testDasDatumWirdBeiAllenOffenenLeistungenAktualisiert() throws VertecServiceException
    {
        leistung1.setDatum(getDate(2011, 11, 1));
        leistung2.setDatum(getDate(2011, 10, 1));
        leistungen.add(leistung1);
        leistungen.add(leistung2);

        setupPhaseLeistungBearbeiter(bearbeiterMjr, vertecPhase);

        Date newDate = getDate(2011, 12, 1);
        Worklog worklog = CreateWorklog(bearbeiterMjr.getLoginName(), 100, defaultWorkLogId, newDate, defaultComment);
        leistungsManager.leistungAktualisieren(worklog, vertecPhase.getId());

        verify(repository).updateLeistung(leistung1.getId(), 20, newDate, vertecPhase.getId(), worklogText);
        verify(repository).updateLeistung(leistung2.getId(), 80, newDate, vertecPhase.getId(), worklogText);
    }

    public void testDerWorklogTextWirdBeiAllenOffenenLeistungenAktualisiert() throws VertecServiceException
    {
        leistung1.setText(worklogText + " und noch bla!");
        leistungen.add(leistung1);
        leistungen.add(leistung2);

        setupPhaseLeistungBearbeiter(bearbeiterMjr, vertecPhase);

        String text = "Moi, toi et le roi.";
        String updatedWorklogText = "PRO-1 : PRO-10 : Summary 1 : " + text;

        Worklog worklog = CreateWorklog(bearbeiterMjr.getLoginName(), 100, defaultWorkLogId, defaultDate, text);
        leistungsManager.leistungAktualisieren(worklog, vertecPhase.getId());

        verify(repository).updateLeistung(leistung1.getId(), 20, defaultDate, vertecPhase.getId(), updatedWorklogText);
        verify(repository).updateLeistung(leistung2.getId(), 80, defaultDate, vertecPhase.getId(), updatedWorklogText);
    }

    public void testDerWorklogTextUndDasDatumWerdenBeiAllenOffenenLeistungenAktualisiert() throws VertecServiceException
    {
        leistung1.setText(worklogText + " und noch bla!");
        leistung1.setDatum(getDate(2011, 11, 1));
        leistung2.setDatum(getDate(2011, 10, 1));
        leistungen.add(leistung1);
        leistungen.add(leistung2);

        setupPhaseLeistungBearbeiter(bearbeiterMjr, vertecPhase);

        String text = "Moi, toi et le roi.";
        String updatedWorklogText = "PRO-1 : PRO-10 : Summary 1 : " + text;

        Date newDate = getDate(2011, 12, 1);
        Worklog worklog = CreateWorklog(bearbeiterMjr.getLoginName(), 100, defaultWorkLogId, newDate, text);
        leistungsManager.leistungAktualisieren(worklog, vertecPhase.getId());

        verify(repository).updateLeistung(leistung1.getId(), 20, newDate, vertecPhase.getId(), updatedWorklogText);
        verify(repository).updateLeistung(leistung2.getId(), 80, newDate, vertecPhase.getId(), updatedWorklogText);
    }

    public void testMutationLeistungPhasenwechselMitNullPhase() throws VertecServiceException
    {
        leistung1.setPhaseId(null);
        offeneLeistungen.add(leistung1);
        setupPhaseLeistungBearbeiter(bearbeiterMjr, null);

        Worklog worklog = CreateWorklog(bearbeiterMjr.getLoginName(), leistung1.getMinuten(), defaultWorkLogId, defaultDate, defaultComment);
        leistungsManager.leistungAktualisierenPhasenWechsel(worklog, null);

       verify(repository).updateLeistung(leistung1.getId(), leistung1.getMinuten(), defaultDate, config.getDefaultPhaseIdWennNichtZugeordnet(), worklogText);
        }

    public void testMutationLeistungPhasenwechsel() throws VertecServiceException
    {
        String neuePhase = "phase778855";
        offeneLeistungen.add(leistung1);
        offeneLeistungen.add(leistung2);
        setupPhaseLeistungBearbeiter(bearbeiterMjr, vertecPhase);

        Worklog worklog = CreateWorklog(bearbeiterMjr.getLoginName(), leistung1.getMinuten(), defaultWorkLogId, defaultDate, defaultComment);
        leistungsManager.leistungAktualisierenPhasenWechsel(worklog, neuePhase);

        verify(repository, times(1)).updateLeistung(leistung1.getId(), leistung1.getMinuten(), defaultDate, neuePhase, worklogText);
        verify(repository, times(1)).updateLeistung(leistung2.getId(), leistung2.getMinuten(), defaultDate, neuePhase, worklogText);
    }

    public void testWennDiePhaseInaktivIstWirdEineKorrektuleistungInDieNachbearbeitungsPhaseGebucht() throws VertecServiceException
    {
        vertecPhase.setAktiv(0);
        setupPhaseLeistungBearbeiter(bearbeiterMjr, vertecPhase);

        Worklog worklog = CreateWorklog(bearbeiterMjr.getLoginName(), leistung1.getMinuten(), defaultWorkLogId, defaultDate, defaultComment);
        leistungsManager.leistungAktualisieren(worklog, vertecPhase.getId());

        verify(repository, never()).updateLeistung(anyString(), anyInt(), any(Date.class), anyString(), anyString());
        verify(repository).createLeistung(leistung1.getMinuten(), defaultWorkLogId.toString(), bearbeiterMjr.getId(), config.getDefaultPhaseIdWennNachbearbeitungNoetig(), defaultDate, worklogText);
    }

    public void testMutationLeistungPhasenwechselVorSperrdatumWirdIgnoriert() throws VertecServiceException
    {
        sperrdatum.setPropertyValue(getDate(2011, 11, 1));

        String neuePhase = "phase778855";
        offeneLeistungen.add(leistung1);
        leistung2.setDatum(getDate(2012, 2, 1));
        offeneLeistungen.add(leistung2);
        setupPhaseLeistungBearbeiter(bearbeiterMjr, vertecPhase);

        Worklog worklog = CreateWorklog(bearbeiterMjr.getLoginName(), leistung1.getMinuten(), defaultWorkLogId, defaultDate, defaultComment);
        leistungsManager.leistungAktualisierenPhasenWechsel(worklog, neuePhase);

        verify(repository, times(1)).updateLeistung(anyString(), anyInt(), any(Date.class), anyString(), anyString());
        verify(repository, times(1)).updateLeistung(leistung2.getId(), leistung2.getMinuten(), defaultDate, neuePhase, worklogText);
        verify(repository, never()).updateLeistung(leistung1.getId(), leistung1.getMinuten(), defaultDate, neuePhase, worklogText);
    }

    public void testMutationLeistungPhasenwechselNachSperrdatumWirdNichtIgnoriert() throws VertecServiceException
    {
        sperrdatum.setPropertyValue(getDate(2010, 10, 1));

        String neuePhase = "phase778855";
        offeneLeistungen.add(leistung1);
        setupPhaseLeistungBearbeiter(bearbeiterMjr, vertecPhase);

        Worklog worklog = CreateWorklog(bearbeiterMjr.getLoginName(), leistung1.getMinuten(), defaultWorkLogId, defaultDate, defaultComment);
        leistungsManager.leistungAktualisierenPhasenWechsel(worklog, neuePhase);

        verify(repository).updateLeistung(leistung1.getId(), leistung1.getMinuten(), defaultDate, neuePhase, worklogText);
    }

    public void testWennLeistungVorSperrdatumWirdEineKorrektuleistungInDieNachbearbeitungsPhaseGebucht() throws VertecServiceException
    {
        sperrdatum.setPropertyValue(getDate(2011, 11, 1));
        setupPhaseLeistungBearbeiter(bearbeiterMjr, vertecPhase);

        Worklog worklog = CreateWorklog(bearbeiterMjr.getLoginName(), 100, defaultWorkLogId, defaultDate, defaultComment);
        leistungsManager.leistungAktualisieren(worklog, vertecPhase.getId());

        verify(repository, never()).updateLeistung(anyString(), anyInt(), any(Date.class), anyString(), anyString());
        verify(repository).createLeistung(100, defaultWorkLogId.toString(), bearbeiterMjr.getId(), config.getDefaultPhaseIdWennNachbearbeitungNoetig(), defaultDate, worklogText);
    }

    public void testWennLeistungAmSperrdatumWirdEineKorrektuleistungInDieNachbearbeitungsPhaseGebucht() throws VertecServiceException
    {
        sperrdatum.setPropertyValue(defaultDate);
        setupPhaseLeistungBearbeiter(bearbeiterMjr, vertecPhase);

        Worklog worklog = CreateWorklog(bearbeiterMjr.getLoginName(), 100, defaultWorkLogId, defaultDate, defaultComment);
        leistungsManager.leistungAktualisieren(worklog, vertecPhase.getId());

        verify(repository, never()).updateLeistung(anyString(), anyInt(), any(Date.class), anyString(), anyString());
        verify(repository).createLeistung(100, defaultWorkLogId.toString(), bearbeiterMjr.getId(), config.getDefaultPhaseIdWennNachbearbeitungNoetig(), defaultDate, worklogText);
    }

    public void testWennLeistungVorPersFreigabeWirdEineKorrektuleistungInDieNachbearbeitungsPhaseGebucht() throws VertecServiceException
    {
        persFreigabeDatum.setBis(getDate(2011, 11, 1));
        setupPhaseLeistungBearbeiter(bearbeiterMjr, vertecPhase);

        Worklog worklog = CreateWorklog(bearbeiterMjr.getLoginName(), 100, defaultWorkLogId, defaultDate, defaultComment);
        leistungsManager.leistungAktualisieren(worklog, vertecPhase.getId());

        verify(repository, never()).updateLeistung(anyString(), anyInt(), any(Date.class), anyString(), anyString());
        verify(repository).createLeistung(100, defaultWorkLogId.toString(), bearbeiterMjr.getId(), config.getDefaultPhaseIdWennNachbearbeitungNoetig(), defaultDate, worklogText);
    }

    public void testWennLeistungVorPersFreigabeUndSperrdatumWirdEineKorrektuleistungInDieNachbearbeitungsPhaseGebucht() throws VertecServiceException
    {
        persFreigabeDatum.setBis(getDate(2011, 10, 11));
        sperrdatum.setPropertyValue(getDate(2011, 10, 10));
        setupPhaseLeistungBearbeiter(bearbeiterMjr, vertecPhase);

        Worklog worklog = CreateWorklog(bearbeiterMjr.getLoginName(), 100, defaultWorkLogId, defaultDate, defaultComment);
        leistungsManager.leistungAktualisieren(worklog, vertecPhase.getId());

        verify(repository, never()).updateLeistung(anyString(), anyInt(), any(Date.class), anyString(), anyString());
        verify(repository).createLeistung(100, defaultWorkLogId.toString(), bearbeiterMjr.getId(), config.getDefaultPhaseIdWennNachbearbeitungNoetig(), defaultDate, worklogText);
    }

    public void testWennLeistungVorPersFreigabeUndGleichzeitigSperrdatumWirdEineKorrektuleistungInDieNachbearbeitungsPhaseGebucht() throws VertecServiceException
    {
        persFreigabeDatum.setBis(getDate(2011, 11, 1));
        sperrdatum.setPropertyValue(getDate(2011, 11, 1));
        setupPhaseLeistungBearbeiter(bearbeiterMjr, vertecPhase);

        Worklog worklog = CreateWorklog(bearbeiterMjr.getLoginName(), 100, defaultWorkLogId, defaultDate, defaultComment);
        leistungsManager.leistungAktualisieren(worklog, vertecPhase.getId());

        verify(repository, never()).updateLeistung(anyString(), anyInt(), any(Date.class), anyString(), anyString());
        verify(repository).createLeistung(100, defaultWorkLogId.toString(), bearbeiterMjr.getId(), config.getDefaultPhaseIdWennNachbearbeitungNoetig(), defaultDate, worklogText);
    }

    public void testMutationLeistungMitKorrerkturLeistungWennDiePhaseInaktivIst() throws VertecServiceException
    {
        leistungen.add(leistung1);
        vertecPhase.setAktiv(0);
        setupPhaseLeistungBearbeiter(bearbeiterMjr, vertecPhase);

        Worklog worklog = CreateWorklog(bearbeiterMjr.getLoginName(), 100, defaultWorkLogId, defaultDate, defaultComment);
        leistungsManager.leistungAktualisieren(worklog, vertecPhase.getId());

        verify(repository, never()).updateLeistung(anyString(), anyInt(), any(Date.class), anyString(), anyString());
        verify(repository).createLeistung(90, defaultWorkLogId.toString(), bearbeiterMjr.getId(), config.getDefaultPhaseIdWennNachbearbeitungNoetig(), defaultDate, worklogText);
    }

    public void testMutationLeistungMitKorrerkturLeistungWennBereitsVerrechnet() throws VertecServiceException
    {
        leistungen.add(verrechneteLeistung);
        setupPhaseLeistungBearbeiter(bearbeiterMjr, vertecPhase);

        Worklog worklog = CreateWorklog(bearbeiterMjr.getLoginName(), 70, defaultWorkLogId, defaultDate, defaultComment);
        leistungsManager.leistungAktualisieren(worklog, vertecPhase.getId());

        verify(repository, never()).updateLeistung(anyString(), anyInt(), any(Date.class), anyString(), anyString());
        verify(repository).createLeistung(-10, defaultWorkLogId.toString(), bearbeiterMjr.getId(), vertecPhase.getId(), defaultDate, worklogText);
    }
       
    public void testNeueKorrekturLeistungenWerdenImmerAufDiePhaseDesIssuesGebucht() throws VertecServiceException
    {
        verrechneteLeistung.setPhaseId("nichtAktivePhase");
        leistungen.add(verrechneteLeistung);
        setupPhaseLeistungBearbeiter(bearbeiterMjr, vertecPhase);

        Worklog worklog = CreateWorklog(bearbeiterMjr.getLoginName(), 100, defaultWorkLogId, defaultDate, defaultComment);
        leistungsManager.leistungAktualisieren(worklog, vertecPhase.getId());
        verify(repository).createLeistung(20, defaultWorkLogId.toString(), bearbeiterMjr.getId(), vertecPhase.getId(), defaultDate, worklogText);
    }

    public void testErstelleWorklogTextSubtask()
    {
        Worklog worklog = CreateWorklog(bearbeiterMjr.getLoginName(), 100, defaultWorkLogId, defaultDate, defaultComment);
        String text = leistungsManager.erstelleWorklogText(worklog);

        assertEquals(parentIssue.getKey() + " : " + issue.getKey() + " : " + issue.getSummary() + " : " + worklog.getComment(), text);
    }

    public void testErstelleWorklogTextSubtaskOhneKommentar()
    {
        Worklog worklog = CreateWorklog(bearbeiterMjr.getLoginName(), 100, defaultWorkLogId, defaultDate, defaultComment);
        when(worklog.getComment()).thenReturn("");
        String text = leistungsManager.erstelleWorklogText(worklog);

        assertEquals(parentIssue.getKey() + " : " + issue.getKey() + " : " + issue.getSummary(), text);
    }

    public void testErstelleWorklogTextOhneSubtask()
    {
        Worklog worklog = CreateWorklog(bearbeiterMjr.getLoginName(), 100, defaultWorkLogId, defaultDate, defaultComment);
        when(issue.getParentObject()).thenReturn(null);
        String text = leistungsManager.erstelleWorklogText(worklog);

        assertEquals(issue.getKey() + " : " + issue.getSummary() + " : " + worklog.getComment(), text);
    }

    public void testErstelleWorklogTextOhneSubtaskOhneKommentar()
    {
        Worklog worklog = CreateWorklog(bearbeiterMjr.getLoginName(), 100, defaultWorkLogId, defaultDate, defaultComment);
        when(worklog.getComment()).thenReturn("");
        when(issue.getParentObject()).thenReturn(null);
        String text = leistungsManager.erstelleWorklogText(worklog);

        assertEquals(issue.getKey() + " : " + issue.getSummary(), text);
    }

    public void testLoescheOffeneLeistung() throws VertecServiceException
    {
        leistungen.add(leistung1);
        setupPhaseLeistungBearbeiter(bearbeiterMjr, vertecPhase);
        Worklog worklog = CreateWorklog(bearbeiterMjr.getLoginName(), 100, defaultWorkLogId, defaultDate, defaultComment);
        leistungsManager.leistungLoeschen(worklog, vertecPhase.getId());

        verify(repository).deleteLeistung(leistung1.getId());
    }

    public void testLoescheLeistungMitInaktiverPhase() throws VertecServiceException
    {
        leistungen.add(leistung1);
        vertecPhase.setAktiv(0);
        setupPhaseLeistungBearbeiter(bearbeiterMjr, vertecPhase);
        Worklog worklog = CreateWorklog(bearbeiterMjr.getLoginName(), 100, defaultWorkLogId, defaultDate, defaultComment);
        leistungsManager.leistungLoeschen(worklog, vertecPhase.getId());

        verify(repository, never()).deleteLeistung(anyString());
        verify(repository).createLeistung(-10, defaultWorkLogId.toString(), bearbeiterMjr.getId(), config.getDefaultPhaseIdWennNachbearbeitungNoetig(), defaultDate, worklogText);
    }

    public void testLoescheVerrechneteLeistung() throws VertecServiceException
    {
        leistungen.add(verrechneteLeistung);
        setupPhaseLeistungBearbeiter(bearbeiterMjr, vertecPhase);
        Worklog worklog = CreateWorklog(bearbeiterMjr.getLoginName(), 100, defaultWorkLogId, defaultDate, defaultComment);
        leistungsManager.leistungLoeschen(worklog, vertecPhase.getId());

        verify(repository, never()).deleteLeistung(anyString());
        verify(repository).createLeistung(-80, defaultWorkLogId.toString(), bearbeiterMjr.getId(), vertecPhase.getId(), defaultDate, worklogText);
    }

    public void testLoescheLeistungWelcheVorDemSperrdatumLiegt() throws VertecServiceException
    {
        leistungen.add(leistung1);
        sperrdatum.setPropertyValue(getDate(2011, 11, 1));
        setupPhaseLeistungBearbeiter(bearbeiterMjr, vertecPhase);
        Worklog worklog = CreateWorklog(bearbeiterMjr.getLoginName(), 100, defaultWorkLogId, defaultDate, defaultComment);
        leistungsManager.leistungLoeschen(worklog, vertecPhase.getId());

        verify(repository, never()).deleteLeistung(anyString());
        verify(repository).createLeistung(-10, defaultWorkLogId.toString(), bearbeiterMjr.getId(), config.getDefaultPhaseIdWennNachbearbeitungNoetig(), defaultDate, worklogText);
    }

    public void testLoescheLeistungWelcheVorPersoenlichemFreigabedatumLiegt() throws VertecServiceException
    {
        leistungen.add(leistung1);
        persFreigabeDatum.setBis(getDate(2011, 11, 1));
        setupPhaseLeistungBearbeiter(bearbeiterMjr, vertecPhase);
        Worklog worklog = CreateWorklog(bearbeiterMjr.getLoginName(), 100, defaultWorkLogId, defaultDate, defaultComment);
        leistungsManager.leistungLoeschen(worklog, vertecPhase.getId());

        verify(repository, never()).deleteLeistung(anyString());
        verify(repository).createLeistung(-10, defaultWorkLogId.toString(), bearbeiterMjr.getId(), config.getDefaultPhaseIdWennNachbearbeitungNoetig(), defaultDate, worklogText);
    }

    public void testLoescheLeistungWelcheVorSperrdatumUndPersoenlichemFreigabedatumLiegen() throws VertecServiceException
    {
        leistungen.add(leistung1);
        sperrdatum.setPropertyValue(getDate(2011, 10, 10));
        persFreigabeDatum.setBis(getDate(2011, 10, 11));
        setupPhaseLeistungBearbeiter(bearbeiterMjr, vertecPhase);
        Worklog worklog = CreateWorklog(bearbeiterMjr.getLoginName(), 100, defaultWorkLogId, defaultDate, defaultComment);
        leistungsManager.leistungLoeschen(worklog, vertecPhase.getId());

        verify(repository, never()).deleteLeistung(anyString());
        verify(repository).createLeistung(-10, defaultWorkLogId.toString(), bearbeiterMjr.getId(), config.getDefaultPhaseIdWennNachbearbeitungNoetig(), defaultDate, worklogText);
    }

    public void testLoescheLeistungWelcheVorSperrdatumUndGleichemPersoenlichemFreigabedatumLiegt() throws VertecServiceException
    {
        leistungen.add(leistung1);
        sperrdatum.setPropertyValue(getDate(2011, 11, 1));
        persFreigabeDatum.setBis(getDate(2011, 11, 1));
        setupPhaseLeistungBearbeiter(bearbeiterMjr, vertecPhase);
        Worklog worklog = CreateWorklog(bearbeiterMjr.getLoginName(), 100, defaultWorkLogId, defaultDate, defaultComment);
        leistungsManager.leistungLoeschen(worklog, vertecPhase.getId());

        verify(repository, never()).deleteLeistung(anyString());
        verify(repository).createLeistung(-10, defaultWorkLogId.toString(), bearbeiterMjr.getId(), config.getDefaultPhaseIdWennNachbearbeitungNoetig(), defaultDate, worklogText);
    }

    private Worklog CreateWorklog(String user, int minuten, long worklogId, Date date, String comment)
    {
        Worklog worklog = mock(Worklog.class);
        when(worklog.getAuthorKey()).thenReturn(user);
        when(worklog.getTimeSpent()).thenReturn((long) (minuten * 60));
        when(worklog.getId()).thenReturn(worklogId);
        when(worklog.getStartDate()).thenReturn(date);
        when(worklog.getIssue()).thenReturn(issue);
        when(worklog.getComment()).thenReturn(comment);

        ApplicationUser appUser = mock(LazyLoadingApplicationUser.class);
        when(appUser.getUsername()).thenReturn(user);

        when(worklog.getAuthorObject()).thenReturn(appUser);

        return worklog;
    }

    private Date getDate(int year, int month, int day)
    {
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(year, month, day);
        
        return cal.getTime();
    }

    private Date getDate(int year, int month, int day, int hours, int minutes)
    {
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(year, month, day);
        cal.set(Calendar.HOUR_OF_DAY, hours);
        cal.set(Calendar.MINUTE, minutes);

        return cal.getTime();
    }
}
