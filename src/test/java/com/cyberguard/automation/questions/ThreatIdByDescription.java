package com.cyberguard.automation.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.rest.questions.LastResponse;

import java.util.List;
import java.util.Map;

public class ThreatIdByDescription implements Question<String> {

    private final String description;

    public ThreatIdByDescription(String description) {
        this.description = description;
    }

    public static ThreatIdByDescription containing(String description) {
        return new ThreatIdByDescription(description);
    }

    @Override
    public String answeredBy(Actor actor) {
        List<Map<String, Object>> threats = LastResponse.received()
                .answeredBy(actor)
                .body()
                .jsonPath()
                .getList("threats");
        return threats.stream()
                .filter(t -> t.get("description").toString().contains(description))
                .map(t -> t.get("threatId").toString())
                .findFirst()
                .orElse("");
    }
}
