package com.company.ConsultasConInteraccion;

import com.company.Controladores.ControladorArticulo;
import org.xmldb.api.base.*;
import org.xmldb.api.modules.XPathQueryService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TotalComprasPorArticulo {

    /* solicitar al usuario que elija un articulo - mostrarlistado de articulos
    y mostrar los totales de ese articulo vendido,
    hay que coger el campo de cantidad.
    Mostrar todas las compras (<unidadesArticulos>)
    * */
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    ControladorArticulo ca = new ControladorArticulo();


    public void MostrarTotalComprasxArticulo(Collection col) throws IOException, XMLDBException {

        String articuloElegido = "";
        boolean existeArticulo = false;

        do {
            System.out.println("Elige un artículo (ID): ");
            ca.ListarArticulos(col);
            articuloElegido = br.readLine();
            existeArticulo = ca.Comprobacion(articuloElegido, col);
        } while (!existeArticulo);


       /* let $nombre:=/Articulos/Articulo[@id="2"]/NombreArticulo/text() return concat(
                sum(/CompraVenta/Venta/Compra/ProductoUnidades[Articulo=$nombre]/Cantidad), " ", $nombre)*/

        String consulta = "let $nombre:=/Articulos/Articulo[@id=\"" + articuloElegido + "\"]/NombreArticulo/text() return concat(\n" +
                "                sum(/CompraVenta/Venta/Compra/ProductoUnidades[Articulo=$nombre]/Cantidad), \" \", $nombre)";

        XPathQueryService servicio = (XPathQueryService) col.getService("XPathQueryService", "1.0");
        ResourceSet result = servicio.query(consulta);

        ResourceIterator i;
        i = result.getIterator();

        while (i.hasMoreResources()) {
            Resource r = i.nextResource();
            String resultado = r.getContent().toString();
            System.out.println("Se han vendido: " + resultado);
        }

        col.close();

    }
}