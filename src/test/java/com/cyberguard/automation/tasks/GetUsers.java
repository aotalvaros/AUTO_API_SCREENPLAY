package com.cyberguard.automation.tasks;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Get;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class GetUsers implements Task {

    private final String token;

    public GetUsers(String token) {
        this.token = token;
    }

    public static GetUsers withToken(String token) {
        return instrumented(GetUsers.class, token);
    }

    @Override
    @Step("{0} consulta el listado de usuarios registrados")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Get.resource("/api/admin/users")
                        .with(request -> request
                                .header("Authorization", "Bearer " + token)
                        )
        );
    }
}
