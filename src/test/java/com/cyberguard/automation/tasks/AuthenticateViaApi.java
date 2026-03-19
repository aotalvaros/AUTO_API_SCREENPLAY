package com.cyberguard.automation.tasks;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Post;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class AuthenticateViaApi implements Task {

    private final String username;
    private final String password;

    public AuthenticateViaApi(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static AuthenticateViaApi withCredentials(String username, String password) {
        return instrumented(AuthenticateViaApi.class, username, password);
    }

    @Override
    @Step("{0} se autentica en la API con el usuario #username")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Post.to("/api/auth/login")
                        .with(request -> request
                                .header("Content-Type", "application/json")
                                .body("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}")
                        )
        );
    }
}
