# devops_007d_ols — Pipeline DevOps para el Microservicio de Catálogo (ms-productos)

## 1. Introducción y Propósito del Proyecto

Este repositorio contiene la base del microservicio `ms-productos` (catálogo de 
productos), desarrollado previamente en Spring Boot, utilizado como base para 
implementar el pipeline DevOps del curso Ingeniería DevOps. 
El objetivo de este encargo es demostrar el uso de control de versiones colaborativo 
mediante Git y GitHub, la automatización de integración continua con 
GitHub Actions, y la ejecución del microservicio con Docker Compose.

**Integrantes:** Javier Romero (Fakiier) — Catalina Campos (miucat05)

**Stack técnico:** Java 21, Spring Boot 3.5.15, Maven, MySQL (producción) / H2 (tests), 
Spring Security con JWT, Eureka Client, Flyway, JaCoCo, Snyk, Dependabot, Docker y Docker Compose.

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
- `feature/ajuste-dockerfile` y `feature/docker-compose`: Dockerfile y Docker Compose del microservicio
- `feature/pipeline-ep2`: pipeline de CI (pruebas, reporte HTML y publicación en ghcr.io)

## 3. Convenciones de Commits y Naming de Ramas

**Formato de mensajes de commit:**
- `feat:` nueva funcionalidad
- `fix:` corrección de errores
- `ci:` cambios de automatización
- `docs:` cambios de documentación
- `chore:` configuración del entorno

**Naming de ramas:** `feature/<nombre>` para nuevas funcionalidades y `hotfix/<nombre>` para arreglos urgentes.

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

## 5. Docker y Docker Compose

Para ejecutar el microservicio con su base de datos:

1. Crear el archivo `.env` a partir del ejemplo (el `.env` no se sube al repositorio):

   ```
   copy .env.example .env
   ```

2. Levantar los servicios:

   ```
   docker compose up --build
   ```

3. Verificar que ambos estén `healthy`:

   ```
   docker compose ps
   ```

4. Detener:

   ```
   docker compose down
   ```

La app queda en el puerto `9095` y MySQL en el `3307`. Los datos de MySQL y los logs de la 
aplicación se guardan en los volúmenes `mysql_data` y `app_logs`, así que no se pierden al hacer 
`docker compose down`.

## 6. Automatización con GitHub Actions (CI/CD)

El repositorio cuenta con un workflow en `.github/workflows/ci.yml` que se ejecuta
automáticamente con cada `push` a ramas `feature/**`, `develop` y `main`, y con cada
`pull request` hacia `develop` o `main`. Usa Java 21 (distribución Temurin) y tiene cinco jobs:

| Job | Qué hace |
|---|---|
| `build` | Descarga el código, configura Java 21 y compila el proyecto con Maven |
| `test` | Ejecuta las pruebas unitarias, verifica la cobertura mínima de JaCoCo, genera el reporte HTML y lo sube como artefacto |
| `security` | Analiza las dependencias con Snyk (`snyk test`) y el código con Snyk Code; falla si hay vulnerabilidades altas o críticas que no estén aceptadas en el archivo `.snyk` |
| `push-ghcr` | Construye la imagen Docker y la publica en ghcr.io (solo en `push` a `develop` o `main`, y solo si `build`, `test` y `security` pasan) |
| `deploy` | Descarga la imagen publicada, la despliega con Docker Compose en un entorno simulado (`staging` para `develop`, `production` para `main`), espera a que esté `healthy`, hace una prueba de humo y registra la trazabilidad en el resumen de la ejecución |

Si alguna prueba, la cobertura mínima o el análisis de seguridad falla, no se publica ni se despliega la imagen.

**Nota sobre cobertura de código:** el proyecto incluye el plugin JaCoCo con una 
regla de cobertura mínima de 80%, en evaluaciones anteriores esto se ignoró usando un skip y simplemente teniendo aproximadamente un 48% de cobertura, para su mejor funcionamiento hemos decidido ampliar este porcentaje hasta un 81%.

## 7. Pruebas unitarias y reporte HTML

El proyecto tiene 44 pruebas, con esta lista: ProductoControllerTest: 6, ProductoServiceTest: 8, MsProductosApplicationTests: 1, GlobalExceptionHandlerTest: 9, JwtServiceTest: 9, JwtAuthFilterTest: 5 y UserClientTest: 6. Se ejecutan con JDK 21:

```
mvnw.cmd clean test
```

El reporte de cobertura queda en target/site/jacoco/index.html

Para generar el reporte HTML localmente:

```
mvnw.cmd clean test org.apache.maven.plugins:maven-surefire-report-plugin:3.5.2:report-only
```

Queda en `target/reports/surefire.html`. En GitHub, el reporte se descarga desde la pestaña 
**Actions**, en la sección **Artifacts** de cada ejecución (`reporte-pruebas-html`).

## 8. Imagen en ghcr.io

El pipeline publica la imagen en GitHub Container Registry:

```
docker pull ghcr.io/fakiier/devops_007d_ols:develop
```

## 9. Declaración de Uso de Inteligencia Artificial

Se utilizó Claude como apoyo para: comprender conceptos de Git/GitFlow 
y GitHub Actions, resolver errores de configuración del pipeline de CI/CD (versión 
de Java, umbral de cobertura de JaCoCo, discrepancia de rutas entre controlador y 
tests), configurar Docker Compose, el job de pruebas con reporte HTML y la publicación 
en ghcr.io, y tener una estructura basica para el README. Las decisiones finales, el código del proyecto 
y las justificaciones técnicas fueron hechas por nosotros, los integrantes del grupo.

## 10. Reflexiones Individuales

**Javier Romero:**  
Como reflexión yo diría que los contenidos vistos en esta unidad son de vital importancia en nuestros futuros trabajos, aunque sean un tanto simples, a fin de cuentas son cosas básicas que se ocuparán día a día, por lo que aprenderlas sin dudas ayuda a que nuestra formación como futuros profesionales sea aún más completa.
Esta herramienta GitFlow nos ayuda a poder ser más organizados a la hora de realizar proyectos que suelen ser más importantes, sobre todo si es en grupos ya que cada uno puede interactuar en su linea y posteriormente acceder a versiones más nuevas o antiguas del código.
Sobre el tema IA, está claro que hoy en día tenemos que aprender a convivir con ella ya que es una nueva tecnología que llegó para quedarse, pero no por eso nos tenemos que dejar llevar por ella y dejar que haga todo por nosotros, es por ello que en nuestro trabajo se utilizó principalmente como una guía y consultor de errores dejando las decisiones más importantes para el factor humano, o sea, nosotros.

**Catalina Campos:**  
Personalmente, creo que uno de los mayores aprendizajes de esta evaluación y el proceso que conlleva es que al tener un orden en los trabajos, se puede ser mucho mas eficiente. Cada uno por su lado y cuando ya tenemos casi todo hecho, juntarlo no es tan difícil y tampoco vamos sobre escribiendo lo que nuestro compañero hace. Además que tener todo detallado ayuda mucho a la hora de poder comprender de mejor manera el trabajo hecho.
Tambien en el pull request hace que tengamos al otro para apoyarnos y que tambien vea y revise lo que estamos haciendo, y pueda quiza ver errores o detalles donde yo no los vi. 
Y sobre el uso de la ia, no esta del todo erroneo utilizarla, siempre y cuando sea para apoyarnos y no hacer solo copia y pega, porque no aprenderiamos nada de esa manera.
