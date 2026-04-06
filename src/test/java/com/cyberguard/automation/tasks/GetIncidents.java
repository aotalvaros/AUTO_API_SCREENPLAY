package com.cyberguard.automation.tasks;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Get;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class GetIncidents implements Task {

    private final String token;

    public GetIncidents(String token) {
        this.token = token;
    }

    public static GetIncidents withToken(String token) {
        return instrumented(GetIncidents.class, token);
    }

    @Override
    @Step("{0} consulta el listado de incidentes registrados")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Get.resource("/api/incidents")
                        .with(request -> request
                                .header("Authorization", "Bearer " + token)
                        )
        );
    }
}
