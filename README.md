# AUTO_API_SCREENPLAY - CyberGuard System

Automatización de pruebas de API REST para **CyberGuard System** utilizando el patrón **Screenplay** con **Serenity Rest**.

---

## Repositorio bajo prueba

Este proyecto automatiza pruebas sobre la API de **CyberGuard System**:
> [https://github.com/aotalvaros/cyberguard-system](https://github.com/aotalvaros/cyberguard-system)

---

## Descripción

Este proyecto valida **3 módulos** de la API REST de CyberGuard System mediante escenarios positivos y negativos:

### 1. Gestión de Amenazas (CRUD completo)

| # | Verbo HTTP | Endpoint | Acción |
|---|------------|----------|--------|
| 1 | **POST** | `/api/auth/login` | Autenticación y obtención de JWT |
| 2 | **POST** | `/api/threats` | Creación de amenaza (x2) |
| 3 | **GET** | `/api/threats` | Consulta del listado de amenazas (x2) |
| 4 | **DELETE** | `/api/threats/:id` | Eliminación de amenaza (x2) |

### 2. Gestión de Incidentes (HU-001)

| # | Verbo HTTP | Endpoint | Acción |
|---|------------|----------|--------|
| 1 | **POST** | `/api/incidents` | Creación de incidente a partir de amenaza crítica |
| 2 | **GET** | `/api/incidents` | Consulta del listado de incidentes |
| — | — | — | Escenarios negativos: sin token (401), amenaza inexistente (404), severidad insuficiente (422) |

### 3. Gestión de Usuarios (HU-008)

| # | Verbo HTTP | Endpoint | Acción |
|---|------------|----------|--------|
| 1 | **POST** | `/api/users` | Creación de usuario (analista SOC) |
| 2 | **GET** | `/api/users` | Listado de usuarios |
| 3 | **PUT** | `/api/users/:id` | Actualización de rol |
| 4 | **PATCH** | `/api/users/:id/toggle-status` | Activar / desactivar usuario |
| — | — | — | Escenarios negativos: email duplicado (409), sin token (401), rol insuficiente (403), auto-desactivación (400) |

---

## Arquitectura

```
AUTO_API_SCREENPLAY/
├── build.gradle
├── settings.gradle
├── gradlew
├── gradle/wrapper/
└── src/
    └── test/
        ├── java/com/cyberguard/automation/
        │   ├── hooks/
        │   │   └── ApiHooks.java                              ← Cast con CallAnApi
        │   ├── questions/
        │   │   ├── ResponseField.java                         ← Extrae campo del JSON response
        │   │   ├── ResponseStatusCode.java                    ← Obtiene código HTTP
        │   │   ├── ThreatListContains.java                    ← ¿La amenaza está en el listado?
        │   │   ├── ThreatIdByDescription.java                 ← Busca ID de amenaza por descripción
        │   │   ├── IncidentInList.java                        ← ¿El incidente aparece en el listado?
        │   │   ├── UserInList.java                            ← ¿El usuario aparece en el listado?
        │   │   └── UserFieldValue.java                        ← Extrae campo de un usuario específico
        │   ├── runners/
        │   │   ├── GestionAmenazasApiRunner.java              ← @Suite — amenazas
        │   │   ├── GestionIncidentesApiRunner.java            ← @Suite — incidentes
        │   │   └── GestionUsuariosApiRunner.java              ← @Suite — usuarios
        │   ├── stepdefinitions/
        │   │   ├── SharedApiStepDefinitions.java              ← Pasos compartidos (auth, response)
        │   │   ├── GestionAmenazasApiStepDefinitions.java     ← Steps de amenazas
        │   │   ├── GestionIncidentesApiStepDefinitions.java   ← Steps de incidentes
        │   │   └── GestionUsuariosApiStepDefinitions.java     ← Steps de usuarios
        │   ├── tasks/
        │   │   ├── AuthenticateViaApi.java                    ← POST /api/auth/login
        │   │   ├── CreateThreat.java                          ← POST /api/threats
        │   │   ├── DeleteThreat.java                          ← DELETE /api/threats/:id
        │   │   ├── ListThreats.java                           ← GET /api/threats
        │   │   ├── CreateIncident.java                        ← POST /api/incidents
        │   │   ├── GetIncidents.java                          ← GET /api/incidents
        │   │   ├── CreateUser.java                            ← POST /api/users
        │   │   ├── GetUsers.java                              ← GET /api/users
        │   │   ├── UpdateUser.java                            ← PUT /api/users/:id
        │   │   └── ToggleUserStatus.java                      ← PATCH /api/users/:id/toggle-status
        │   └── util/
        │       └── TestData.java                              ← Constantes y datos de prueba
        └── resources/
            ├── features/
            │   ├── gestion_amenazas_api.feature
            │   ├── gestion_incidentes_api.feature
            │   └── gestion_usuarios_api.feature
            ├── serenity.conf
            └── cucumber.properties
```

### Patrón utilizado

**Screenplay + Serenity Rest:** Los actores poseen la habilidad `CallAnApi` y ejecutan Tasks que encapsulan las interacciones HTTP (Post, Get, Delete, Put, Patch). Las Questions extraen datos de `LastResponse` para validar resultados.

| Componente | Clase(s) | Responsabilidad |
|------------|----------|----------------|
| `Task` | AuthenticateViaApi, CreateThreat, ListThreats, DeleteThreat, CreateIncident, GetIncidents, CreateUser, GetUsers, UpdateUser, ToggleUserStatus | Acción HTTP (SRP por verbo/recurso) |
| `Question` | ResponseStatusCode, ResponseField, ThreatListContains, ThreatIdByDescription, IncidentInList, UserInList, UserFieldValue | Consulta del response HTTP |
| `StepDefinition` | SharedApiStepDefinitions (pasos compartidos), + 1 por módulo | Glue Cucumber ↔ Screenplay |
| `Hook` | ApiHooks | Configura Cast con CallAnApi |

---

## Stack Tecnológico

| Herramienta | Versión |
|-------------|---------|
| Java | 17 (OpenJDK) |
| Gradle | 8.12 |
| Serenity BDD | 4.2.12 |
| Serenity Screenplay REST | 4.2.12 |
| Serenity Rest Assured | 4.2.12 |
| Serenity Gradle Plugin | 5.3.7 |
| Cucumber | 7.20.1 |
| IDE | VS Code / IntelliJ IDEA |
| AI Assistant | GitHub Copilot |

---

## Prerequisitos

- **Java JDK 17+** instalado y configurado en `JAVA_HOME`
- **CyberGuard System** clonado y corriendo localmente:
  ```bash
  git clone https://github.com/aotalvaros/cyberguard-system.git
  cd cyberguard-system
  sudo docker compose up --build
  ```
  Verificar que la API esté disponible en `http://localhost:3000/health`

### Datos de ambiente requeridos

Los escenarios dependen de que ciertos usuarios existan en **Firebase Auth** y en **PostgreSQL**:

| Usuario | Email | Contraseña | Rol (PostgreSQL) | Requerido por |
|---------|-------|------------|------------------|---------------|
| Administrador | `admin@cyberguard.com` | `AdminSofka123456` | `admin` | Todos los features |
| Analista SOC | `soc@cyberguard.com` | `SocSofka123456` | `soc_analyst` | `gestion_usuarios_api.feature` (escenario rol insuficiente) |

> **Nota:** El usuario analista SOC (`soc@cyberguard.com`) debe crearse manualmente en Firebase Console del proyecto CyberGuard y tener asignado el rol `soc_analyst` en PostgreSQL. Sin este usuario, el escenario de **rechazo por rol insuficiente** fallará en el paso de autenticación.

---

## Instalación

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/aotalvaros/AUTO_API_SCREENPLAY.git
   cd AUTO_API_SCREENPLAY
   ```

2. Verificar que Gradle Wrapper esté disponible:
   ```bash
   ./gradlew --version
   ```

---

## Ejecución de Tests

### Ejecutar todos los tests y generar reporte
```bash
./gradlew clean test aggregate
```

### Abrir el reporte Serenity (Linux)
```bash
xdg-open target/site/serenity/index.html
```

---

## Reportes

Tras la ejecución, Serenity BDD genera un reporte HTML detallado en:

```
target/site/serenity/index.html
```

El reporte incluye:
- Resultado de cada paso del escenario (passed / failed / error)
- Detalle de cada request HTTP (URL, headers, body, response)
- Código de estado y payload de cada respuesta
- Tiempo de ejecución por paso

---

## Flujos de los Escenarios

### Amenazas — CRUD completo
```
POST /api/auth/login        → Obtiene JWT token
    │
    ▼
POST /api/threats           → Crea amenaza #1 (malware, high)
    │
    ▼
GET  /api/threats           → Verifica que amenaza #1 está en el listado
    │
    ▼
POST /api/threats           → Crea amenaza #2 (phishing, critical)
    │
    ▼
GET  /api/threats           → Verifica que ambas amenazas están en el listado
    │
    ▼
DELETE /api/threats/:id1    → Elimina amenaza #1
    │
    ▼
DELETE /api/threats/:id2    → Elimina amenaza #2
```

### Incidentes — Creación y consulta + negativos
```
POST /api/auth/login        → Autenticación admin
    │
    ▼
POST /api/threats           → Crea amenaza crítica (prerequisito)
    │
    ▼
POST /api/incidents         → Crea incidente a partir de la amenaza
    │
    ▼
GET  /api/incidents         → Verifica que el incidente aparece en el listado
    │
    ▼
Escenarios negativos: sin token (401), amenaza inexistente (404), severidad insuficiente (422)
```

### Usuarios — CRUD + toggle status + negativos
```
POST /api/auth/login        → Autenticación admin
    │
    ▼
POST /api/users             → Crea usuario (analista SOC)
    │
    ▼
GET  /api/users             → Listado de usuarios
    │
    ▼
PUT  /api/users/:id         → Actualización de rol
    │
    ▼
PATCH /api/users/:id/toggle → Desactivar / reactivar usuario
    │
    ▼
Escenarios negativos: email duplicado (409), sin token (401),
                       rol insuficiente (403), auto-desactivación (400)
```
