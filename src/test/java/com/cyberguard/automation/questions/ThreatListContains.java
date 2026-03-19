package com.cyberguard.automation.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.rest.questions.LastResponse;

public class ThreatListContains implements Question<Boolean> {

    private final String description;

    public ThreatListContains(String description) {
        this.description = description;
    }

    public static ThreatListContains threatWithDescription(String description) {
        return new ThreatListContains(description);
    }

    @Override
    public Boolean answeredBy(Actor actor) {
        java.util.List<String> descriptions = LastResponse.received()
                .answeredBy(actor)
                .body()
                .jsonPath()
                .getList("threats.description");
        return descriptions != null && descriptions.stream().anyMatch(d -> d.contains(description));
    }
}
