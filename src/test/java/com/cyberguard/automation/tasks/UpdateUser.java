package com.cyberguard.automation.tasks;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Put;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class UpdateUser implements Task {

    private final String userId;
    private final String fullName;
    private final String role;
    private final String token;

    public UpdateUser(String userId, String fullName, String role, String token) {
        this.userId = userId;
        this.fullName = fullName;
        this.role = role;
        this.token = token;
    }

    public static UpdateUser withData(String userId, String fullName, String role, String token) {
        return instrumented(UpdateUser.class, userId, fullName, role, token);
    }

    @Override
    @Step("{0} actualiza el usuario #userId con rol #role")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Put.to("/api/admin/users/" + userId)
                        .with(request -> request
                                .header("Content-Type", "application/json")
                                .header("Authorization", "Bearer " + token)
                                .body("{\"fullName\":\"" + fullName + "\","
                                        + "\"role\":\"" + role + "\"}")
                        )
        );
    }
}
