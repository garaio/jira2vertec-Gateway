package com.jira.plugins.vertec;

import com.jira.plugins.vertec.entities.*;
import com.jira.plugins.vertec.service.VertecServiceException;
import com.jira.plugins.vertec.service.VertecSerializerImpl;
import com.jira.plugins.vertec.soap.*;
import junit.framework.TestCase;

import javax.xml.bind.JAXBException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.List;

public class VertecSerializerTest extends TestCase {

    public void testCanSerializeEnvleopeRequest() throws JAXBException {

        VertecSerializerImpl serializer = new VertecSerializerImpl();

        VertecSoapEnvelope e = new VertecSoapEnvelope("{username}", "{password}");
        VertecSoapQuery query = new VertecSoapQuery();
        VertecSoapSelection selection = new VertecSoapSelection();
        selection.setOcl("Leistung");
        selection.setObjRef("4157");
        selection.setSqlWhere("referenz = '123456789'");
        query.setSelection(selection);
        e.getBody().setContent(query);

        StringWriter writer = new StringWriter();
        serializer.SerializeObject(e);

        String result = writer.toString();
        assertNotNull(result);
    }

    public void testCanDeserializeProjectResponse() throws JAXBException, VertecServiceException
    {
        InputStream xml = VertecSerializerTest.class.getClassLoader().getResourceAsStream("ProjectResponse.xml");
        VertecSerializerImpl serializer = new VertecSerializerImpl();
        List<VertecProject> projects = serializer.DeserializeResponseList(xml);

        assertNotNull(projects);
        assertEquals(3, projects.size());
        assertEquals("0001", projects.get(0).getId());
        assertEquals("Beschrieb TestProjekt 1", projects.get(0).getBeschrieb());
        assertEquals("TestProjekt 2", projects.get(1).getName());
        assertEquals("TestProjekt 3", projects.get(2).getName());
    }

    public void testCanDeserializeLeistungenResponse() throws JAXBException, VertecServiceException
    {
        InputStream xml = VertecSerializerTest.class.getClassLoader().getResourceAsStream("LeistungenResponse.xml");
        VertecSerializerImpl serializer = new VertecSerializerImpl();
        List<VertecLeistung> leistungen = serializer.DeserializeResponseList(xml);

        assertNotNull(leistungen);
        assertEquals(3, leistungen.size());
        assertEquals("123456", leistungen.get(0).getJiraReferenz());
        assertEquals("111", leistungen.get(0).getPhaseId());
        assertEquals(false, leistungen.get(0).istVerrechnet());
        assertEquals(true, leistungen.get(1).istVerrechnet());
        assertEquals(12, leistungen.get(1).getMinuten());

        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(2011, 8, 20);

        assertEquals(calendar.getTime(), leistungen.get(1).getDatum());
    }

    public void testCanDeserializePhasenResponse() throws JAXBException, VertecServiceException
    {
        InputStream xml = VertecSerializerTest.class.getClassLoader().getResourceAsStream("PhasenResponse.xml");
        VertecSerializerImpl serializer = new VertecSerializerImpl();
        List<VertecPhase> phasen = serializer.DeserializeResponseList(xml);

        assertNotNull(phasen);
        assertEquals(3, phasen.size());
        assertEquals("21507", phasen.get(1).getId());
        assertEquals("MILVER", phasen.get(0).getName());
        assertEquals("armasuisse", phasen.get(1).getProjektName());
        assertEquals(0, phasen.get(0).getAktiv());
    }

    public void testCanDeserializeBearbeiterResponse() throws JAXBException, VertecServiceException
    {
        InputStream xml = VertecSerializerTest.class.getClassLoader().getResourceAsStream("ProjektBearbeiterResponse.xml");
        VertecSerializerImpl serializer = new VertecSerializerImpl();
        List<VertecProjektBearbeiter> beabeiter = serializer.DeserializeResponseList(xml);

        assertNotNull(beabeiter);
        assertEquals(1, beabeiter.size());
        assertEquals("25534", beabeiter.get(0).getId());
        assertEquals("mjr", beabeiter.get(0).getLoginName());
    }

    public void testCanDeserializeProjektIdResponse() throws JAXBException, VertecServiceException
    {
        InputStream xml = VertecSerializerTest.class.getClassLoader().getResourceAsStream("ProjektIdResponse.xml");
        VertecSerializerImpl serializer = new VertecSerializerImpl();
        List<VertecPhase> phasen = serializer.DeserializeResponseList(xml);

        assertNotNull(phasen);
        assertEquals(1, phasen.size());
        assertEquals("19720", phasen.get(0).getProjektId());
    }

    public void testCanDeserializeFaultResponse() throws JAXBException {
        InputStream xml = VertecSerializerTest.class.getClassLoader().getResourceAsStream("FaultResponse.xml");
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

    public void testCanDeserializeCreateResponse() throws JAXBException, VertecServiceException
    {
        InputStream xml = VertecSerializerTest.class.getClassLoader().getResourceAsStream("CreateResponse.xml");
        VertecSerializerImpl serializer = new VertecSerializerImpl();

        VertecSoapCreateResponse<VertecOffeneLeistung> response = serializer.DeserializeResponse(xml);

        assertNotNull(response);
        VertecOffeneLeistung item = response.getItem();
        assertNotNull(item);
        assertTrue(item.isValid());
    }
}
