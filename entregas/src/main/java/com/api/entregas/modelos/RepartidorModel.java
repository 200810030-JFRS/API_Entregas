package com.api.entregas.modelos;

public class RepartidorModel {
    private final String id;
    private final String nombre;
    private volatile double latitud;
    private volatile double longitud;
    private volatile boolean disponible;

    public RepartidorModel(String id, String nombre, double latitud, double longitud, boolean disponible) {
        this.id=id; this.nombre=nombre; this.latitud=latitud; this.longitud=longitud; this.disponible=disponible;
    }
    public String getId(){return id;} public String getNombre(){return nombre;}
    public double getLatitud(){return latitud;} public void setLatitud(double latitud){this.latitud=latitud;}
    public double getLongitud(){return longitud;} public void setLongitud(double longitud){this.longitud=longitud;}
    public boolean isDisponible(){return disponible;} public void setDisponible(boolean disponible){this.disponible=disponible;}
}
