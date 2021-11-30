package com.company.ConsultasConInteraccion;

import com.company.Controladores.ControladorCliente;
import org.xmldb.api.base.*;
import org.xmldb.api.modules.XPathQueryService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/*Mostrar todos los productos que ha comprado un cliente.*/

public class ProductosClientes {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    ControladorCliente cc = new ControladorCliente();

    public void productosXClientes(Collection col) throws IOException, XMLDBException {

        //Listar los clientes,

        cc.ListarClientes(col);

        // Elegir un cliente

        boolean existe = false;
        String clienteElegido = "";

        do {
            System.out.println("Escribe el ID del cliente: ");
            clienteElegido = br.readLine();
            existe = cc.Comprobacion(clienteElegido, col);
        } while (!existe);

        // consulta a la bd

        //
        //let $cliente:=/Clientes/Cliente[@id="4"]/NombreCliente/text()
        // return /CompraVenta/Venta[Cliente=$cliente]/Compra/ProductoUnidades/concat(Articulo/text(), " - " , Cantidad/text())

        String consulta = "let $cliente:=/Clientes/Cliente[@id=\"" + clienteElegido + "\"]/NombreCliente/text() return /CompraVenta/Venta[Cliente=$cliente]/Compra/ProductoUnidades/concat(Cantidad/text(),  \"  \" , Articulo/text())";

        XPathQueryService servicio = (XPathQueryService) col.getService("XPathQueryService", "1.0");
        ResourceSet result = servicio.query(consulta);

        ResourceIterator i;
        i = result.getIterator();

        while (i.hasMoreResources()) {
            Resource r = i.nextResource();
            String resultado = r.getContent().toString();
            System.out.println("Ha comprado " + resultado);
        }

        col.close();
    }
}
