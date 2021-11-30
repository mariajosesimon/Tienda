package com.company.ConsultasSinInteraccion;


import org.xmldb.api.base.*;
import org.xmldb.api.modules.XPathQueryService;

import java.io.*;

public class ConsultaVentasPorVendedor {

    // Calcular la suma de todas las ventas por vendedores
    //Sin interaccion del usuario

    public void Ventas(Collection col) throws XMLDBException, IOException {

        String consulta = "for $ven in /Vendedores/Vendedor let $nombre := $ven/NombreVendedor/text() return  concat(sum(/CompraVenta/Venta[Vendedor=$nombre]/Compra/ProductoUnidades/Cantidad),  \" articulos ha vendido \" , $nombre)";

        XPathQueryService servicio = (XPathQueryService) col.getService("XPathQueryService", "1.0");
        ResourceSet result = servicio.query(consulta);

        //Creación del archivo .dat con las ventas totales por vendedor.
        DataOutputStream fon = new DataOutputStream(new FileOutputStream("Ventas.dat"));

        ResourceIterator i;
        i = result.getIterator();

        while (i.hasMoreResources()) {
            Resource r = i.nextResource();
            String resultado = r.getContent().toString();
            fon.writeUTF(resultado);
        }
        fon.close();

        //Mostrar por pantalla desde el archivo .dat

        DataInputStream fin = new DataInputStream(new FileInputStream("Ventas.dat"));
        try {
            System.out.println("TOTAL VENTAS POR VENDEDOR");
            while (true) {
                String venta = fin.readUTF();
                System.out.println(venta);
            }

        } catch (EOFException e) {
            System.out.println("fin de lectura");
        }
        fin.close();

        col.close();

    }

}
