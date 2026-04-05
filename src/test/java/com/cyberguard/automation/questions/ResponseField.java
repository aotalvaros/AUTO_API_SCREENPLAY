package com.cyberguard.automation.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.rest.questions.LastResponse;

public class ResponseField implements Question<String> {

    private final String jsonPath;

    public ResponseField(String jsonPath) {
        this.jsonPath = jsonPath;
    }

    public static ResponseField withPath(String jsonPath) {
        return new ResponseField(jsonPath);
    }

    @Override
    public String answeredBy(Actor actor) {
        Object result = LastResponse.received().answeredBy(actor).body().path(jsonPath);
        return result != null ? result.toString() : null;
    }
}
