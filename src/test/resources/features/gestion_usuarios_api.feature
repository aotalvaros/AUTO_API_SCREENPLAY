# language: es

Característica: Gestión de usuarios via API REST — HU-008
  Como administrador del sistema
  Quiero gestionar usuarios mediante la API
  Para mantener el control de acceso al sistema CyberGuard

  Antecedentes:
    Dado que el administrador se autentica con credenciales válidas en la API

  @positivo @smoke @HU-008
  Escenario: Creación exitosa de un nuevo analista SOC
    Cuando el administrador crea un usuario con los datos del analista de prueba
    Entonces la API responde con código 201
    Y el cuerpo contiene "success" con valor "true"
    Y el campo "role" del usuario creado es "soc_analyst"
    Y el campo "isActive" del usuario creado es "true"

  @negativo @duplicado @HU-008
  Escenario: Rechazo de email duplicado al intentar crear usuario
    Cuando el administrador intenta crear un usuario con el email ya existente "admin@cyberguard.com"
    Entonces la API responde con código 409
    Y el cuerpo de la respuesta contiene el error "El correo electrónico ya está en uso"

  @negativo @seguridad @sin-autenticacion @HU-008
  Escenario: Rechazo de acceso sin token de autenticación al listar usuarios
    Cuando se intenta obtener el listado de usuarios sin token de autenticación
    Entonces la API responde con código 401

  @negativo @seguridad @rol-insuficiente @HU-008
  Escenario: Rechazo por rol insuficiente al intentar acceder a gestión de usuarios
    Dado que un analista SOC se autentica con sus credenciales en la API
    Cuando el analista intenta obtener el listado de usuarios
    Entonces la API responde con código 403

  @positivo @toggle-status @HU-008
  Escenario: Desactivación y posterior reactivación de un usuario
    Dado que existe el usuario de prueba registrado en el sistema
    Cuando el administrador desactiva al usuario de prueba
    Entonces la API responde con código 200
    Y el campo "isActive" de la respuesta es "false"
    Cuando el administrador reconsulta el listado de usuarios
    Entonces el campo "isActive" del usuario de prueba en el listado es "false"
    Cuando el administrador reactiva al usuario de prueba
    Entonces la API responde con código 200
    Y el campo "isActive" de la respuesta es "true"

  @negativo @seguridad @auto-proteccion @HU-008
  Escenario: El administrador no puede desactivar su propia cuenta
    Cuando el administrador intenta desactivar su propia cuenta
    Entonces la API responde con código 400
    Y el cuerpo de la respuesta contiene el error "No puede desactivar su propia cuenta"

  @positivo @actualizacion @HU-008
  Escenario: Actualización de rol de usuario existente
    Dado que existe el usuario de prueba registrado en el sistema
    Cuando el administrador cambia el rol del usuario de prueba a "incident_handler"
    Entonces la API responde con código 200
    Y el campo "role" del usuario actualizado es "incident_handler"
