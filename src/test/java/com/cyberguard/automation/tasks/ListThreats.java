package com.cyberguard.automation.tasks;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Get;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class ListThreats implements Task {

    private final String token;

    public ListThreats(String token) {
        this.token = token;
    }

    public static ListThreats withToken(String token) {
        return instrumented(ListThreats.class, token);
    }

    @Override
    @Step("{0} consulta el listado de amenazas registradas")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Get.resource("/api/threats")
                        .with(request -> request
                                .header("Authorization", "Bearer " + token)
                        )
        );
    }
}
