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

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.customfields.impl.TextCFType;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.jira.plugins.configuration.JiraToVertecConfiguration;
import com.jira.plugins.rest.entities.RestPhase;
import com.jira.plugins.vertec.entities.VertecPhase;
import com.jira.plugins.vertec.VertecRepository;
import org.apache.log4j.Logger;

import java.util.Map;

public class VertecPhaseField extends TextCFType {
    private static final String PHASEN_CACHE_KEY = "PHASEN-CACHE-KEY";

    private final static Logger logger = Logger.getLogger(VertecPhaseField.class);

    private VertecRepository repository;
    private JiraToVertecConfiguration configuration;
    // Es sollte eigentlich das Interface CacheFactory verwendet werden, dieses ist aber aktuell buggy...
    // See https://jac-new.atlassian.com/browse/CONF-22424
    private CacheManager cacheFactory;

    public VertecPhaseField(CustomFieldValuePersister customFieldValuePersister, GenericConfigManager genericConfigManager, VertecRepository repository, CacheManager cacheFactory, JiraToVertecConfiguration configuration) {
        super(customFieldValuePersister, genericConfigManager);
        this.repository = repository;
        this.cacheFactory = cacheFactory;
        this.configuration = configuration;
    }

    private Cache<String, VertecPhase> getPhasenCache() {
        return cacheFactory.getCache(PHASEN_CACHE_KEY, String.class, VertecPhase.class);
    }

    private RestPhase getPhase(String phaseId) {
        VertecPhase phase = null;
        Cache<String, VertecPhase> phaseCache = null;
        if (configuration.isCachingEnabled()) {
            try {
                phaseCache = getPhasenCache();
                phase = phaseCache.get(phaseId);
            } catch (Exception e) {
                logger.warn(String.format("[JiraToVertec] Fehler beim Zugriff auf PhasenCache. Stattdessen wird direkt auf das Repository zugegriffen. '%s'", e.getMessage()));
            }
        }

        if (phase == null) {
            if (configuration.zeigeProjektbeschrieb()) {
                phase = repository.getPhaseMitProjektbeschrieb(phaseId);
            } else {
                phase = repository.getPhase(phaseId);
            }
            
            if (phase != null && phaseCache != null) {
                phaseCache.put(phaseId, phase);
            }
        }

        return new RestPhase(phase, configuration.zeigeProjektbeschrieb(), configuration.getProjektbeschriebMaxLaenge());
    }


    @Override
    public Map<String, Object> getVelocityParameters(Issue issue, CustomField field, FieldLayoutItem fieldLayoutItem) {

        Map<String, Object> map = super.getVelocityParameters(issue, field, fieldLayoutItem);

        if (issue == null)
            return map;

        RestPhase phase = null;
        String description = "<undef>";
        String projektId = "";
        String phaseId = "";

        Object value = field.getValue(issue);
        if (value != null) {
            String id = value.toString();
            if (id.length() > 0) {
                phase = getPhase(id);
                if (phase == null) {
                    projektId = "-1";
                    description = String.format("UNG�LTIGE PHASE (%1s)", id);
                    phaseId = "-1";
                } else {
                    projektId = phase.getProjektId();
                    description = String.format("%1s -> %1s (%1s)", phase.getProjektBezeichnung(), phase.getName(), phase.getId());
                    phaseId = phase.getId();
                }
            }
        }
        map.put("projektId", projektId);
        map.put("phaseDescription", description);
        map.put("phaseId", phaseId);
        return map;
    }
}