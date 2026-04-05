# language: es

Característica: Gestión de incidentes via API REST — HU-001
  Como analista SOC o administrador
  Quiero crear y consultar incidentes a través de la API
  Para gestionar los eventos de seguridad detectados en CyberGuard

  Antecedentes:
    Dado que el administrador se autentica con credenciales válidas en la API

  @positivo @smoke @HU-001
  Escenario: Creación exitosa de incidente desde una amenaza de severidad crítica
    Dado que existe una amenaza de severidad crítica en el sistema
    Cuando el administrador crea un incidente a partir de esa amenaza
    Entonces la API responde con código 201
    Y el cuerpo contiene "success" con valor "true"
    Y el campo "status" del incidente creado es "open"
    Cuando el administrador consulta el listado de incidentes
    Entonces la API responde con código 200
    Y el incidente recién creado aparece en el listado

  @negativo @seguridad @sin-autenticacion @HU-001
  Escenario: Rechazo de creación de incidente sin token de autenticación
    Dado que existe una amenaza de severidad crítica en el sistema
    Cuando se intenta crear un incidente sin token de autenticación
    Entonces la API responde con código 401

  @negativo @regla-negocio @amenaza-inexistente @HU-001
  Escenario: Rechazo cuando el UUID de amenaza no existe en el sistema
    Cuando el administrador intenta crear un incidente a partir de una amenaza con ID inexistente
    Entonces la API responde con código 404
    Y el cuerpo de la respuesta contiene el error "Threat not found"

  @negativo @regla-negocio @severidad-baja @HU-001
  Escenario: Rechazo de creación de incidente desde amenaza con severidad insuficiente
    Dado que existe una amenaza de severidad "medium" en el sistema
    Cuando el administrador intenta crear un incidente a partir de esa amenaza de baja severidad
    Entonces la API responde con código 422
    Y el cuerpo de la respuesta contiene el error "severidad ALTA o CRÍTICA"
