package com.api.entregas.modelos;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class PedidoModel {
    private String id;
    private String clienteNombre;
    private String clienteCorreo;
    private String restauranteId;
    private String restauranteNombre;
    private String direccionEntrega;
    private double latitudEntrega;
    private double longitudEntrega;
    private java.util.List<LineaPedidoModel> detalles;
    private java.math.BigDecimal subtotal;
    private java.math.BigDecimal tarifaEnvio;
    private java.math.BigDecimal total;
    private EstadoPedido estado;
    private EstadoPago estadoPago;
    private java.time.Instant creadoEn;
    private java.time.Instant pagadoEn;
    private String referenciaPago;
    private String motivoCancelacion;
    private String repartidorId;
    private String repartidorNombre;
    private Double ultimaLatitud;
    private Double ultimaLongitud;
    private java.time.Instant ultimaActualizacionUbicacion;
    private Integer calificacion;
    private String comentarioCalificacion;
    private String facturaId;

    public PedidoModel() {}
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }
    public String getClienteCorreo() { return clienteCorreo; }
    public void setClienteCorreo(String clienteCorreo) { this.clienteCorreo = clienteCorreo; }
    public String getRestauranteId() { return restauranteId; }
    public void setRestauranteId(String restauranteId) { this.restauranteId = restauranteId; }
    public String getRestauranteNombre() { return restauranteNombre; }
    public void setRestauranteNombre(String restauranteNombre) { this.restauranteNombre = restauranteNombre; }
    public String getDireccionEntrega() { return direccionEntrega; }
    public void setDireccionEntrega(String direccionEntrega) { this.direccionEntrega = direccionEntrega; }
    public double getLatitudEntrega() { return latitudEntrega; }
    public void setLatitudEntrega(double latitudEntrega) { this.latitudEntrega = latitudEntrega; }
    public double getLongitudEntrega() { return longitudEntrega; }
    public void setLongitudEntrega(double longitudEntrega) { this.longitudEntrega = longitudEntrega; }
    public java.util.List<LineaPedidoModel> getDetalles() { return detalles; }
    public void setDetalles(java.util.List<LineaPedidoModel> detalles) { this.detalles = detalles; }
    public java.math.BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(java.math.BigDecimal subtotal) { this.subtotal = subtotal; }
    public java.math.BigDecimal getTarifaEnvio() { return tarifaEnvio; }
    public void setTarifaEnvio(java.math.BigDecimal tarifaEnvio) { this.tarifaEnvio = tarifaEnvio; }
    public java.math.BigDecimal getTotal() { return total; }
    public void setTotal(java.math.BigDecimal total) { this.total = total; }
    public EstadoPedido getEstado() { return estado; }
    public void setEstado(EstadoPedido estado) { this.estado = estado; }
    public EstadoPago getEstadoPago() { return estadoPago; }
    public void setEstadoPago(EstadoPago estadoPago) { this.estadoPago = estadoPago; }
    public java.time.Instant getCreadoEn() { return creadoEn; }
    public void setCreadoEn(java.time.Instant creadoEn) { this.creadoEn = creadoEn; }
    public java.time.Instant getPagadoEn() { return pagadoEn; }
    public void setPagadoEn(java.time.Instant pagadoEn) { this.pagadoEn = pagadoEn; }
    public String getReferenciaPago() { return referenciaPago; }
    public void setReferenciaPago(String referenciaPago) { this.referenciaPago = referenciaPago; }
    public String getMotivoCancelacion() { return motivoCancelacion; }
    public void setMotivoCancelacion(String motivoCancelacion) { this.motivoCancelacion = motivoCancelacion; }
    public String getRepartidorId() { return repartidorId; }
    public void setRepartidorId(String repartidorId) { this.repartidorId = repartidorId; }
    public String getRepartidorNombre() { return repartidorNombre; }
    public void setRepartidorNombre(String repartidorNombre) { this.repartidorNombre = repartidorNombre; }
    public Double getUltimaLatitud() { return ultimaLatitud; }
    public void setUltimaLatitud(Double ultimaLatitud) { this.ultimaLatitud = ultimaLatitud; }
    public Double getUltimaLongitud() { return ultimaLongitud; }
    public void setUltimaLongitud(Double ultimaLongitud) { this.ultimaLongitud = ultimaLongitud; }
    public java.time.Instant getUltimaActualizacionUbicacion() { return ultimaActualizacionUbicacion; }
    public void setUltimaActualizacionUbicacion(java.time.Instant ultimaActualizacionUbicacion) { this.ultimaActualizacionUbicacion = ultimaActualizacionUbicacion; }
    public Integer getCalificacion() { return calificacion; }
    public void setCalificacion(Integer calificacion) { this.calificacion = calificacion; }
    public String getComentarioCalificacion() { return comentarioCalificacion; }
    public void setComentarioCalificacion(String comentarioCalificacion) { this.comentarioCalificacion = comentarioCalificacion; }
    public String getFacturaId() { return facturaId; }
    public void setFacturaId(String facturaId) { this.facturaId = facturaId; }
}