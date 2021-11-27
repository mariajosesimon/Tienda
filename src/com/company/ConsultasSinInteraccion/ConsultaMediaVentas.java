package com.company.ConsultasSinInteraccion;

import java.io.*;

public class ConsultaMediaVentas {

    // Calcular la media de todas las ventas
    //Sin interaccion del usuario

    /*Utilizo el fichero */

    public ConsultaMediaVentas() throws IOException {
        double suma = 0;
        DataInputStream fin = new DataInputStream(new FileInputStream("Totales.dat"));

        int i = 0;
        try {
            while (true) {
                suma += fin.readDouble();
                i++;
            }
        } catch (EOFException e) {
            System.out.println("Fin de lectura");
        }

        fin.close();
        System.out.println("La media es: " + suma / i);


    }
}
