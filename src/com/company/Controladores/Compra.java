package com.company.Controladores;

public class Compra {
    //Atributos
    private String nombreArticulo;
    private int cantidad;

    //Constructores

    public Compra(String nombreArticulo, int cantidad) {

        this.nombreArticulo = nombreArticulo;
        this.cantidad = cantidad;
    }

    public Compra() {
    }

    //Getter y setters


    public String getNombreArticulo() {
        return nombreArticulo;
    }

    public void setNombreArticulo(String nombreArticulo) {
        this.nombreArticulo = nombreArticulo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}
