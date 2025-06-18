package com.pucp.limatraf.model;

public class MovimientoLinea1 {

    private String id;
    private String idTarjeta;
    private String fecha;
    private String estacionEntrada;
    private String estacionSalida;
    private String tiempoViaje;

    // Constructor vacío (requerido por Firestore)
    public MovimientoLinea1() {
    }

    // Constructor sin ID (para crear nuevos)
    public MovimientoLinea1(String idTarjeta, String fecha, String estacionEntrada, String estacionSalida, String tiempoViaje) {
        this.idTarjeta = idTarjeta;
        this.fecha = fecha;
        this.estacionEntrada = estacionEntrada;
        this.estacionSalida = estacionSalida;
        this.tiempoViaje = tiempoViaje;
    }

    // Constructor con ID (opcional, útil al cargar datos)
    public MovimientoLinea1(String id, String idTarjeta, String fecha, String estacionEntrada, String estacionSalida, String tiempoViaje) {
        this.id = id;
        this.idTarjeta = idTarjeta;
        this.fecha = fecha;
        this.estacionEntrada = estacionEntrada;
        this.estacionSalida = estacionSalida;
        this.tiempoViaje = tiempoViaje;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdTarjeta() {
        return idTarjeta;
    }

    public void setIdTarjeta(String idTarjeta) {
        this.idTarjeta = idTarjeta;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getEstacionEntrada() {
        return estacionEntrada;
    }

    public void setEstacionEntrada(String estacionEntrada) {
        this.estacionEntrada = estacionEntrada;
    }

    public String getEstacionSalida() {
        return estacionSalida;
    }

    public void setEstacionSalida(String estacionSalida) {
        this.estacionSalida = estacionSalida;
    }

    public String getTiempoViaje() {
        return tiempoViaje;
    }

    public void setTiempoViaje(String tiempoViaje) {
        this.tiempoViaje = tiempoViaje;
    }
}
