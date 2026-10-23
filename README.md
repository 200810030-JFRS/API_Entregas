RapiEntrega API — Reto 2 de SOA, Unidad 2
API REST de demostración para una plataforma de pedidos de comida a domicilio. Se construyó sobre la plantilla Maven/Spring Boot entregada, conservando el proyecto Java y ampliando su estructura en controladores, DTO, modelos, servicios, repositorios, eventos, excepciones e integraciones.

Alcance implementado
Catálogo de restaurantes y productos; los precios válidos los aporta el catálogo, nunca el cliente.
Creación y consulta de pedidos, desglose de conceptos, subtotal, tarifa y total.
Cobro mediante adaptador simulado de una pasarela externa; token de prueba, no número de tarjeta.
Decisión del restaurante con límite de cinco minutos y cancelación/reembolso automático si vence el plazo.
Asignación del repartidor disponible más cercano al restaurante mediante distancia geográfica (Haversine).
Actualización de ubicación mediante REST y seguimiento de eventos en tiempo real con Server-Sent Events (SSE).
Confirmación de entrega, calificación de 1 a 5 y solicitud de factura.
Adaptador de demostración para el sistema de facturación legado de 2012, sin sustituirlo ni conectarse a él.
Respuestas de error uniformes y validación de DTO de entrada.
Contrato OpenAPI/Swagger UI y pruebas de integración del flujo principal.
Tecnologías
Java 17
Spring Boot 3.5.6
Spring Web (REST), Jakarta Bean Validation
Spring Events + @Async para publicación/suscripción local; SSE para transmitir eventos a clientes
springdoc-openapi 2.8.13
Maven Wrapper y JUnit 5 / MockMvc
Requisitos e instalación
Instalar un JDK 17 o posterior y tener conexión a Internet la primera vez para que Maven descargue dependencias.

Descomprimir RapiEntrega_SOA.zip y abrir la carpeta entregas/ en VS Code o IntelliJ.

En Windows PowerShell, desde la carpeta del proyecto:

.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
En macOS/Linux:

chmod +x ./mvnw
./mvnw test
./mvnw spring-boot:run
Abrir http://localhost:8080/swagger-ui.html o http://localhost:8080/swagger-ui/index.html para explorar los contratos.

Ver los ejemplos completos y el orden de la prueba manual en docs/requests.http.

Variables de entorno
No se necesitan claves ni credenciales para esta práctica.

Variable	Valor predeterminado	Uso
PORT	8080	Puerto HTTP de Spring Boot
Propiedad rapientrega.timeout.scan-ms	10000 ms	Frecuencia de revisión de pedidos que llevan más de cinco minutos esperando al restaurante
Para cambiar el intervalo de revisión, ejecutar con --rapientrega.timeout.scan-ms=10000; para cambiar el puerto, PORT=8081 en macOS/Linux o $env:PORT=8081 en PowerShell.

Endpoints principales
Base URL: http://localhost:8080/api/v1.

Método	Ruta	Propósito
GET	/restaurantes	Listar restaurantes abiertos
GET	/restaurantes/{id}	Consultar restaurante y menú
GET	/restaurantes/{restauranteId}/productos	Productos del restaurante
POST	/pedidos	Crear pedido
GET	/pedidos	Listar pedidos de demostración
GET	/pedidos/{pedidoId}	Consultar estado, total y asignación
GET	/pedidos/{pedidoId}/detalles	Consultar líneas del pedido
POST	/pedidos/{pedidoId}/pago	Autorizar/cobrar mediante token de prueba
POST	/pedidos/{pedidoId}/decision-restaurante	Aceptar o rechazar el pedido
POST	/pedidos/{pedidoId}/asignacion	Asignar al repartidor cercano disponible
POST	/pedidos/{pedidoId}/ubicacion	Actualizar ubicación y cambiar a EN_CAMINO
GET	/pedidos/{pedidoId}/eventos	Suscribirse al flujo SSE en tiempo real
POST	/pedidos/{pedidoId}/entrega	Confirmar entrega
POST	/pedidos/{pedidoId}/calificacion	Calificar entrega de 1 a 5
GET	/clientes/pedidos?correo=...	Consultar pedidos por correo (demostración, sin autenticación)
GET	/repartidores	Ver estado de repartidores de prueba
POST	/facturas	Solicitar factura a través del adaptador legado simulado
Ejemplo rápido
GET http://localhost:8080/api/v1/restaurantes
POST http://localhost:8080/api/v1/pedidos
Content-Type: application/json

