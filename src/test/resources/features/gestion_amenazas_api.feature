# language: es

Característica: Ciclo CRUD completo de amenazas en la API de CyberGuard System

  Escenario: Gestión completa del ciclo de vida de una amenaza via API REST
    Dado que el analista se autentica en la API con credenciales válidas
    Cuando crea una amenaza de tipo "malware" con severidad "high" y descripción "Troyano detectado en servidor de base de datos principal"
    Entonces la API responde con código 201 y retorna el identificador de la amenaza
    Cuando consulta el listado de amenazas registradas
    Entonces la API responde con código 200 y la amenaza creada aparece en el listado
    Cuando crea una segunda amenaza de tipo "phishing" con severidad "critical" y descripción "Campaña de phishing dirigida a empleados del area financiera"
    Entonces la API responde con código 201 y retorna el identificador de la segunda amenaza
    Cuando consulta nuevamente el listado de amenazas registradas
    Entonces la API responde con código 200 y ambas amenazas aparecen en el listado
    Cuando elimina la primera amenaza creada
    Entonces la API responde con código 200 confirmando la eliminación
    Cuando elimina la segunda amenaza creada
    Entonces la API responde con código 200 confirmando la eliminación de la segunda amenaza
