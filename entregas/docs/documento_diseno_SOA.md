# RapiEntrega — documento de diseño SOA

**Reto 2 · Arquitectura Orientada a Servicios · Unidad 2**  
**Caso:** Plataforma de entregas a domicilio  
**Integrantes:** completar en la portada del DOCX/PDF.

## 1. Contexto y alcance

RapiEntrega conecta clientes, restaurantes y repartidores para procesar pedidos de comida. Los puntos no negociables del caso son la pasarela externa de tarjeta, la respuesta del restaurante en cinco minutos y el sistema heredado de facturación de 2012 que no puede reemplazarse. La API implementa catálogo, creación y consulta de pedidos, pago simulado, aceptación/rechazo, cancelación con reembolso simulado, asignación por cercanía, seguimiento en tiempo real, confirmación de entrega, calificación y un adaptador de facturación simulado.

La solución corre como aplicación modular Spring Boot; los dominios son límites lógicos, no microservicios ya desplegados. Los repositorios almacenan datos en memoria. No se recibe ni procesa número de tarjeta/CVV. Las integraciones reales no se pueden afirmar porque no se entregaron credenciales ni contratos del proveedor.

## 2. Modelado de servicios: cinco pasos

El PDF del reto pide cinco pasos sin enumerar sus nombres. Se adopta el proceso inicial de modelado REST de Thomas Erl [2], que es coherente con esta API:

1. **Descomponer el proceso en acciones granulares.** Buscar restaurante; consultar productos; crear pedido; cobrar; esperar decisión del restaurante; aceptar/rechazar; asignar repartidor; publicar ubicación; confirmar entrega; calificar; solicitar factura.
2. **Filtrar acciones inadecuadas.** Detalles de UI como “dar clic” no son capacidades de negocio. El temporizador de cinco minutos es una regla/política del proceso y no una llamada del cliente.
3. **Definir candidatos de servicios de entidad.** Restaurante, Producto, Pedido, DetallePedido, Repartidor, Calificación y Factura.
4. **Identificar la lógica específica del proceso.** Calcular el total, coordinar pago/aceptación, aplicar expiración, pedir reembolso y disparar asignación.
5. **Identificar recursos REST.** Determinar URIs, métodos, esquemas JSON, códigos HTTP y reglas de transición.

## 3. Matriz actividad-servicio

| Actividad | Operación candidata | Servicio propietario | Dominio |
|---|---|---|---|
| Buscar restaurantes | `listarRestaurantes()` | RestauranteService | Catálogo |
| Consultar menú | `listarProductos(restauranteId)` | RestauranteService | Catálogo |
| Crear pedido y total | `crearPedido(request)` | PedidoService | Pedidos |
| Consultar detalle | `consultarPedido(id)`, `listarDetalles(id)` | PedidoService / DetallePedidoController | Pedidos |
| Autorizar pago | `procesarPago(pedidoId, token)` | PagoService, coordinado por PedidoService | Pagos |
| Aceptar/rechazar | `decidirPedidoRestaurante(id, decisión)` | PedidoService | Pedidos |
| Cancelar y reembolsar por plazo | `expirarPedidosPendientes()`, `reembolsarPago()` | PedidoService / PagoService | Pedidos / Pagos |
| Asignar repartidor | `asignarRepartidor(id)` | AsignacionRepartidorService | Logística |
| Publicar ubicación | `actualizarUbicacion(id, repartidorId, lat, lon)` | AsignacionRepartidorService | Logística |
| Difundir seguimiento | `publicarEventoPedido(evento)`, `suscribirEventos(id)` | EventoPedidoService / SeguimientoService | Seguimiento |
| Confirmar entrega | `confirmarEntrega(id, repartidorId)` | AsignacionRepartidorService | Logística |
| Calificar | `calificarPedido(id, puntuacion, comentario)` | CalificacionService | Reputación |
| Solicitar factura | `solicitarFactura(pedidoId, datosFiscales)` | FacturaService + Legacy Adapter | Facturación |

## 4. Dominios

1. **Catálogo y restaurantes:** restaurantes abiertos, menú, precios y disponibilidad; no cobra ni asigna repartidor.
2. **Pedidos/orquestación:** identidad de pedido, líneas, total calculado, estado y deadline de aceptación; coordina, no implementa el protocolo de pagos ni fiscal.
3. **Pagos:** referencia y resultado de cobro/reembolso por un puerto de proveedor.
4. **Logística y reparto:** disponibilidad, asignación cercana, coordenadas y confirmación de entrega.
5. **Seguimiento/eventos:** distribuye eventos de estado y ubicación; no es la fuente principal del estado actual.
6. **Reputación/clientes:** consulta demostrativa por correo y calificación única por pedido; no reemplaza autenticación.
7. **Facturación:** solicita factura y guarda referencia de respuesta del sistema fiscal a través de un wrapper.