{
  "clienteNombre": "Alex Demo",
  "clienteCorreo": "alex@example.com",
  "restauranteId": "R001",
  "direccionEntrega": "Calle Demo 123",
  "latitudEntrega": 19.43,
  "longitudEntrega": -99.13,
  "productos": [{ "productoId": "P001", "cantidad": 1 }]
}
La respuesta devuelve el identificador PED-.... Usarlo en las siguientes peticiones. Para pagar, enviar {"tokenPago":"tok_demo_aprobado"} a /api/v1/pedidos/{id}/pago. El importe se calcula a partir del catálogo, incluye una tarifa de envío de demostración de $29.00 y se representa en MXN en los ejemplos.

Token de prueba
tok_demo_aprobado: simula aprobación.
tok_demo_rechazado: simula rechazo, cancela el pedido y no genera cobro aprobado.
Cualquier otro token no vacío simula aprobación.
No introducir números de tarjeta, CVV ni datos reales. La clase PasarelaPagosSimulada no se conecta a una red de pagos ni cobra dinero real.

Regla de cinco minutos y estados
PENDIENTE_PAGO → PENDIENTE_ACEPTACION → ACEPTADO → REPARTIDOR_ASIGNADO → EN_CAMINO → ENTREGADO.

Un pago rechazado cancela el pedido. Si el restaurante rechaza o no responde en cinco minutos desde la aprobación del pago, se cancela y el adaptador de pago simula el reembolso. El escáner de expiración se ejecuta cada 10 segundos por defecto; la cancelación no espera a una llamada de cliente. CANCELADO y ENTREGADO son estados finales en esta versión.

Estructura del código
src/main/java/com/api/entregas/
  controllers/       REST controllers por capacidad
  dto/               contratos de entrada y errores
  events/             evento de pedido y publicador
  exceptions/         excepciones de negocio y manejador HTTP
  integration/
    pagos/            puerto y pasarela simulada
    facturacion/      puerto/adaptador del sistema legado
  modelos/            entidades/modelos y estados
  repositories/       repositorios en memoria de la demostración
  services/           catálogo, pedidos, pagos, asignación, seguimiento, factura y calificación
src/test/java/         pruebas con Spring Boot y MockMvc
docs/                  diseño SOA, diagramas y peticiones HTTP
Limitaciones explícitas y siguiente paso para producción
Los repositorios almacenan datos en memoria y se vacían al reiniciar; la pasarela y el conector de facturación son simuladores; el evento pub/sub es local al proceso y SSE necesita que el cliente mantenga una conexión abierta. No hay autenticación, autorización, persistencia durable, catálogo dinámico ni proveedor real de mapas/pagos. Antes de producción: base de datos transaccional, autenticación/autorización, idempotency keys, cifrado/gestión de secretos, broker durable (RabbitMQ/Kafka), observabilidad, manejo de reintentos y conciliación de pagos, pruebas de carga y conector real aprobado por el proveedor legado.

Documentos de entrega
docs/documento_diseno_SOA.md: versión Markdown del razonamiento de arquitectura.
RapiEntrega_Diseno_SOA.docx y RapiEntrega_Diseno_SOA.pdf: especificación de 10–15 páginas preparada para entrega.
docs/diagrama_dominios.png / .svg: dominios y relaciones.
docs/secuencia_pedido.png / .svg: proceso principal.
