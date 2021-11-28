package com.company;

import com.company.ConsultasConInteraccion.TotalComprasPorArticulo;
import com.company.ConsultasSinInteraccion.ConsultaArticulosMasVendidos;
import com.company.ConsultasSinInteraccion.ConsultaVentasPorVendedor;
import com.company.Controladores.*;
import org.xml.sax.SAXException;
import org.xmldb.api.base.*;
import org.xmldb.api.modules.XPathQueryService;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        final Collection col = Conexion.conectar();
        ControladorCliente cCliente = new ControladorCliente();
        ControladorVendedor cVendedor = new ControladorVendedor();
        ControladorArticulo cArticulo = new ControladorArticulo();
        CompraVenta cv = new CompraVenta();
        int submenu = 0;
        int op = 0;
        do {
            do {
                try {
                    mostrarMenu();
                    op = Integer.parseInt(br.readLine());

                    switch (op) {
                        case 1:
                            menuClientes();
                            submenu = Integer.parseInt(br.readLine());
                            switch (submenu) {
                                case 1:
                                    cCliente.CrearCliente(col);
                                    break;
                                case 2:
                                    cCliente.ModificarCliente(col);
                                    break;
                                case 3:
                                    cCliente.EliminarCliente(col);
                                    break;
                                case 4:
                                    cCliente.ListarClientes(col);
                                    break;

                            }
                            break;
                        case 2:
                            menusVendedores();
                            submenu = Integer.parseInt(br.readLine());
                            switch (submenu) {
                                case 1:
                                    cVendedor.CrearVendedor(col);
                                    break;
                                case 2:
                                    cVendedor.ModificarVendedor(col);
                                    break;
                                case 3:
                                    cVendedor.EliminarVendedor(col);
                                    break;
                                case 4:
                                    cVendedor.ListarVendedores(col);
                                    break;
                            }
                            break;
                        case 3:
                            menuArticulos();
                            submenu = Integer.parseInt(br.readLine());
                            switch (submenu) {
                                case 1:
                                    cArticulo.CrearArticulo(col);
                                    break;
                                case 2:
                                    cArticulo.ModificarArticulo(col);
                                    break;
                                case 3:
                                    cArticulo.EliminarArticulo(col);
                                    break;
                                case 4:
                                    cArticulo.ListarArticulos(col);
                                    break;
                            }
                            break;
                        case 4:
                            // Hay que revisar si hay clientes, vendedores y articulos sino, no se puede entrar el la funcion
                            XPathQueryService servicio = (XPathQueryService) col.getService("XPathQueryService", "1.0");
                            String existenClientes = "";
                            String existenVendedores = "";
                            String existenArticulos = "";
                            try {
                                //Consulta para consultar si existen clientes
                                ResourceSet resultClientes = servicio.query("/Clientes/count(Cliente)");

                                ResourceIterator i;
                                i = resultClientes.getIterator();
                                while(i.hasMoreResources()) {
                                    Resource r = i.nextResource();
                                    existenClientes = (String) r.getContent();
                                }
                                ResourceSet resultVendedores = servicio.query("/Vendedores/count(Vendedor)");
                                ResourceIterator j;
                                j = resultVendedores.getIterator();
                                while(j.hasMoreResources()) {
                                    Resource t = j.nextResource();
                                    existenVendedores = (String) t.getContent();
                                }
                                ResourceSet resultArticulos = servicio.query("/Articulos/count(Articulo)");
                                ResourceIterator s;
                                s = resultArticulos.getIterator();
                                while(!s.hasMoreResources()) {
                                    Resource u = s.nextResource();
                                    existenArticulos = (String) u.getContent();
                                }
                            } catch (Exception e) {
                                System.out.println("Error al consultar.");
                            }
                            boolean puedeComprar = true;
                            if (existenArticulos.equals("0")) {
                                System.out.println("No existen articulos. No se puede realizar una compraventa");
                                puedeComprar = false;
                            }
                            if (existenClientes.equals("0")) {
                                System.out.println("No existen clientes. No se puede realizar una compraventa");

                                puedeComprar = false;
                            }
                            if (existenVendedores.equals("0")) {
                                System.out.println("No existen vendedores. No se puede realizar una compraventa");

                                puedeComprar = false;
                            }
                            if (puedeComprar) {
                                //Meter menu de compra venta
                                menuCompraVenta();
                                submenu = Integer.parseInt(br.readLine());
                                switch (submenu) {
                                    case 1:
                                        cv.CrearCompraVenta(col);
                                        break;
                                    case 2:
                                        cv.ModificarCompraVenta(col);
                                        break;
                                    case 3:
                                        cv.EliminarCompraVenta(col);
                                        break;
                                    case 4:
                                        cv.ListarCompraVenta(col);
                                        break;
                                }


                            }
                            break;
                        case 5:
                            TotalComprasPorArticulo tcXa = new TotalComprasPorArticulo();
                            tcXa.MostrarTotalComprasxArticulo(col);
                            break;
                        case 6:
                            ConsultaVentasPorVendedor cVV = new ConsultaVentasPorVendedor();
                            cVV.Ventas(col);
                            break;
                        case 7:
                            ConsultaArticulosMasVendidos consultaArticulosMasVendidos = new ConsultaArticulosMasVendidos();
                            consultaArticulosMasVendidos.ArticuloMasVendido(col);
                            break;

                    }


                } catch (XMLDBException e) {
                    e.printStackTrace();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (TransformerException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                }


            } while (!(op > 0 && op < 18));

    } while(op !=17);

        System.out.println("FIN");
}


    private static void mostrarMenu() {

        System.out.println("----------------MENU----------------------");
        System.out.println("1 - Clientes.");
        System.out.println("2 - Vendedores.");
        System.out.println("3 - Articulos.");
        System.out.println("4 - Compra-venta.");
        System.out.println("5 - Consulta: Total Compras por articulo.");
        System.out.println("6 - Consulta: Total Ventas por vendedor.");
        System.out.println("7 - Consulta: Articulo mas y menos vendido.");

       /*     System.out.println(
                    "14. Crear Compra-Venta\n" +
                        "15. Mostrar todas las compras hechas por cliente (Elegir Cliente) (Articulo-cantidad-Cliente - vendedor - total (cantidad * precio).\n" +
                        "16. Media de las ventas por vendedor (elegir vendedor).\n" +
                        "17. Media de ventas totales (sin interaccion con el usuario");*/
    }

    private static void menuClientes() {
        System.out.println(
                " ---- CLIENTES----      \n" +
                        "1. Crear Cliente.\n" +
                        "2. Modificar Cliente.\n" +
                        "3. Eliminar Cliente.\n" +
                        "4. Listado Clientes.\n");
    }

    private static void menusVendedores() {
        System.out.println(
                " ---- VENDEDORES ----     \n " +
                        "1. Crear Vendedor.\n" +
                        "2. Modificar Vendedor.\n" +
                        "3. Eliminar Vendedor.\n" +
                        "4. Listado Vendedor.\n");
    }

    private static void menuArticulos() {
        System.out.println(
                " ---- ARTICULOS----      \n" +
                        "1. Crear Articulo.\n" +
                        "2. Modificar Articulo.\n" +
                        "3. Eliminar Articulo.\n" +
                        "4. Listado Articulos.\n");
    }

    private static void menuCompraVenta() {
        System.out.println(
                " ---- COMPRA VENTA----      \n" +
                        "1. Crear compra-venta.\n" +
                        "2. Modificar compra-venta.\n" +
                        "3. Eliminar compra-venta.\n" +
                        "4. Listado compra-venta.\n");
    }

}