Consultar `diagrama_dominios.png`/`.svg` y `secuencia_pedido.png`/`.svg` en esta carpeta para la vista gráfica de los límites y del proceso.

## 5. Patrones seleccionados

| Patrón del catálogo de Erl | Aplicación concreta |
|---|---|
| **Process Abstraction** | `PedidoService` oculta al cliente la coordinación de pago, plazo, aceptación y cancelación. |
| **Entity Abstraction** | Los recursos REST exponen `Pedido`, `Restaurante`, `Repartidor`, `Factura` y `Calificación` con identidades/ciclos de vida distintos. |
| **Service Façade** | `PagoService` y `FacturaService` ocultan detalles de sus adaptadores. |
| **Legacy Wrapper** | `SistemaFacturacionLegadoPort` y `SistemaFacturacionLegadoAdapter` aíslan el sistema heredado que no se debe reemplazar. |
| **Event-Driven Messaging** | `EventoPedidoService` publica `PedidoActualizadoEvent`; `SeguimientoService` lo recibe con `@Async` y lo retransmite por SSE. |
| **Compensating Service Transaction** | Si el restaurante rechaza o no responde en cinco minutos, el proceso cancela el pedido y solicita reembolso. No es una transacción ACID. |
| **Asynchronous Queuing** | Evolución propuesta: reemplazar o complementar el bus local con RabbitMQ/Kafka para retención, reintentos, consumidores distribuidos y dead-letter. No se afirma que exista una cola durable en esta entrega. |

Se usa la terminología del catálogo de Erl [3]; la presentación de clase no fue adjuntada, por lo cual conviene verificar que los nombres coincidan con la nomenclatura usada por el docente.

## 6. Comunicación y mensajes

| Interacción | Estilo y patrón | Motivo |
|---|---|---|
| Cliente → Catálogo | Síncrono, REST GET request-response | El usuario necesita el menú antes de continuar. |
| Cliente → Pedido | Síncrono, REST POST/GET | Devuelve id, totales y estado actual. |
| Pedido → Pasarela | Síncrono en prototipo, puerto/adaptador | El proceso necesita respuesta de pago antes de comenzar el plazo de aceptación. |
| Restaurante → Pedido | Síncrono, comando de decisión | Aceptación explícita o rechazo, limitado por cinco minutos. |
| Pedido/Logística → Eventos | Pub/sub local asíncrono | Permite múltiples consumidores sin llamada directa desde el productor. |
| Seguimiento → App | SSE unidireccional | Empuja cambios de estado/ubicación sin polling repetitivo. |
| FacturaService → legado | Request-response aislado por Legacy Wrapper | Preserva el sistema fiscal y permite reemplazar sólo el conector. |

En esta versión se usa Spring `ApplicationEventPublisher` y un listener `@Async`: demuestra pub/sub local, pero no retiene mensajes ante reinicios. Para operar varias instancias, el diseño evoluciona a un broker durable y eventos versionados. La pasarela y el legado quedan simulados.

## 7. Especificaciones de cuatro capacidades

### 7.1 `crearPedido`
- **Contrato:** `POST /api/v1/pedidos`; recibe `CrearPedidoRequest`: clienteNombre `String`, clienteCorreo `String/email`, restauranteId `String`, direccionEntrega `String`, latitud/longitud `Double` con límites geográficos y productos `Array<{productoId:String,cantidad:int}>` con al menos un elemento y cantidad ≥1.
- **Salida:** 201 Created y `PedidoModel` en `PENDIENTE_PAGO`, estado de pago `PENDIENTE`, líneas con precios desde el catálogo, subtotal, tarifa demo $29.00 y total calculado en el servidor.
- **Reglas:** restaurante abierto; producto disponible y perteneciente al restaurante; nunca confiar en importes enviados por el cliente; emitir `PEDIDO_CREADO`.
- **Errores:** 400 por validación/JSON; 404 por restaurante o producto no hallado; 409 por restaurante cerrado/producto no disponible; 500 inesperado.
- **Calidad propuesta:** p95 <500 ms en operación interna, JSON versionado y correlación por id de pedido; objetivos no medidos.

### 7.2 `confirmarPago`
- **Contrato:** `POST /api/v1/pedidos/{pedidoId}/pago`, recibe `{tokenPago:String}`; el importe se toma de `PedidoModel`, nunca del cliente.
- **Salida:** 200 con `APROBADO` y `PENDIENTE_ACEPTACION`, referencia y timestamp; o pedido `CANCELADO` con pago `RECHAZADO` si la pasarela declina.
- **Reglas:** sólo opera en `PENDIENTE_PAGO`; no almacenar PAN/CVV; llamada tras un puerto `PasarelaPagos`; la ventana de cinco minutos inicia al aprobarse el pago.
- **Errores:** 400 por token/JSON inválido; 404 por pedido inexistente; 409 por estado inválido; 5xx para fallos técnicos. Un rechazo conocido se refleja como estado de negocio.
- **Calidad propuesta:** idempotencia, timeout, tokenización y conciliación de pagos en producción.

