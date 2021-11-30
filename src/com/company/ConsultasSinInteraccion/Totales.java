package com.company.ConsultasSinInteraccion;

import java.io.*;

public class Totales {

    //Mostrar los totales de todas las compras por clientes.
    // Solicitar al usuario que indique el cliente.

    public void Totales() throws IOException {
        float venta = 0;
        DataInputStream fin = new DataInputStream(new FileInputStream("VentasTotales.dat"));
        System.out.println("TOTAL VENTAS");
        float suma = 0;
        try {
            while (true) {
                venta = fin.readFloat();
                //System.out.println(venta);
                suma = suma + venta;
            }
        } catch (EOFException ex) {
            System.out.println("Fin de lectura.");
        }
        System.out.println("El total de las ventas es " + suma);
        fin.close();

    }
}
