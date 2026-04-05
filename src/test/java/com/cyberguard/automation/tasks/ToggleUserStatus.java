package com.cyberguard.automation.tasks;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Patch;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class ToggleUserStatus implements Task {

    private final String userId;
    private final boolean isActive;
    private final String token;

    public ToggleUserStatus(String userId, boolean isActive, String token) {
        this.userId = userId;
        this.isActive = isActive;
        this.token = token;
    }

    public static ToggleUserStatus forUser(String userId, boolean isActive, String token) {
        return instrumented(ToggleUserStatus.class, userId, isActive, token);
    }

    @Override
    @Step("{0} cambia el estado del usuario #userId a isActive=#isActive")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Patch.to("/api/admin/users/" + userId + "/status")
                        .with(request -> request
                                .header("Content-Type", "application/json")
                                .header("Authorization", "Bearer " + token)
                                .body("{\"isActive\":" + isActive + "}")
                        )
        );
    }
}
