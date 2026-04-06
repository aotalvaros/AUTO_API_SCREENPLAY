package com.cyberguard.automation.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.rest.questions.LastResponse;

import java.util.List;
import java.util.Map;

/**
 * Question que extrae el valor de un campo específico del usuario identificado
 * por username dentro del listado retornado por GET /api/admin/users.
 * Uso: actor.asksFor(UserFieldValue.forUsername("ana.torres").field("isActive"))
 */
public class UserFieldValue implements Question<String> {

    private final String username;
    private final String field;

    public UserFieldValue(String username, String field) {
        this.username = username;
        this.field = field;
    }

    public static Builder forUsername(String username) {
        return new Builder(username);
    }

    @Override
    public String answeredBy(Actor actor) {
        List<Map<String, Object>> users = LastResponse.received()
                .answeredBy(actor)
                .body()
                .jsonPath()
                .getList("users");
        if (users == null) return "";
        return users.stream()
                .filter(u -> username.equals(u.get("username")))
                .map(u -> {
                    Object val = u.get(field);
                    return val != null ? val.toString() : "";
                })
                .findFirst()
                .orElse("");
    }

    public static class Builder {
        private final String username;

        public Builder(String username) {
            this.username = username;
        }

        public UserFieldValue field(String field) {
            return new UserFieldValue(username, field);
        }
    }
}
