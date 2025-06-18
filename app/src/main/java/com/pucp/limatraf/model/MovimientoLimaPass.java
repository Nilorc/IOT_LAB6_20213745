package com.pucp.limatraf.model;

public class MovimientoLimaPass {

    private String id;
    private String idTarjeta;
    private String fecha;
    private String paraderoEntrada;
    private String paraderoSalida;
    private String tiempoViaje;

    public MovimientoLimaPass() {
    }

    public MovimientoLimaPass(String idTarjeta, String fecha, String paraderoEntrada, String paraderoSalida, String tiempoViaje) {
        this.idTarjeta = idTarjeta;
        this.fecha = fecha;
        this.paraderoEntrada = paraderoEntrada;
        this.paraderoSalida = paraderoSalida;
        this.tiempoViaje = tiempoViaje;
    }

    public MovimientoLimaPass(String id, String idTarjeta, String fecha, String paraderoEntrada, String paraderoSalida, String tiempoViaje) {
        this.id = id;
        this.idTarjeta = idTarjeta;
        this.fecha = fecha;
        this.paraderoEntrada = paraderoEntrada;
        this.paraderoSalida = paraderoSalida;
        this.tiempoViaje = tiempoViaje;
    }

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

    public String getParaderoEntrada() {
        return paraderoEntrada;
    }

    public void setParaderoEntrada(String paraderoEntrada) {
        this.paraderoEntrada = paraderoEntrada;
    }

    public String getParaderoSalida() {
        return paraderoSalida;
    }

    public void setParaderoSalida(String paraderoSalida) {
        this.paraderoSalida = paraderoSalida;
    }

    public String getTiempoViaje() {
        return tiempoViaje;
    }

    public void setTiempoViaje(String tiempoViaje) {
        this.tiempoViaje = tiempoViaje;
    }
}
