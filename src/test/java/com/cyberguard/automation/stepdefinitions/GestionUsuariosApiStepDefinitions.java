package com.cyberguard.automation.stepdefinitions;

import com.cyberguard.automation.questions.ResponseField;
import com.cyberguard.automation.questions.ResponseStatusCode;
import com.cyberguard.automation.questions.UserFieldValue;
import com.cyberguard.automation.questions.UserInList;
import com.cyberguard.automation.tasks.CreateUser;
import com.cyberguard.automation.tasks.GetUsers;
import com.cyberguard.automation.tasks.ToggleUserStatus;
import com.cyberguard.automation.tasks.UpdateUser;
import com.cyberguard.automation.util.TestData;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.rest.interactions.Get;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Step definitions exclusivos de Gestión de Usuarios (HU-008).
 *
 * Los steps compartidos (autenticación, código HTTP, cuerpo de error)
 * viven en {@link SharedApiStepDefinitions} para evitar AmbiguousStepDefinitions.
 *
 * El token del admin se recupera via actor.recall("adminToken") — el Stage
 * de Screenplay es el punto de encuentro entre clases de step definitions.
 */
public class GestionUsuariosApiStepDefinitions {

    private String createdUserId;

    // ── Helpers de token ──────────────────────────────────────────────────────

    private String adminToken() {
        return OnStage.theActorCalled("admin").recall("adminToken");
    }

    private String analystToken() {
        return OnStage.theActorCalled("admin").recall("analystToken");
    }

    // ── Creación de usuario ───────────────────────────────────────────────────

    @Cuando("el administrador crea un usuario con los datos del analista de prueba")
    public void adminCreatesTestUser() {
        Actor admin = OnStage.theActorInTheSpotlight();
        admin.attemptsTo(
                CreateUser.withData(
                        TestData.NEW_USER_EMAIL,
                        TestData.NEW_USER_FULL_NAME,
                        TestData.NEW_USER_USERNAME,
                        TestData.NEW_USER_ROLE,
                        adminToken()
                )
        );
    }

    @Cuando("el administrador intenta crear un usuario con el email ya existente {string}")
    public void adminCreatesUserWithDuplicateEmail(String email) {
        Actor admin = OnStage.theActorInTheSpotlight();
        admin.attemptsTo(
                CreateUser.withData(
                        email,
                        "Usuario Duplicado",
                        "usuario.duplicado",
                        TestData.NEW_USER_ROLE,
                        adminToken()
                )
        );
    }

    // ── Listado de usuarios ───────────────────────────────────────────────────

    @Cuando("se intenta obtener el listado de usuarios sin token de autenticación")
    public void listUsersWithoutToken() {
        Actor admin = OnStage.theActorInTheSpotlight();
        admin.attemptsTo(
                Get.resource("/api/admin/users")
        );
    }

    @Cuando("el analista intenta obtener el listado de usuarios")
    public void analystTriesToListUsers() {
        Actor admin = OnStage.theActorInTheSpotlight();
        admin.attemptsTo(GetUsers.withToken(analystToken()));
    }

    @Cuando("el administrador reconsulta el listado de usuarios")
    public void adminListsUsersAgain() throws InterruptedException {
        Thread.sleep(500);
        Actor admin = OnStage.theActorInTheSpotlight();
        admin.attemptsTo(GetUsers.withToken(adminToken()));
    }

    // ── Toggle de estado ──────────────────────────────────────────────────────

    @Dado("que existe el usuario de prueba registrado en el sistema")
    public void ensureTestUserExists() {
        Actor admin = OnStage.theActorInTheSpotlight();
        admin.attemptsTo(
                CreateUser.withData(
                        TestData.NEW_USER_EMAIL,
                        TestData.NEW_USER_FULL_NAME,
                        TestData.NEW_USER_USERNAME,
                        TestData.NEW_USER_ROLE,
                        adminToken()
                )
        );
        int status = admin.asksFor(ResponseStatusCode.value());
        if (status == 201) {
            createdUserId = admin.asksFor(ResponseField.withPath("user.id"));
        } else {
            admin.attemptsTo(GetUsers.withToken(adminToken()));
            createdUserId = resolveUserIdByUsername(admin, TestData.NEW_USER_USERNAME);
        }
        assertThat(createdUserId).isNotEmpty();
    }

