package com.cyberguard.automation.tasks;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Post;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class CreateIncident implements Task {

    private final String threatId;
    private final String token;

    public CreateIncident(String threatId, String token) {
        this.threatId = threatId;
        this.token = token;
    }

    public static CreateIncident fromThreat(String threatId, String token) {
        return instrumented(CreateIncident.class, threatId, token);
    }

    @Override
    @Step("{0} crea un incidente a partir de la amenaza #threatId")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Post.to("/api/incidents")
                        .with(request -> request
                                .header("Content-Type", "application/json")
                                .header("Authorization", "Bearer " + token)
                                .body("{\"threatId\":\"" + threatId + "\"}")
                        )
        );
    }
}
