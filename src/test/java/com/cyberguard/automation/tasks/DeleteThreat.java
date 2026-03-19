package com.cyberguard.automation.tasks;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Delete;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class DeleteThreat implements Task {

    private final String threatId;
    private final String token;

    public DeleteThreat(String threatId, String token) {
        this.threatId = threatId;
        this.token = token;
    }

    public static DeleteThreat withId(String threatId, String token) {
        return instrumented(DeleteThreat.class, threatId, token);
    }

    @Override
    @Step("{0} elimina la amenaza con ID #threatId")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Delete.from("/api/threats/" + threatId)
                        .with(request -> request
                                .header("Authorization", "Bearer " + token)
                        )
        );
    }
}
