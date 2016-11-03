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

package com.jira.plugins;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.customfields.CustomFieldType;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.worklog.Worklog;
import com.atlassian.jira.issue.worklog.WorklogManager;
import com.jira.plugins.vertec.service.VertecServiceException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;

public class VertecEventListener implements InitializingBean, DisposableBean
{

    private final EventPublisher eventPublisher;
    private LeistungsManager leistungsManager;
    private CustomFieldManager customFieldManager;
    private WorklogManager worklogManager;

    private final static Logger logger = Logger.getLogger(VertecEventListener.class);

    public VertecEventListener(EventPublisher eventPublisher, LeistungsManager leistungsManager, CustomFieldManager customFieldManager, WorklogManager worklogManager)
    {
        this.eventPublisher = eventPublisher;
        this.leistungsManager = leistungsManager;
        this.customFieldManager = customFieldManager;
        this.worklogManager = worklogManager;
    }

    /**
     * Called when the plugin has been enabled.
     *
     * @throws Exception
     */
    public void afterPropertiesSet() throws Exception
    {
        // register ourselves with the EventPublisher
        eventPublisher.register(this);
    }

    /**
     * Called when the plugin is being disabled or removed.
     *
     * @throws Exception
     */
    public void destroy() throws Exception
    {
        // unregister ourselves with the EventPublisher
        eventPublisher.unregister(this);
    }

    private String getVertecPhaseVonIssueOderParent(Issue issue, CustomField vertecField) {
        String vertecPhase = vertecField.getValueFromIssue(issue);
        if (vertecPhase == null && issue.getParentObject() != null)
            return getVertecPhaseVonIssueOderParent(issue.getParentObject(), vertecField);

        return vertecPhase; 
    }

    private void updateWorklogsByIssue(Issue issue, String vertecPhase) {
        for (Worklog wl : worklogManager.getByIssue(issue)) {
            try {
                leistungsManager.leistungAktualisierenPhasenWechsel(wl, vertecPhase);
            } catch (VertecServiceException e) {
                logger.error("[JiraToVertec] Leistung konnte nicht aktualisiert werden! " + e.getMessage());
            }
        }
    }

    private void updatePhaseDerWorklogs(Issue issue, String vertecPhase, CustomField vertecField) {        
            // Alle Worklogs des Issues aktualisieren
            updateWorklogsByIssue(issue, vertecPhase);

            // Alle Worklogs der Phasenlosen Subtasks aktualisieren
            for (Issue i : issue.getSubTaskObjects()) {
                String subTaskVertecPhase = vertecField.getValueFromIssue(i);
                if (subTaskVertecPhase == null) {
                    updateWorklogsByIssue(i, vertecPhase);
                }
            }
    }

    /**
     * Receives any {@code IssueEvent}s sent by JIRA.
     *
     * @param issueEvent the IssueEvent passed to us
     */
    @EventListener
    public void onIssueEvent(IssueEvent issueEvent)
    {
        Long eventTypeId = issueEvent.getEventTypeId();
        Issue issue = issueEvent.getIssue();
        Worklog worklog = issueEvent.getWorklog();

        CustomField vertecField = getVertecField(issue);
        if (vertecField == null){
            return;
        }

        String vertecPhase = getVertecPhaseVonIssueOderParent(issue, vertecField);
        
        if (eventTypeId.equals(EventType.ISSUE_UPDATED_ID)) {
            updatePhaseDerWorklogs(issue, vertecPhase, vertecField);
        } else if (eventTypeId.equals(EventType.ISSUE_WORKLOG_UPDATED_ID)) {
            try {
                leistungsManager.leistungAktualisieren(worklog, vertecPhase);
            } catch (VertecServiceException e) {
                logger.error("[JiraToVertec] Leistung konnte nicht aktualisiert werden! " + e.getMessage());
            }
        } else if (eventTypeId.equals(EventType.ISSUE_WORKLOGGED_ID)) {
            try {
                leistungsManager.leistungErstellen(worklog, vertecPhase);
            } catch (VertecServiceException e) {
                logger.error("[JiraToVertec] Leistung konnte nicht erstellt werden! " + e.getMessage());
            }
        } else if (eventTypeId.equals(EventType.ISSUE_WORKLOG_DELETED_ID)) {
            try {
                leistungsManager.leistungLoeschen(worklog, vertecPhase);
            } catch (VertecServiceException e) {
                logger.error("[JiraToVertec] Leistung konnte nicht geloescht werden! " + e.getMessage());
            }
        }
        
    }

    private CustomField getVertecField(Issue issue){
        List<CustomField> customFieldList = this.customFieldManager.getCustomFieldObjects(issue);
        for(CustomField field : customFieldList){
            CustomFieldType type = field.getCustomFieldType();
            if (type.getKey().equals("com.jira.plugins.jira2vertec:vertecphasefield")){
                return field;
            }
        }
        return null;
    }
}