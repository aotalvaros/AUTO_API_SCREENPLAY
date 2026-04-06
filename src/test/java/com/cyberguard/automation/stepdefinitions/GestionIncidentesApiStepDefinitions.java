package com.cyberguard.automation.stepdefinitions;

import com.cyberguard.automation.questions.IncidentInList;
import com.cyberguard.automation.questions.ResponseField;
import com.cyberguard.automation.questions.ResponseStatusCode;
import com.cyberguard.automation.tasks.CreateIncident;
import com.cyberguard.automation.tasks.CreateThreat;
import com.cyberguard.automation.tasks.GetIncidents;
import com.cyberguard.automation.util.TestData;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.rest.interactions.Post;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Step definitions exclusivos de Gestión de Incidentes (HU-001).
 *
 * Los steps compartidos (autenticación, código HTTP, cuerpo de error)
 * viven en {@link SharedApiStepDefinitions} para evitar AmbiguousStepDefinitions.
 *
 * El token del admin se recupera via actor.recall("adminToken") — el Stage
 * de Screenplay es el punto de encuentro entre clases de step definitions.
 */
public class GestionIncidentesApiStepDefinitions {

    private String criticalThreatId;
    private String mediumThreatId;

    private static final String NON_EXISTENT_UUID = "00000000-0000-0000-0000-000000000000";

    // ── Helper de token ─────────────────────────────────────────────────────────

    private String adminToken() {
        return OnStage.theActorCalled("admin").recall("adminToken");
    }

    // ── Setup de amenazas ─────────────────────────────────────────────────────

    @Dado("que existe una amenaza de severidad crítica en el sistema")
    public void criticalThreatExists() throws InterruptedException {
        Actor admin = OnStage.theActorInTheSpotlight();
        admin.attemptsTo(
                CreateThreat.withData("malware", "critical", TestData.VALID_SOURCE_IP,
                        "Troyano en servidor principal — escenario automatizado E2E", adminToken())
        );
        assertThat(admin.asksFor(ResponseStatusCode.value())).isEqualTo(201);
        Thread.sleep(500); // Dar tiempo al worker de RabbitMQ para procesar
        criticalThreatId = admin.asksFor(ResponseField.withPath("threatId"));
        assertThat(criticalThreatId)
                .as("El ID de la amenaza crítica no debe estar vacío")
                .isNotEmpty();
    }

    @Dado("que existe una amenaza de severidad {string} en el sistema")
    public void threatWithSeverityExists(String severity) throws InterruptedException {
        Actor admin = OnStage.theActorInTheSpotlight();
        admin.attemptsTo(
                CreateThreat.withData("phishing", severity, TestData.VALID_SOURCE_IP,
                        "Amenaza de severidad " + severity + " — escenario automatizado E2E", adminToken())
        );
        assertThat(admin.asksFor(ResponseStatusCode.value())).isEqualTo(201);
        Thread.sleep(500);
        mediumThreatId = admin.asksFor(ResponseField.withPath("threatId"));
        assertThat(mediumThreatId).isNotEmpty();
    }

    // ── Creación de incidentes ────────────────────────────────────────────────

    @Cuando("el administrador crea un incidente a partir de esa amenaza")
    public void adminCreatesIncidentFromCriticalThreat() {
        Actor admin = OnStage.theActorInTheSpotlight();
        admin.attemptsTo(CreateIncident.fromThreat(criticalThreatId, adminToken()));
    }

    @Cuando("se intenta crear un incidente sin token de autenticación")
    public void createIncidentWithoutToken() {
        Actor admin = OnStage.theActorInTheSpotlight();
        admin.attemptsTo(
                Post.to("/api/incidents")
                        .with(request -> request
                                .header("Content-Type", "application/json")
                                .body("{\"threatId\":\"" + criticalThreatId + "\"}")
                        )
        );
    }

    @Cuando("el administrador intenta crear un incidente a partir de esa amenaza de baja severidad")
    public void adminCreatesIncidentFromLowSeverityThreat() {
        Actor admin = OnStage.theActorInTheSpotlight();
        admin.attemptsTo(CreateIncident.fromThreat(mediumThreatId, adminToken()));
    }

    @Cuando("el administrador intenta crear un incidente a partir de una amenaza con ID inexistente")
    public void adminCreatesIncidentFromNonExistentThreat() {
        Actor admin = OnStage.theActorInTheSpotlight();
        admin.attemptsTo(CreateIncident.fromThreat(NON_EXISTENT_UUID, adminToken()));
    }

    // ── Consulta de incidentes ────────────────────────────────────────────────

    @Cuando("el administrador consulta el listado de incidentes")
    public void adminListsIncidents() throws InterruptedException {
        Thread.sleep(500);
        Actor admin = OnStage.theActorInTheSpotlight();
        admin.attemptsTo(GetIncidents.withToken(adminToken()));
    }

    // ── Assertions ────────────────────────────────────────────────────────────

    @Entonces("el campo {string} del incidente creado es {string}")
    public void incidentFieldEquals(String field, String expectedValue) {
        Actor actor = OnStage.theActorInTheSpotlight();
        assertThat(actor.asksFor(ResponseField.withPath("incident." + field)))
                .as("Campo incident.%s", field)
                .isEqualTo(expectedValue);
    }

    @Entonces("el incidente recién creado aparece en el listado")
    public void newIncidentAppearsInList() {
        Actor actor = OnStage.theActorInTheSpotlight();
        assertThat(actor.asksFor(IncidentInList.forThreat(criticalThreatId)))
                .as("El incidente para la amenaza '%s' debe aparecer en el listado", criticalThreatId)
                .isTrue();
    }

}
