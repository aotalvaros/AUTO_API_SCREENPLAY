package com.cyberguard.automation.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.rest.questions.LastResponse;

import java.util.List;
import java.util.Map;

/**
 * Question que verifica si un usuario (por username) existe en el listado
 * retornado por GET /api/admin/users.
 */
public class UserInList implements Question<Boolean> {

    private final String username;

    public UserInList(String username) {
        this.username = username;
    }

    public static UserInList withUsername(String username) {
        return new UserInList(username);
    }

    @Override
    public Boolean answeredBy(Actor actor) {
        List<Map<String, Object>> users = LastResponse.received()
                .answeredBy(actor)
                .body()
                .jsonPath()
                .getList("users");
        return users != null && users.stream()
                .anyMatch(u -> username.equals(u.get("username")));
    }
}
