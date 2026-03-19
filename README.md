# AUTO_API_SCREENPLAY - CyberGuard System

Automatización de pruebas de API REST para **CyberGuard System** utilizando el patrón **Screenplay** con **Serenity Rest**.

---

## Repositorio bajo prueba

Este proyecto automatiza pruebas sobre la API de **CyberGuard System**:
> [https://github.com/aotalvaros/cyberguard-system](https://github.com/aotalvaros/cyberguard-system)

---

## Descripción

Este proyecto valida el ciclo de vida completo (CRUD) de amenazas a través de la API REST de CyberGuard System, cubriendo los **4 verbos HTTP** en un único escenario secuencial:

| # | Verbo HTTP | Endpoint | Acción |
|---|------------|----------|--------|
| 1 | **POST** | `/api/auth/login` | Autenticación y obtención de JWT |
| 2 | **POST** | `/api/threats` | Creación de amenaza (x2) |
| 3 | **GET** | `/api/threats` | Consulta del listado de amenazas (x2) |
| 4 | **DELETE** | `/api/threats/:id` | Eliminación de amenaza (x2) |

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
        │   │   └── ApiHooks.java                          ← Cast con CallAnApi
        │   ├── questions/
        │   │   ├── ResponseField.java                     ← Extrae campo del JSON response
        │   │   ├── ResponseStatusCode.java                ← Obtiene código HTTP
        │   │   └── ThreatListContains.java                ← ¿La amenaza está en el listado?
        │   ├── runners/
        │   │   └── GestionAmenazasApiRunner.java          ← @Suite JUnit Platform
        │   ├── stepdefinitions/
        │   │   └── GestionAmenazasApiStepDefinitions.java ← Glue Cucumber ↔ Screenplay
        │   ├── tasks/
        │   │   ├── AuthenticateViaApi.java                ← POST /api/auth/login
        │   │   ├── CreateThreat.java                      ← POST /api/threats
        │   │   ├── DeleteThreat.java                      ← DELETE /api/threats/:id
        │   │   └── ListThreats.java                       ← GET /api/threats
        │   └── util/
        │       └── TestData.java                          ← Constantes de prueba
        └── resources/
            ├── features/
            │   └── gestion_amenazas_api.feature
            ├── serenity.conf
            └── cucumber.properties
```

### Patrón utilizado

**Screenplay + Serenity Rest:** Los actores poseen la habilidad `CallAnApi` y ejecutan Tasks que encapsulan las interacciones HTTP (Post, Get, Delete). Las Questions extraen datos de `LastResponse` para validar resultados.

| Componente | Clase(s) | Responsabilidad |
|------------|----------|----------------|
| `Task` | AuthenticateViaApi, CreateThreat, ListThreats, DeleteThreat | Acción HTTP (SRP por verbo/recurso) |
| `Question` | ResponseStatusCode, ResponseField, ThreatListContains | Consulta del response HTTP |
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

## Flujo del Escenario

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
