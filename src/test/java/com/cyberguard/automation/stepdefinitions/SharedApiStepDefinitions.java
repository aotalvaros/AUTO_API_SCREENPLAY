package com.cyberguard.automation.stepdefinitions;

import com.cyberguard.automation.questions.ResponseField;
import com.cyberguard.automation.questions.ResponseStatusCode;
import com.cyberguard.automation.tasks.AuthenticateViaApi;
import com.cyberguard.automation.util.TestData;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actors.OnStage;

import static org.assertj.core.api.Assertions.assertThat;

public class SharedApiStepDefinitions {

    @Dado("que el administrador se autentica con credenciales válidas en la API")
    public void adminAuthenticatedShared() {
        Actor admin = OnStage.theActorCalled("admin");
        admin.attemptsTo(
                AuthenticateViaApi.withCredentials(TestData.ADMIN_USERNAME, TestData.ADMIN_PASSWORD)
        );
        assertThat(admin.asksFor(ResponseStatusCode.value())).isEqualTo(200);
        String token = admin.asksFor(ResponseField.withPath("token"));
        assertThat(token).isNotEmpty();

        admin.remember("adminToken", token);
    }

    @Dado("que un analista SOC se autentica con sus credenciales en la API")
    public void analystAuthenticatedShared() {
        Actor admin = OnStage.theActorCalled("admin");
        admin.attemptsTo(
                AuthenticateViaApi.withCredentials(TestData.ANALYST_USERNAME, TestData.ANALYST_PASSWORD)
        );
        int status = admin.asksFor(ResponseStatusCode.value());
        assertThat(status)
                .as("Login del analista SOC (%s) debe retornar 200. "
                   + "Verifique que el usuario exista en Firebase Auth con la contraseña correcta.",
                   TestData.ANALYST_USERNAME)
                .isEqualTo(200);
        String token = admin.asksFor(ResponseField.withPath("token"));
        assertThat(token).as("Token del analista SOC").isNotEmpty();
        admin.remember("analystToken", token);
    }

    @Entonces("la API responde con código {int}")
    public void apiRespondsWithStatusCode(int expectedCode) {
        Actor actor = OnStage.theActorInTheSpotlight();
        assertThat(actor.asksFor(ResponseStatusCode.value()))
                .as("Código HTTP esperado %d", expectedCode)
                .isEqualTo(expectedCode);
    }

    @Entonces("el cuerpo contiene {string} con valor {string}")
    public void bodyContainsFieldWithValue(String field, String expectedValue) {
        Actor actor = OnStage.theActorInTheSpotlight();
        assertThat(actor.asksFor(ResponseField.withPath(field)))
                .as("Campo '%s' en la respuesta", field)
                .isEqualTo(expectedValue);
    }

    @Entonces("el cuerpo de la respuesta contiene el error {string}")
    public void bodyContainsError(String expectedMessage) {
        Actor actor = OnStage.theActorInTheSpotlight();
        String errorField = actor.asksFor(ResponseField.withPath("error"));
        assertThat(errorField)
                .as("Mensaje de error esperado: '%s'", expectedMessage)
                .isNotNull()
                .containsIgnoringCase(expectedMessage);
    }
}
