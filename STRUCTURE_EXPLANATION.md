# Estructura del Proyecto AUTO_API_SCREENPLAY

## Descripción General

Este proyecto implementa pruebas automatizadas de API REST bajo el patrón **Screenplay**
utilizando **Serenity Rest** con **Cucumber** como test runner y **Gradle** como gestor
de dependencias. Valida el ciclo de vida completo (CRUD) de amenazas en la API de
**CyberGuard System**.

---

## Diferencia clave con los proyectos Front-End

| Aspecto | Front-End (POM / Screenplay) | API (Screenplay REST) |
|---|---|---|
| **Habilidad del Actor** | `BrowseTheWeb` (WebDriver) | `CallAnApi` (REST Assured) |
| **Interacciones** | Click, Enter, Select (UI) | Post, Get, Delete (HTTP) |
| **Targets** | Localizadores CSS/XPath del DOM | No aplica |
| **Questions** | Estado visual de la UI | Contenido del response HTTP |
| **Screenshots** | Captura por paso | Desactivadas |
| **Driver** | Chrome (autodownload) | No requiere navegador |

---

## Árbol de Directorios

```
AUTO_API_SCREENPLAY/
├── build.gradle
├── settings.gradle
├── gradlew
├── gradlew.bat
├── .gitignore
├── README.md
├── STRUCTURE_EXPLANATION.md
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
└── src/
    └── test/
        ├── java/
        │   └── com/
        │       └── cyberguard/
        │           └── automation/
        │               ├── hooks/
        │               │   └── ApiHooks.java
        │               ├── questions/
        │               │   ├── ResponseField.java
        │               │   ├── ResponseStatusCode.java
        │               │   ├── ThreatIdByDescription.java
        │               │   └── ThreatListContains.java
        │               ├── runners/
        │               │   └── GestionAmenazasApiRunner.java
        │               ├── stepdefinitions/
        │               │   └── GestionAmenazasApiStepDefinitions.java
        │               ├── tasks/
        │               │   ├── AuthenticateViaApi.java
        │               │   ├── CreateThreat.java
        │               │   ├── DeleteThreat.java
        │               │   └── ListThreats.java
        │               └── util/
        │                   └── TestData.java
        └── resources/
            ├── features/
            │   └── gestion_amenazas_api.feature
            ├── serenity.conf
            └── cucumber.properties
```

---

## Descripción de Cada Elemento

### Archivos Raíz

#### `build.gradle`
Archivo de configuración de Gradle. Define las dependencias específicas para testing de
API: `serenity-screenplay-rest` y `serenity-rest-assured`, además de la base compartida
con los otros proyectos (Cucumber, JUnit Platform, AssertJ).

#### `settings.gradle`
Define el nombre del proyecto Gradle como `auto-api-screenplay`.

#### `serenity.conf`
Configuración de Serenity BDD en formato HOCON. A diferencia de los proyectos Front-End,
no configura WebDriver ni navegador. Define la URL base de la API (`http://127.0.0.1:3000`)
y desactiva las capturas de pantalla (`take.screenshots = DISABLED`).

#### `cucumber.properties`
Configura el glue de Cucumber apuntando a los paquetes `stepdefinitions` y `hooks`,
y habilita el plugin de reporte de Serenity.

---

### `src/test/resources/features/`

#### `gestion_amenazas_api.feature`
Archivo Gherkin que describe un único escenario secuencial que ejecuta el ciclo CRUD
completo de amenazas. Cubre los 4 verbos HTTP requeridos por el taller:

| Paso | Verbo HTTP | Endpoint | Propósito |
|------|------------|----------|-----------|
| Autenticación | POST | `/api/auth/login` | Obtener JWT |
| Crear amenaza #1 | POST | `/api/threats` | Registrar malware |
| Consultar listado | GET | `/api/threats` | Verificar amenaza #1 |
| Crear amenaza #2 | POST | `/api/threats` | Registrar phishing |
| Consultar listado | GET | `/api/threats` | Verificar ambas |
| Eliminar amenaza #1 | DELETE | `/api/threats/:id` | Limpiar datos |
| Eliminar amenaza #2 | DELETE | `/api/threats/:id` | Limpiar datos |

---

### `src/test/java/com/cyberguard/automation/`

---

#### `hooks/`

##### `ApiHooks.java`
Configura el escenario antes de cada ejecución usando `@Before` de Cucumber.
Inicializa el stage de Screenplay con un `Cast` donde cada actor posee la habilidad
`CallAnApi` apuntando a la URL base de la API.

```
@Before → OnStage.setTheStage(Cast.whereEveryoneCan(CallAnApi.at(BASE_URL)))
```

> A diferencia del Hook del proyecto Front-End (que usa `OnlineCast` con `BrowseTheWeb`),
> aquí se usa `Cast.whereEveryoneCan(CallAnApi.at(...))` porque no hay navegador involucrado.

---

#### `tasks/`
Contiene las tareas que encapsulan las interacciones HTTP. Cada clase implementa `Task`
y representa una única operación de negocio (SRP).

##### `AuthenticateViaApi.java`
- **Verbo:** POST
- **Endpoint:** `/api/auth/login`
- **Responsabilidad:** Enviar credenciales y obtener respuesta con JWT.
- **Parámetros:** username, password.

