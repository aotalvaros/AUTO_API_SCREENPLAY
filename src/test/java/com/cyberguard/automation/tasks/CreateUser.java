package com.cyberguard.automation.tasks;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Post;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class CreateUser implements Task {

    private final String email;
    private final String fullName;
    private final String username;
    private final String role;
    private final String token;

    public CreateUser(String email, String fullName, String username, String role, String token) {
        this.email = email;
        this.fullName = fullName;
        this.username = username;
        this.role = role;
        this.token = token;
    }

    public static CreateUser withData(String email, String fullName, String username, String role, String token) {
        return instrumented(CreateUser.class, email, fullName, username, role, token);
    }

    @Override
    @Step("{0} crea un usuario con email #email y rol #role")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Post.to("/api/admin/users")
                        .with(request -> request
                                .header("Content-Type", "application/json")
                                .header("Authorization", "Bearer " + token)
                                .body("{\"email\":\"" + email + "\","
                                        + "\"fullName\":\"" + fullName + "\","
                                        + "\"username\":\"" + username + "\","
                                        + "\"role\":\"" + role + "\"}")
                        )
        );
    }
}
