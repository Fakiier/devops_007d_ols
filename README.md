# devops_007d_ols — Pipeline DevOps para el Microservicio de Catálogo (ms-productos)

## 1. Introducción y Propósito del Proyecto

Este repositorio contiene la base del microservicio `ms-productos` (catálogo de 
productos), desarrollado previamente en Spring Boot, utilizado como base para 
implementar el primer pipeline DevOps del curso Ingeniería DevOps. 
El objetivo de este encargo es demostrar el uso de control de versiones colaborativo 
mediante Git y GitHub, y la automatización básica de integración continua con 
GitHub Actions.

**Integrantes:** Javier Romero (Fakiier) — Catalina Campos (miucat05)

**Stack técnico:** Java 21, Spring Boot 3.4.4, Maven, MySQL (producción) / H2 (tests), 
Spring Security con JWT, Eureka Client, Flyway, JaCoCo.

## 2. Estrategia de Ramificación y Justificación

Se implementó **GitFlow** como modelo de ramificación. Se eligió este modelo porque:

- El encargo requería explícitamente ramas diferenciadas para desarrollo (`develop`), 
  producción (`main`), nuevas funcionalidades (`feature/*`) y arreglos urgentes (`hotfix/*`).
- Al trabajar en pareja sobre el mismo microservicio, GitFlow nos permitió que cada uno
  desarrolle su propia trabajo en una rama independiente sin afectar el trabajo 
  del otro, integrando los cambios de forma controlada mediante pull Requests.
- Separar `main` de `develop` permitió mantener siempre una versión estable del 
  microservicio, mientras el trabajo en curso se valida en `develop` antes de integrarse.

Ramas utilizadas en este repositorio:
- `main`: versión estable del microservicio
- `develop`: rama de integración de los cambios en desarrollo
- `feature/busqueda-productos`: nuevo endpoint para verificar existencia de un producto
- `feature/<validacion-stock>`: validación de stock no negativo al crear un producto
- `hotfix/<corregir-validacion-producto>`: corrección de la validación de existencia de producto

## 3. Convenciones de Commits y Naming de Ramas

**Formato de mensajes de commit:**
- `feat:` nueva funcionalidad
- `fix:` corrección de errores
- `ci:` cambios de automatización
- `docs:` cambios de documentación
- `chore:` configuración del entorno
## 4. Estrategia de Revisión de Pull Requests

Cada Pull Request fue revisado por el otro integrante del equipo antes de aprobar 
el merge, verificando:

- Que el pipeline de GitHub Actions (CI) pasara en verde antes de mergear.
- Que la rama base del Pull Request fuera la correcta según el tipo de cambio 
  (`develop` para features, `main` para hotfix).
- Que el código compilara sin errores y no rompiera funcionalidad existente.

Se detectó y corrigió durante el desarrollo una inconsistencia entre la ruta 
definida en el controlador (`/exist/{id}`) y la esperada por las pruebas 
(`/exists/{id}`), resuelta mediante un commit adicional de corrección.

## 5. Automatización con GitHub Actions (CI/CD)

El repositorio cuenta con un workflow en `.github/workflows/ci.yml` que:

- Se ejecuta automáticamente con cada `push` a `develop` y `pull request` hacia `main`.
- Descarga el código con `actions/checkout@v7`.
- Configura Java 21 (distribución Temurin) con `actions/setup-java@v7`.
- Compila el proyecto y ejecuta las pruebas unitarias y de integración con Maven 
  (`mvn clean install`).

**Nota sobre cobertura de código:** el proyecto incluye el plugin JaCoCo con una 
regla de cobertura mínima de 80%. Dado que el objetivo de esta primera etapa del 
pipeline es validar la integración y automatización del flujo (no alcanzar un 
porcentaje específico de cobertura), se optó por omitir la verificación estricta 
de este umbral en el pipeline (`-Djacoco.skip=true`), manteniendo la ejecución 
real de los tests como parte de la validación de cada cambio.

## 6. Declaración de Uso de Inteligencia Artificial

Se utilizó Claude como apoyo para: comprender conceptos de Git/GitFlow 
y GitHub Actions, resolver errores de configuración del pipeline de CI/CD (versión 
de Java, umbral de cobertura de JaCoCo, discrepancia de rutas entre controlador y 
tests), y tener una estructura basica para el README. Las decisiones finales, el código del proyecto 
y las justificaciones técnicas fueron hechas por nosotros, los integrantes del grupo.

## 7. Reflexiones Individuales

**Javier Romero:**  
Como reflexión yo diría que los contenidos vistos en esta unidad son de vital importancia en nuestros futuros trabajos, aunque sean un tanto simples, a fin de cuentas son cosas básicas que se ocuparán día a día, por lo que aprenderlas sin dudas ayuda a que nuestra formación como futuros profesionales sea aún más completa.
Esta herramienta GitFlow nos ayuda a poder ser más organizados a la hora de realizar proyectos que suelen ser más importantes, sobre todo si es en grupos ya que cada uno puede interactuar en su linea y posteriormente acceder a versiones más nuevas o antiguas del código.
Sobre el tema IA, está claro que hoy en día tenemos que aprender a convivir con ella ya que es una nueva tecnología que llegó para quedarse, pero no por eso nos tenemos que dejar llevar por ella y dejar que haga todo por nosotros, es por ello que en nuestro trabajo se utilizó principalmente como una guía y consultor de errores dejando las decisiones más importantes para el factor humano, o sea, nosotros.
**Catalina Campos:**  
Personalmente, creo que uno de los mayores aprendizajes de esta evaluación y el proceso que conlleva es que al tener un orden en los trabajos, se puede ser mucho mas eficiente. Cada uno por su lado y cuando ya tenemos casi todo hecho, juntarlo no es tan difícil y tampoco vamos sobre escribiendo lo que nuestro compañero hace. Además que tener todo detallado ayuda mucho a la hora de poder comprender de mejor manera el trabajo hecho.
Tambien en el pull request hace que tengamos al otro para apoyarnos y que tambien vea y revise lo que estamos haciendo, y pueda quiza ver errores o detalles donde yo no los vi. 
Y sobre el uso de la ia, no esta del todo erroneo utilizarla, siempre y cuando sea para apoyarnos y no hacer solo copia y pega, porque no aprenderiamos nada de esa manera.

