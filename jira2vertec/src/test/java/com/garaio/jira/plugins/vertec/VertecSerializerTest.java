package com.garaio.jira.plugins.vertec;

import com.garaio.jira.plugins.configuration.JiraToVertecConfiguration;
import com.garaio.jira.plugins.vertec.entities.*;
import com.garaio.jira.plugins.vertec.service.VertecServiceException;
import com.garaio.jira.plugins.vertec.service.VertecSerializerImpl;
import com.garaio.jira.plugins.vertec.soap.*;
import junit.framework.TestCase;
import org.apache.commons.io.IOUtils;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.List;

public class VertecSerializerTest extends TestCase {

    private JiraToVertecConfiguration config = new TestConfigurationImpl();

    public void testCanSerializeEnvleopeRequest() throws JAXBException {

        VertecSerializerImpl serializer = new VertecSerializerImpl();

        VertecSoapEnvelope e = new VertecSoapEnvelope();
        VertecSoapQuery query = new VertecSoapQuery();
        VertecSoapSelection selection = new VertecSoapSelection();
        selection.setOcl("Leistung");
        selection.setObjRef("4157");
        selection.setSqlWhere(config.getVertecJiraReferenceField() + " = '123456789'");
        query.setSelection(selection);
        e.getBody().setContent(query);

        StringWriter writer = new StringWriter();
        String result = serializer.SerializeObject(e);

        assertNotNull(result);
        assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\"?><Envelope><Header/><Body><Query><Selection><objref>4157</objref><ocl>Leistung</ocl><sqlwhere>referenz = '123456789'</sqlwhere></Selection></Query></Body></Envelope>", result);
    }

    public void testCanSerializeCreateRequest() throws JAXBException {
        VertecSerializerImpl serializer = new VertecSerializerImpl();

        VertecOffeneLeistung offeneLeistung = new VertecOffeneLeistung();
        offeneLeistung.setPhaseId("1919585");
        offeneLeistung.setBearbeiterId("2062925");
        offeneLeistung.setMinuten(180);
        offeneLeistung.setJiraReferenz(config.getVertecJiraReferenceField(), "101542");
        offeneLeistung.setText("PUT-1772 : PUT-1814 : Installation für cf");
        VertecSoapCreate create = new VertecSoapCreate();
        create.setItem(offeneLeistung);

        VertecSoapEnvelope e = new VertecSoapEnvelope();
        e.getBody().setContent(create);

        String result = serializer.SerializeObject(e);
        assertNotNull(result);
        assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\"?><Envelope><Header/><Body><Create><OffeneLeistung><phase><objref>1919585</objref></phase><bearbeiter><objref>2062925</objref></bearbeiter><minutenInt>180</minutenInt><referenz>101542</referenz><text>PUT-1772 : PUT-1814 : Installation für cf</text></OffeneLeistung></Create></Body></Envelope>", result);
    }

    public void testCanDeserializeProjectResponse() throws JAXBException, VertecServiceException, IOException {
        String xml = IOUtils.toString(VertecSerializerTest.class.getClassLoader().getResourceAsStream("ProjectResponse.xml"), StandardCharsets.UTF_8.name());
        VertecSerializerImpl serializer = new VertecSerializerImpl();
        List<VertecProject> projects = serializer.DeserializeResponseList(xml);

        assertNotNull(projects);
        assertEquals(3, projects.size());
        assertEquals("0001", projects.get(0).getId());
        assertEquals("Beschrieb TestProjekt 1", projects.get(0).getBeschrieb());
        assertEquals("TestProjekt 2", projects.get(1).getName());
        assertEquals("TestProjekt 3", projects.get(2).getName());
    }

    public void testCanDeserializeLeistungenResponse() throws JAXBException, VertecServiceException, IOException {
        String xml = IOUtils.toString(VertecSerializerTest.class.getClassLoader().getResourceAsStream("LeistungenResponse.xml"), StandardCharsets.UTF_8.name());
        VertecSerializerImpl serializer = new VertecSerializerImpl();
        List<VertecLeistung> leistungen = serializer.DeserializeResponseList(xml);

        assertNotNull(leistungen);
        assertEquals(3, leistungen.size());
        assertEquals("123456", leistungen.get(0).getJiraReferenz(config.getVertecJiraReferenceField()));
        assertEquals("111", leistungen.get(0).getPhaseId());
        assertEquals(false, leistungen.get(0).istVerrechnet());
        assertEquals(true, leistungen.get(1).istVerrechnet());
        assertEquals(12, leistungen.get(1).getMinuten());

        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(2011, 8, 20);

        assertEquals(calendar.getTime(), leistungen.get(1).getDatum());
    }

    public void testCanDeserializePhasenResponse() throws JAXBException, VertecServiceException, IOException {
        String xml = IOUtils.toString(VertecSerializerTest.class.getClassLoader().getResourceAsStream("PhasenResponse.xml"), StandardCharsets.UTF_8.name());
        VertecSerializerImpl serializer = new VertecSerializerImpl();
        List<VertecPhase> phasen = serializer.DeserializeResponseList(xml);

        assertNotNull(phasen);
        assertEquals(3, phasen.size());
        assertEquals("21507", phasen.get(1).getId());
        assertEquals("MILVER", phasen.get(0).getName());
        assertEquals("armasuisse", phasen.get(1).getProjektName());
        assertEquals(0, phasen.get(0).getAktiv());
    }

    public void testCanDeserializeBearbeiterResponse() throws JAXBException, VertecServiceException, IOException {
        String xml = IOUtils.toString(VertecSerializerTest.class.getClassLoader().getResourceAsStream("ProjektBearbeiterResponse.xml"), StandardCharsets.UTF_8.name());
        VertecSerializerImpl serializer = new VertecSerializerImpl();
        List<VertecProjektBearbeiter> beabeiter = serializer.DeserializeResponseList(xml);

        assertNotNull(beabeiter);
        assertEquals(1, beabeiter.size());
        assertEquals("25534", beabeiter.get(0).getId());
        assertEquals("mjr", beabeiter.get(0).getLoginName());
    }

    public void testCanDeserializeProjektIdResponse() throws JAXBException, VertecServiceException, IOException {
        String xml = IOUtils.toString(VertecSerializerTest.class.getClassLoader().getResourceAsStream("ProjektIdResponse.xml"), StandardCharsets.UTF_8.name());
        VertecSerializerImpl serializer = new VertecSerializerImpl();
        List<VertecPhase> phasen = serializer.DeserializeResponseList(xml);

        assertNotNull(phasen);
        assertEquals(1, phasen.size());
        assertEquals("19720", phasen.get(0).getProjektId());
    }

    public void testCanDeserializeFaultResponse() throws JAXBException, IOException {
        String xml = IOUtils.toString(VertecSerializerTest.class.getClassLoader().getResourceAsStream("FaultResponse.xml"), StandardCharsets.UTF_8.name());
        VertecSerializerImpl serializer = new VertecSerializerImpl();

        VertecServiceException exception = null;
        List<VertecPhase> phasen = null;
        try {
            phasen = serializer.DeserializeResponseList(xml);
        } catch (VertecServiceException e) {
            exception = e;
        }

        assertNotNull(exception);
        assertNotNull(exception.getMessage());
    }

    public void testCanDeserializeCreateResponse() throws JAXBException, VertecServiceException, IOException {
        String xml = IOUtils.toString(VertecSerializerTest.class.getClassLoader().getResourceAsStream("CreateResponse.xml"), StandardCharsets.UTF_8.name());
        VertecSerializerImpl serializer = new VertecSerializerImpl();

        VertecSoapCreateResponse<VertecOffeneLeistung> response = serializer.DeserializeResponse(xml);

        assertNotNull(response);
        VertecOffeneLeistung item = response.getItem();
        assertNotNull(item);
        assertTrue(item.isValid());
    }
}
