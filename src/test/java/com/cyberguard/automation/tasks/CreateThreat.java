package com.cyberguard.automation.tasks;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Post;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class CreateThreat implements Task {

    private final String type;
    private final String severity;
    private final String sourceIp;
    private final String description;
    private final String token;

    public CreateThreat(String type, String severity, String sourceIp, String description, String token) {
        this.type = type;
        this.severity = severity;
        this.sourceIp = sourceIp;
        this.description = description;
        this.token = token;
    }

    public static CreateThreat withData(String type, String severity, String sourceIp, String description, String token) {
        return instrumented(CreateThreat.class, type, severity, sourceIp, description, token);
    }

    @Override
    @Step("{0} crea una amenaza de tipo #type con severidad #severity")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Post.to("/api/threats")
                        .with(request -> request
                                .header("Content-Type", "application/json")
                                .header("Authorization", "Bearer " + token)
                                .body("{\"type\":\"" + type + "\","
                                        + "\"severity\":\"" + severity + "\","
                                        + "\"sourceIp\":\"" + sourceIp + "\","
                                        + "\"description\":\"" + description + "\"}")
                        )
        );
    }
}
