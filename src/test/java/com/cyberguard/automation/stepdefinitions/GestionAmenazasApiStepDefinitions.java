package com.cyberguard.automation.stepdefinitions;

import com.cyberguard.automation.questions.ResponseField;
import com.cyberguard.automation.questions.ResponseStatusCode;
import com.cyberguard.automation.questions.ThreatIdByDescription;
import com.cyberguard.automation.questions.ThreatListContains;
import com.cyberguard.automation.tasks.AuthenticateViaApi;
import com.cyberguard.automation.tasks.CreateThreat;
import com.cyberguard.automation.tasks.DeleteThreat;
import com.cyberguard.automation.tasks.ListThreats;
import com.cyberguard.automation.util.TestData;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actors.OnStage;

import static org.assertj.core.api.Assertions.assertThat;

public class GestionAmenazasApiStepDefinitions {

    private String authToken;
    private String firstDescription;
    private String secondDescription;
    private String firstThreatUuid;
    private String secondThreatUuid;

    @Dado("que el analista se autentica en la API con credenciales válidas")
    public void authenticateViaApi() {
        Actor analista = OnStage.theActorCalled("analista");
        analista.attemptsTo(
                AuthenticateViaApi.withCredentials(TestData.ADMIN_USERNAME, TestData.ADMIN_PASSWORD)
        );
        assertThat(analista.asksFor(ResponseStatusCode.value())).isEqualTo(200);
        authToken = analista.asksFor(ResponseField.withPath("token"));
    }

    @Cuando("crea una amenaza de tipo {string} con severidad {string} y descripción {string}")
    public void createFirstThreat(String type, String severity, String description) {
        firstDescription = description;
        Actor analista = OnStage.theActorInTheSpotlight();
        analista.attemptsTo(
                CreateThreat.withData(type, severity, TestData.VALID_SOURCE_IP, description, authToken)
        );
    }

    @Entonces("la API responde con código {int} y retorna el identificador de la amenaza")
    public void verifyFirstThreatCreated(int expectedStatus) {
        Actor analista = OnStage.theActorInTheSpotlight();
        assertThat(analista.asksFor(ResponseStatusCode.value())).isEqualTo(expectedStatus);
        String success = analista.asksFor(ResponseField.withPath("success"));
        assertThat(success).isEqualTo("true");
    }

    @Cuando("consulta el listado de amenazas registradas")
    public void listThreats() throws InterruptedException {
        Thread.sleep(2000);
        Actor analista = OnStage.theActorInTheSpotlight();
        analista.attemptsTo(ListThreats.withToken(authToken));
    }

    @Entonces("la API responde con código {int} y la amenaza creada aparece en el listado")
    public void verifyFirstThreatInList(int expectedStatus) {
        Actor analista = OnStage.theActorInTheSpotlight();
        assertThat(analista.asksFor(ResponseStatusCode.value())).isEqualTo(expectedStatus);
        assertThat(analista.asksFor(ThreatListContains.threatWithDescription(firstDescription))).isTrue();
        firstThreatUuid = analista.asksFor(ThreatIdByDescription.containing(firstDescription));
        assertThat(firstThreatUuid).isNotEmpty();
    }

    @Cuando("crea una segunda amenaza de tipo {string} con severidad {string} y descripción {string}")
    public void createSecondThreat(String type, String severity, String description) {
        secondDescription = description;
        Actor analista = OnStage.theActorInTheSpotlight();
        analista.attemptsTo(
                CreateThreat.withData(type, severity, TestData.VALID_SOURCE_IP, description, authToken)
        );
    }

    @Entonces("la API responde con código {int} y retorna el identificador de la segunda amenaza")
    public void verifySecondThreatCreated(int expectedStatus) {
        Actor analista = OnStage.theActorInTheSpotlight();
        assertThat(analista.asksFor(ResponseStatusCode.value())).isEqualTo(expectedStatus);
        String success = analista.asksFor(ResponseField.withPath("success"));
        assertThat(success).isEqualTo("true");
    }

    @Cuando("consulta nuevamente el listado de amenazas registradas")
    public void listThreatsAgain() throws InterruptedException {
        Thread.sleep(2000);
        Actor analista = OnStage.theActorInTheSpotlight();
        analista.attemptsTo(ListThreats.withToken(authToken));
    }

    @Entonces("la API responde con código {int} y ambas amenazas aparecen en el listado")
    public void verifyBothThreatsInList(int expectedStatus) {
        Actor analista = OnStage.theActorInTheSpotlight();
        assertThat(analista.asksFor(ResponseStatusCode.value())).isEqualTo(expectedStatus);
        assertThat(analista.asksFor(ThreatListContains.threatWithDescription(firstDescription))).isTrue();
        assertThat(analista.asksFor(ThreatListContains.threatWithDescription(secondDescription))).isTrue();
        secondThreatUuid = analista.asksFor(ThreatIdByDescription.containing(secondDescription));
        assertThat(secondThreatUuid).isNotEmpty();
    }

    @Cuando("elimina la primera amenaza creada")
    public void deleteFirstThreat() {
        Actor analista = OnStage.theActorInTheSpotlight();
        analista.attemptsTo(DeleteThreat.withId(firstThreatUuid, authToken));
    }

    @Entonces("la API responde con código {int} confirmando la eliminación")
    public void verifyFirstThreatDeleted(int expectedStatus) {
        Actor analista = OnStage.theActorInTheSpotlight();
        assertThat(analista.asksFor(ResponseStatusCode.value())).isEqualTo(expectedStatus);
        assertThat(analista.asksFor(ResponseField.withPath("success"))).isEqualTo("true");
    }

    @Cuando("elimina la segunda amenaza creada")
    public void deleteSecondThreat() {
        Actor analista = OnStage.theActorInTheSpotlight();
        analista.attemptsTo(DeleteThreat.withId(secondThreatUuid, authToken));
    }

    @Entonces("la API responde con código {int} confirmando la eliminación de la segunda amenaza")
    public void verifySecondThreatDeleted(int expectedStatus) {
        Actor analista = OnStage.theActorInTheSpotlight();
        assertThat(analista.asksFor(ResponseStatusCode.value())).isEqualTo(expectedStatus);
        assertThat(analista.asksFor(ResponseField.withPath("success"))).isEqualTo("true");
    }
}
