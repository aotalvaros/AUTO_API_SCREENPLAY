package com.cyberguard.automation.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.rest.questions.LastResponse;

import java.util.List;
import java.util.Map;

/**
 * Question que verifica si un incidente (por threatId) existe en el listado
 * retornado por GET /api/incidents.
 */
public class IncidentInList implements Question<Boolean> {

    private final String threatId;

    public IncidentInList(String threatId) {
        this.threatId = threatId;
    }

    public static IncidentInList forThreat(String threatId) {
        return new IncidentInList(threatId);
    }

    @Override
    public Boolean answeredBy(Actor actor) {
        List<Map<String, Object>> incidents = LastResponse.received()
                .answeredBy(actor)
                .body()
                .jsonPath()
                .getList("incidents");
        return incidents != null && incidents.stream()
                .anyMatch(i -> threatId.equals(i.get("threatId")));
    }
}