### 7.3 `decidirPedidoRestaurante`
- **Contrato:** `POST /api/v1/pedidos/{pedidoId}/decision-restaurante`, recibe `{aceptado:Boolean,motivo:String|null}`; `pagadoEn` es marca interna y deadline = +5 min.
- **Salida:** 200 y `ACEPTADO` si se acepta a tiempo; si se rechaza o expira, `CANCELADO`, motivo y `REEMBOLSADO` si la compensación tuvo éxito.
- **Reglas:** sólo `PENDIENTE_ACEPTACION`; scheduler con revisión cada 10 segundos; transición protegida por pedido; el cliente no decide en nombre del restaurante.
- **Errores:** 400 por entrada inválida; 404 pedido ausente; 409 estado incorrecto o compensación que requiere intervención. Cancelación por vencimiento no depende de polling del cliente.
- **Calidad propuesta:** transiciones atómicas distribuidas, auditoría y alarmas de reembolso fallido.

### 7.4 `asignarRepartidor`
- **Contrato:** `POST /api/v1/pedidos/{pedidoId}/asignacion`; el pedido debe estar `ACEPTADO`; usa la ubicación del restaurante y candidatos `{id,latitud,longitud,disponible}`.
- **Salida:** 200, `REPARTIDOR_ASIGNADO`, id/nombre de repartidor y evento de dominio.
- **Reglas:** usa distancia Haversine al punto de recogida; sólo candidatos disponibles; si no existe ninguno, conserva el pedido aceptado para reintento; ubicación posterior lleva a `EN_CAMINO`; la entrega libera al repartidor.
- **Errores:** 404 por recurso ausente; 409 por estado inválido, falta de candidatos o discrepancia de repartidor; 500 inesperado.
- **Calidad propuesta:** selección p95 <300 ms para 1,000 candidatos en memoria; en producción índice geoespacial y reserva atómica.

## 8. API, calidad y pruebas

La base URL es `http://localhost:8080/api/v1`. Endpoints adicionales: catálogo `GET /restaurantes`; productos `GET /restaurantes/{id}/productos`; detalle `GET /pedidos/{id}/detalles`; tracking `GET /pedidos/{id}/eventos` (SSE); ubicación `POST /pedidos/{id}/ubicacion`; entrega `POST /pedidos/{id}/entrega`; calificación `POST /pedidos/{id}/calificacion`; factura `POST /facturas`; repartidores `GET /repartidores`. Swagger UI está disponible en `/swagger-ui.html`.

Se incluyen pruebas MockMvc para catálogo, flujo feliz hasta asignación y pago rechazado. El archivo `requests.http` contiene las peticiones encadenadas para probar aceptación/rechazo, SSE, ubicación, entrega, calificación y factura. Las metas de latencia/disponibilidad son objetivos de diseño, no resultados medidos.

Antes de producción: base de datos persistente; autenticación/autorización; idempotency keys; broker durable; indexación geográfica; timeouts/reintentos; observabilidad; cifrado/secrets; pruebas de carga; connector real de pagos y de factura; conciliación financiera.

## 9. Defensa oral de cinco minutos

Idea central: SOA separa las capacidades de catálogo, pedido, pago, logística, seguimiento, reputación y facturación. `PedidoService` coordina, los adaptadores encapsulan dependencias externas, eventos pub/sub informan de cambios y SSE los entrega al cliente. La regla de cinco minutos se realiza con una transición por estado y compensación de reembolso. El prototipo conserva límites y contratos, pero reconoce sus limitaciones: repositorios en memoria, proveedor de pago simulado, legado simulado, ausencia de autenticación y bus local no durable. Para producción serían necesarios almacenamiento persistente, idempotencia, broker, seguridad y observabilidad.

### Referencias

1. *Arquitectura Orientada a Servicios · Retos · Unidad 2: Diseño de servicios*, PDF proporcionado para la actividad, Reto 2, pp. 10–17.
2. Thomas Erl, *Service-Oriented Architecture: Analysis & Design for Services and Microservices*, 2.ª ed., capítulo 7, “REST Service Modeling Process”. https://www.thomaserl.com/book/service-oriented-architecture-analysis-design-for-services-and-microservices-second-edition-2/table-of-contents/index.html
3. Thomas Erl (ed.), *SOA Design Patterns*. Prentice Hall, 2009. https://www.thomaserl.com/book/soa-design-patterns/table-of-contents/index.html
