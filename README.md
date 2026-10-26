# Inventory Service

Servicio backend para la gestión de inventario por SKU y almacén. El proyecto está implementado con Java 17 y Spring Boot 3, sigue una organización hexagonal y persiste artículos y movimientos en MongoDB. También incluye una interfaz web sencilla renderizada con Thymeleaf.

## Contenido

- [Alcance funcional](#alcance-funcional)
- [Arquitectura](#arquitectura)
- [Modelo de dominio](#modelo-de-dominio)
- [API HTTP implementada](#api-http-implementada)
- [Interfaz web](#interfaz-web)
- [Persistencia y configuración](#persistencia-y-configuración)
- [Requisitos y puesta en marcha](#requisitos-y-puesta-en-marcha)
- [Pruebas y calidad](#pruebas-y-calidad)
- [Integración continua](#integración-continua)
- [Estructura del repositorio](#estructura-del-repositorio)
- [Contrato OpenAPI](#contrato-openapi)
- [Limitaciones conocidas](#limitaciones-conocidas)

## Alcance funcional

El servicio permite crear, consultar, modificar y eliminar artículos de inventario, además de registrar movimientos de stock. Cada artículo se identifica por la combinación de `sku` y `warehouse`; el stock disponible se calcula como `onHand - reserved`. Los movimientos admitidos por el adaptador Mongo son `IN`, `OUT`, `RESERVATION` y `RELEASE`.

Los datos se almacenan en dos colecciones MongoDB: `items` y `movements`. La ruta web `/inventory` presenta los artículos y el historial de movimientos disponibles.

## Arquitectura

El código se organiza en capas con dependencias dirigidas hacia el dominio:

- **Dominio (`domain`)**: modelos (`InventoryItem`, `Movement`, `AuditInfo`), reglas básicas e interfaces de puertos de entrada y salida. No depende de los documentos Mongo.
- **Aplicación (`application`)**: implementaciones de casos de uso y `InventoryService`, que coordina las operaciones solicitadas por los adaptadores y convierte DTO mediante mappers.
- **Infraestructura (`infrastructure`)**: adaptadores de entrada REST y MVC, adaptador de persistencia Mongo, documentos de almacenamiento, configuración, DTO y mappers.

Los puertos de entrada están en `domain/ports/in` (crear, consultar, listar, actualizar, eliminar y mover artículos). El puerto de salida `InventoryRepository` abstrae las operaciones de persistencia. `MongoInventoryRepository` implementa ese puerto con `MongoTemplate`. Los mappers MapStruct separan el modelo de dominio de los DTO HTTP y los documentos Mongo.

Flujo habitual de una petición:

```text
HTTP / Thymeleaf
       ↓
Controller → InventoryService → caso de uso (puerto de entrada)
                                      ↓
                              InventoryRepository
                                      ↓
                         MongoInventoryRepository
                                      ↓
                                  MongoDB
```

`GlobalExceptionHandler` convierte errores de validación y argumentos en respuestas `ProblemDetail` (`400`) y los errores no controlados en `500`.

## Modelo de dominio

### Artículo (`InventoryItem`)

- `sku`: referencia del artículo.
- `warehouse`: código del almacén.
- `onHand`: cantidad física registrada.
- `reserved`: cantidad reservada.
- `available`: propiedad calculada como `onHand - reserved` (no se persiste como campo independiente).
- `updatedAt`: instante de última actualización.
- `audit`: metadatos opcionales (`createdBy`, `createdAt`).

El constructor del dominio rechaza SKU o almacén en blanco, cantidades negativas y `reserved > onHand`. Los DTO REST validan campos no vacíos y cantidades no negativas.

### Movimiento (`Movement`)

Un movimiento contiene SKU, almacén, tipo, cantidad positiva, motivo y fecha/hora. Si `occurredAt` no se proporciona, el dominio asigna el instante actual. El registro persistido recibe un identificador `MVT-<UUID>`.

En el repositorio Mongo, las operaciones de stock se realizan con actualizaciones condicionadas: `IN` incrementa `onHand`; `OUT` descuenta unidades solo si el disponible cubre la cantidad; `RESERVATION` y `RELEASE` actualizan `onHand` y `reserved` según sus condiciones. Si las condiciones no se cumplen o el artículo no existe, la operación devuelve `false`; el movimiento solo se registra después de una actualización de stock exitosa.

## API HTTP implementada

La API REST tiene como raíz `/items`. Los endpoints que existen en `InventoryController` son:

| Método | Ruta | Comportamiento | Respuestas principales |
|---|---|---|---|
| `POST` | `/items` | Crea un artículo a partir de un `InventoryItemDTO`. | `201 Created`, `400 Bad Request` |
| `GET` | `/items/{sku}/warehouse/{warehouse}` | Consulta un artículo por SKU y almacén. | `200 OK`, `404 Not Found` |
| `PUT` | `/items/{sku}/warehouse/{warehouse}` | Actualiza las cantidades enviadas en el DTO. | `200 OK`, `404 Not Found`, `400 Bad Request` |
| `DELETE` | `/items/{sku}/warehouse/{warehouse}` | Elimina el artículo únicamente si `onHand` y `reserved` son cero. | `200 OK`, `404 Not Found` |
| `POST` | `/items/movements` | Aplica un movimiento usando SKU, almacén, tipo, cantidad y motivo en el cuerpo. | `200 OK`, `400 Bad Request` |

Ejemplo de creación:

```http
POST /items
Content-Type: application/json

{
  "sku": "SKU-1001",
  "warehouse": "MAD",
  "onHand": 25,
  "reserved": 5
}
```

Ejemplo de movimiento:

```http
POST /items/movements
Content-Type: application/json

{
  "sku": "SKU-1001",
  "warehouse": "MAD",
  "type": "OUT",
  "quantity": 2,
  "reason": "SALE"
}
```

Un `OUT` superior al stock disponible produce un resultado fallido y el controlador responde `400`. El cuerpo de éxito del endpoint de movimientos es texto plano (`Movement applied successfully`); los fallos también devuelven texto plano.

## Interfaz web

La vista `/inventory` renderiza `templates/inventory.html` con Thymeleaf y obtiene del servicio la lista completa de artículos y movimientos. Permite enviar solicitudes para crear artículos, aplicar movimientos y eliminar artículos. Los formularios utilizan `fetch` contra la API REST y recargan la página al completar correctamente la operación. Los estilos están en `static/inventory.css`.

## Persistencia y configuración

La configuración predeterminada está en `src/main/resources/application.yml`:

| Propiedad | Valor predeterminado |
|---|---|
| Nombre de aplicación | `inventory-service` |
| URI MongoDB | `mongodb://root:example@localhost:27017/?authSource=admin` |
| Base de datos | `inventory` |
| Puerto HTTP | `8080` |
| Actuator expuesto | `health`, `info` |

Spring Boot permite sobrescribir la conexión mediante `SPRING_DATA_MONGODB_URI`, la base de datos mediante `SPRING_DATA_MONGODB_DATABASE` y el puerto mediante `SERVER_PORT`.

El identificador de artículo se forma como `sku::warehouse` y actúa como `_id` Mongo. Se crean índices para almacén en `items`, y para SKU, almacén y fecha descendente en `movements`.

`ops/docker-compose.yml` levanta MongoDB 7 en el puerto `27017` con el usuario `root` y la contraseña `example`. Estas credenciales son solo valores de desarrollo; deben sustituirse por configuración segura fuera del entorno local.

## Requisitos y puesta en marcha

- JDK 17.
- MongoDB 7 local o accesible mediante URI, o Docker para ejecutar el servicio definido en Compose.
- Gradle compatible con el proyecto. El pipeline utiliza Gradle 8.10.0.

Desde la raíz del repositorio, iniciar MongoDB:

```bash
docker compose -f ops/docker-compose.yml up -d
```

Configurar un Gradle disponible en el entorno y arrancar la aplicación:

```bash
gradle bootRun
```

La API queda disponible en `http://localhost:8080`; la vista de inventario, en `http://localhost:8080/inventory`; el endpoint de salud de Actuator, en `http://localhost:8080/actuator/health`.

Para apuntar a otra instancia Mongo en Bash:

```bash
export SPRING_DATA_MONGODB_URI="mongodb://usuario:clave@host:27017/?authSource=admin"
export SPRING_DATA_MONGODB_DATABASE="inventory"
gradle bootRun
```

En PowerShell:

```powershell
$env:SPRING_DATA_MONGODB_URI = "mongodb://usuario:clave@host:27017/?authSource=admin"
$env:SPRING_DATA_MONGODB_DATABASE = "inventory"
gradle bootRun
```

## Pruebas y calidad

El proyecto usa JUnit 5, Spring Boot Test, MockMvc, Mockito y Testcontainers con MongoDB. Las pruebas cubren reglas del modelo, respuestas y validaciones REST, consultas y operaciones de persistencia, así como movimientos y restricciones de stock. Docker debe estar disponible para los tests de integración que usan Testcontainers.

Ejecutar las pruebas y generar informe JaCoCo:

```bash
gradle test jacocoTestReport
```

El informe HTML se genera en `build/reports/coverage/index.html`; también se genera XML para integración con herramientas de calidad.

## Integración continua

El archivo `.gitlab-ci.yml` define dos etapas en una imagen `gradle:8.10.0-jdk17`:

1. **build**: ejecuta `gradle -q clean classes` y conserva `build/classes` como artefacto durante una semana.
2. **test**: ejecuta `gradle -q test jacocoTestReport`, publica los resultados JUnit y conserva los informes de pruebas y cobertura durante una semana.

## Estructura del repositorio

```text
.
├── openapi/openapi.yml                 # Contrato OpenAPI declarado
├── ops/docker-compose.yml              # MongoDB para desarrollo local
├── src/main/java/com/acme/inventory
│   ├── application
│   │   ├── services                   # Fachada de aplicación
│   │   └── usecases                   # Casos de uso
│   ├── domain
│   │   ├── models                     # Entidades y reglas de dominio
│   │   ├── ports/in                   # Puertos de entrada
│   │   ├── ports/out                  # Puerto de repositorio
│   │   └── utils                      # Utilidades de dominio
│   └── infrastructure
│       ├── mappers                    # Mapeos REST y Mongo
│       ├── mongo                      # Adaptador, documentos y configuración
│       └── rest                       # Controladores y DTO
├── src/main/resources
│   ├── application.yml                # Configuración Spring
│   ├── static/                        # CSS y recursos estáticos
│   └── templates/                     # Vista Thymeleaf
└── src/test/java                     # Pruebas unitarias y de integración
```

## Contrato OpenAPI

El contrato fuente se encuentra en `openapi/openapi.yml`. Actualmente **no está alineado por completo con la API ejecutable**: declara rutas como `/items/{sku}/warehouses/{wh}`, `/items/search` y movimientos anidados por artículo, mientras el controlador implementa `/items/{sku}/warehouse/{warehouse}` y `/items/movements`. Además, búsqueda paginada se contempla en el puerto y adaptador de persistencia, pero no está expuesta en el controlador REST. Para integrar clientes, tomar como referencia los endpoints implementados arriba hasta que se sincronice el contrato OpenAPI.

## Limitaciones conocidas

- `DataInitializer` ejecuta `deleteAll()` al arrancar y luego inserta datos de demostración. Esto borra las colecciones `items` y `movements` de la base configurada en cada inicio. No se debe conectar la aplicación con datos que se quieran conservar mientras este inicializador esté activo.
- El contrato OpenAPI no refleja las rutas implementadas y no se publica una interfaz Swagger UI.
- La interfaz web consume la lista completa y no ofrece paginación; el buscador paginado solo existe en el repositorio.
- No hay autenticación ni autorización configuradas.
- Los valores de Compose y de `application.yml` son credenciales de desarrollo y no son apropiados para despliegues.
- El wrapper Gradle del repositorio apunta a una distribución local (`file:///C:/Users/migrodri/Downloads/gradle-9.2.0-bin.zip`), por lo que no es portable tal como está. En otro entorno se necesita Gradle instalado o actualizar `gradle/wrapper/gradle-wrapper.properties` a una distribución accesible.
