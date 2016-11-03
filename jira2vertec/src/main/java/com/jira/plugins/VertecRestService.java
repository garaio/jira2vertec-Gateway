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

import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.jira.plugins.configuration.JiraToVertecConfiguration;
import com.jira.plugins.rest.entities.RestPhase;
import com.jira.plugins.rest.entities.RestProject;
import com.jira.plugins.vertec.entities.VertecPhase;
import com.jira.plugins.vertec.entities.VertecProject;
import com.jira.plugins.vertec.VertecRepository;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Path("/vertec")
public class VertecRestService
{
    private JiraToVertecConfiguration configuration;
    private VertecRepository repository;

    public VertecRestService(VertecRepository repository, JiraToVertecConfiguration configuration) {
        this.repository = repository;
        this.configuration = configuration;
    }

    @GET
    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON})
    @Path("/projekte")
    public Response getAlleAktivenProjekte()
    {
        List<VertecProject> vertecProjects = repository.getAllProjectsAktiv();
        List<RestProject> restProjects = new ArrayList<RestProject>(vertecProjects.size());

        for (VertecProject vertecProject : vertecProjects) {
            RestProject restProject = new RestProject(vertecProject, configuration.zeigeProjektbeschrieb(), configuration.getProjektbeschriebMaxLaenge());
            restProjects.add(restProject);
        }

        Collections.sort(restProjects);

        return Response.ok(restProjects).build();
    }

    @GET
    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON})
    @Path("/projekte/{phaseId}")
    public Response getProjektFuerPhase(@PathParam("phaseId")String phaseId)
    {
        VertecProject vertecProjekt = repository.getProjekt(phaseId);
        RestProject restProjekt = new RestProject(vertecProjekt, configuration.zeigeProjektbeschrieb(), configuration.getProjektbeschriebMaxLaenge());
        
        return Response.ok(restProjekt).build();
    }

    @GET
    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON})
    @Path("/phasen/{projektId}")
    public Response getAktivePhasenFuerProjekt(@PathParam("projektId")String projektId)
    {
        List<VertecPhase> vertecPhasen;
        if (configuration.zeigeProjektbeschrieb()) {
            vertecPhasen = repository.getProjektPhasenAktivMitProjektbeschrieb(projektId);
        } else {
            vertecPhasen = repository.getProjektPhasenAktiv(projektId);
        }

        List<RestPhase> restPhasen = new ArrayList<RestPhase>(vertecPhasen.size());

        for (VertecPhase vertecPhase : vertecPhasen) {
            RestPhase restPhase = new RestPhase(vertecPhase, configuration.zeigeProjektbeschrieb(), configuration.getProjektbeschriebMaxLaenge());
            restPhasen.add(restPhase);
        }

        Collections.sort(restPhasen);

        return Response.ok(restPhasen).build();
    }
}
