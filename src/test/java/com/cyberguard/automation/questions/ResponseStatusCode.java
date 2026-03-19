package com.cyberguard.automation.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.rest.questions.LastResponse;

public class ResponseStatusCode implements Question<Integer> {

    public static ResponseStatusCode value() {
        return new ResponseStatusCode();
    }

    @Override
    public Integer answeredBy(Actor actor) {
        return LastResponse.received().answeredBy(actor).statusCode();
    }
}
