package com.api.entregas;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest @AutoConfigureMockMvc
class EntregasApplicationTests {
    @Autowired MockMvc mvc; @Autowired ObjectMapper mapper;
    @Test void catalogoDisponible() throws Exception {
        mvc.perform(get("/api/v1/restaurantes")).andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value("R001"));
    }
    @Test void cicloPedidoHastaAsignacion() throws Exception {
        String cuerpo="""
          {"clienteNombre":"Prueba SOA","clienteCorreo":"prueba@example.com","restauranteId":"R001","direccionEntrega":"Calle Demo 123","latitudEntrega":19.43,"longitudEntrega":-99.13,"productos":[{"productoId":"P001","cantidad":1}]}
          """;
        String respuesta=mvc.perform(post("/api/v1/pedidos").contentType(MediaType.APPLICATION_JSON).content(cuerpo)).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        JsonNode pedido=mapper.readTree(respuesta); String id=pedido.get("id").asText();
        mvc.perform(post("/api/v1/pedidos/"+id+"/pago").contentType(MediaType.APPLICATION_JSON).content("""
            {"tokenPago":"tok_demo_aprobado"}
            """))
          .andExpect(status().isOk()).andExpect(jsonPath("$.estado").value("PENDIENTE_ACEPTACION"));
        mvc.perform(post("/api/v1/pedidos/"+id+"/decision-restaurante").contentType(MediaType.APPLICATION_JSON).content("""
            {"aceptado":true,"motivo":""}
            """))
          .andExpect(status().isOk()).andExpect(jsonPath("$.estado").value("ACEPTADO"));
        mvc.perform(post("/api/v1/pedidos/"+id+"/asignacion")).andExpect(status().isOk())
          .andExpect(jsonPath("$.estado").value("REPARTIDOR_ASIGNADO")).andExpect(jsonPath("$.repartidorId").isNotEmpty());
    }
    @Test void pagoRechazadoCancelaPedido() throws Exception {
        String cuerpo="""
          {"clienteNombre":"Prueba SOA","clienteCorreo":"prueba@example.com","restauranteId":"R002","direccionEntrega":"Calle Demo 123","latitudEntrega":19.43,"longitudEntrega":-99.13,"productos":[{"productoId":"P101","cantidad":1}]}
          """;
        String respuesta=mvc.perform(post("/api/v1/pedidos").contentType(MediaType.APPLICATION_JSON).content(cuerpo)).andReturn().getResponse().getContentAsString();
        String id=mapper.readTree(respuesta).get("id").asText();
        mvc.perform(post("/api/v1/pedidos/"+id+"/pago").contentType(MediaType.APPLICATION_JSON).content("""
            {"tokenPago":"tok_demo_rechazado"}
            """))
          .andExpect(status().isOk()).andExpect(jsonPath("$.estado").value("CANCELADO")).andExpect(jsonPath("$.estadoPago").value("RECHAZADO"));
    }
}