##### `CreateThreat.java`
- **Verbo:** POST
- **Endpoint:** `/api/threats`
- **Responsabilidad:** Crear una nueva amenaza con los datos proporcionados.
- **Parámetros:** type, severity, sourceIp, description, token (JWT).

##### `ListThreats.java`
- **Verbo:** GET
- **Endpoint:** `/api/threats`
- **Responsabilidad:** Consultar el listado completo de amenazas registradas.
- **Parámetros:** token (JWT).

##### `DeleteThreat.java`
- **Verbo:** DELETE
- **Endpoint:** `/api/threats/:threatId`
- **Responsabilidad:** Eliminar una amenaza específica por su UUID.
- **Parámetros:** threatId (UUID), token (JWT).

---

#### `questions/`
Contiene las preguntas que extraen información del último response HTTP recibido.
Cada clase implementa `Question<T>` y consulta `LastResponse`.

##### `ResponseStatusCode.java`
- **Retorna:** `Integer`
- **Responsabilidad:** Obtener el código de estado HTTP de la última respuesta (200, 201, etc.).

##### `ResponseField.java`
- **Retorna:** `String`
- **Responsabilidad:** Extraer un campo específico del body JSON usando un path
  (ej. `"token"`, `"success"`, `"threatId"`).

##### `ThreatListContains.java`
- **Retorna:** `Boolean`
- **Responsabilidad:** Verificar si existe una amenaza en el listado cuya descripción
  contenga el texto buscado. Se usa para validar que un POST fue procesado correctamente.

##### `ThreatIdByDescription.java`
- **Retorna:** `String`
- **Responsabilidad:** Obtener el UUID (`threatId`) de una amenaza buscando por su
  descripción en el listado del GET. Necesario porque la API asigna el UUID de forma
  asíncrona (el POST retorna un ID temporal, el GET retorna el UUID definitivo).

---

#### `stepdefinitions/`

##### `GestionAmenazasApiStepDefinitions.java`
Conecta los pasos Gherkin con las Tasks y Questions. Maneja el estado del escenario
(token JWT, descripciones y UUIDs de las amenazas creadas) como variables de instancia.

Flujo interno:
1. `@Dado` → Ejecuta `AuthenticateViaApi`, guarda el token.
2. `@Cuando` (crear) → Ejecuta `CreateThreat`, guarda la descripción.
3. `@Entonces` (verificar creación) → Consulta `ResponseStatusCode` y `ResponseField`.
4. `@Cuando` (listar) → Espera 2s (procesamiento asíncrono) y ejecuta `ListThreats`.
5. `@Entonces` (verificar listado) → Consulta `ThreatListContains` y `ThreatIdByDescription`.
6. `@Cuando` (eliminar) → Ejecuta `DeleteThreat` con el UUID obtenido.
7. `@Entonces` (verificar eliminación) → Consulta `ResponseStatusCode` y `ResponseField`.

---

#### `runners/`

##### `GestionAmenazasApiRunner.java`
Punto de entrada de las pruebas. Usa `@Suite` de JUnit Platform con `@IncludeEngines("cucumber")`
y apunta al classpath `features/` para descubrir los archivos `.feature`.

---

#### `util/`

##### `TestData.java`
Clase final con constructor privado que agrupa las constantes de datos de prueba:
credenciales del administrador y una IP de origen válida. Evita valores hardcodeados
dispersos en las clases.

---

## Flujo de Ejecución

```
.feature (Gherkin)
    │
    ▼
StepDefinitions           ← Traduce pasos Gherkin a código Java
    │
    ├──▶ Tasks             ← El Actor ejecuta operaciones HTTP
    │       │
    │       └──▶ Post / Get / Delete (Serenity REST interactions)
    │               │
    │               └──▶ REST Assured → HTTP Request → API
    │
    └──▶ Questions         ← El Actor consulta LastResponse
            │
            └──▶ Assertions (AssertJ) ← Valida código HTTP, campos JSON
```

---

## Particularidad: Procesamiento Asíncrono

La API de CyberGuard procesa las amenazas de forma asíncrona a través de RabbitMQ:

```
POST /api/threats
    │
    ▼
Producer (HTTP 201)  →  RabbitMQ  →  Worker  →  PostgreSQL
    │                                                │
    └─ threatId: 8 (temporal)                        └─ threatId: UUID (definitivo)
```

Por esta razón:
- El `threatId` del POST (entero temporal) **no coincide** con el del GET (UUID).
- Se buscan las amenazas **por descripción** en el GET para obtener el UUID definitivo.
- Se incluye una espera de 2 segundos entre POST y GET para dar tiempo al worker.

---

## Relación entre Capas y Principios SOLID

| Capa | Principio | Justificación |
|---|---|---|
| `tasks/` | **S** - Single Responsibility | Una tarea por verbo/recurso HTTP |
| `questions/` | **S** - Single Responsibility | Una pregunta por dato extraído |
| `stepdefinitions/` | **D** - Dependency Inversion | Depende de abstracciones (Task, Question) |
| `hooks/` | **S** - Single Responsibility | Solo configura el Cast con CallAnApi |
| `util/` | **O** - Open/Closed | Agregar constantes sin modificar Tasks |