    @Cuando("el administrador desactiva al usuario de prueba")
    public void adminDeactivatesTestUser() {
        Actor admin = OnStage.theActorInTheSpotlight();
        admin.attemptsTo(ToggleUserStatus.forUser(createdUserId, false, adminToken()));
    }

    @Cuando("el administrador reactiva al usuario de prueba")
    public void adminReactivatesTestUser() {
        Actor admin = OnStage.theActorInTheSpotlight();
        admin.attemptsTo(ToggleUserStatus.forUser(createdUserId, true, adminToken()));
    }

    @Cuando("el administrador intenta desactivar su propia cuenta")
    public void adminAttemptsToDeactivateSelf() {
        Actor admin = OnStage.theActorInTheSpotlight();
        // Obtener el UID del admin vía listado y reutilizarlo con ToggleUserStatus
        admin.attemptsTo(GetUsers.withToken(adminToken()));
        String adminId = resolveUserIdByUsername(admin, "admin");
        admin.attemptsTo(ToggleUserStatus.forUser(adminId, false, adminToken()));
    }

    // ── Actualización de usuario ──────────────────────────────────────────────

    @Cuando("el administrador cambia el rol del usuario de prueba a {string}")
    public void adminUpdatesUserRole(String newRole) {
        Actor admin = OnStage.theActorInTheSpotlight();
        admin.attemptsTo(
                UpdateUser.withData(createdUserId, TestData.NEW_USER_FULL_NAME, newRole, adminToken())
        );
    }

    // ── Assertions ────────────────────────────────────────────────────────────

    @Entonces("el campo {string} del usuario creado es {string}")
    public void createdUserFieldEquals(String field, String expectedValue) {
        Actor actor = OnStage.theActorInTheSpotlight();
        assertThat(actor.asksFor(ResponseField.withPath("user." + field)))
                .as("Campo user.%s", field)
                .isEqualTo(expectedValue);
    }

    @Entonces("el campo {string} del usuario actualizado es {string}")
    public void updatedUserFieldEquals(String field, String expectedValue) {
        Actor actor = OnStage.theActorInTheSpotlight();
        assertThat(actor.asksFor(ResponseField.withPath("user." + field)))
                .as("Campo user.%s tras actualización", field)
                .isEqualTo(expectedValue);
    }

    @Entonces("el campo {string} de la respuesta es {string}")
    public void responseFieldEquals(String field, String expectedValue) {
        Actor actor = OnStage.theActorInTheSpotlight();
        assertThat(actor.asksFor(ResponseField.withPath("user." + field)))
                .as("Campo user.%s en respuesta toggle", field)
                .isEqualTo(expectedValue);
    }

    @Entonces("el campo {string} del usuario de prueba en el listado es {string}")
    public void userFieldInListEquals(String field, String expectedValue) {
        Actor actor = OnStage.theActorInTheSpotlight();
        assertThat(actor.asksFor(UserFieldValue.forUsername(TestData.NEW_USER_USERNAME).field(field)))
                .as("Campo %s del usuario %s en el listado", field, TestData.NEW_USER_USERNAME)
                .isEqualTo(expectedValue);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String resolveUserIdByUsername(Actor actor, String username) {
        java.util.List<java.util.Map<String, Object>> users = net.serenitybdd.screenplay.rest.questions.LastResponse
                .received().answeredBy(actor).body().jsonPath().getList("users");
        if (users == null) return "";
        return users.stream()
                .filter(u -> username.equals(u.get("username")))
                .map(u -> u.get("id") != null ? u.get("id").toString() : "")
                .findFirst()
                .orElse("");
    }
}
