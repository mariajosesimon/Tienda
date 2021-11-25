package com.company;

import com.company.Controladores.Conexion;
import com.company.Controladores.ControladorCliente;
import org.xml.sax.SAXException;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XPathQueryService;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

       final  Collection col = Conexion.conectar();


        ControladorCliente cCliente = new ControladorCliente();

        int op = 0;
        do {
            do {
                try {
                    mostrarMenu();

                    op = Integer.parseInt(br.readLine());

                    switch (op) {
                        case 1:
                            // Crear Cliente
                            cCliente.CrearCliente(col);


                            break;
                        case 2:
                            //Modificar Cliente.

                            break;
                        case 3:
                            // 3. Eliminar Cliente.


                            break;
                        case 4:

                            // Listado Clientes.

                            break;
                        case 5:
                            //eliminar una venta.


                            break;

                        case 6:


                            break;


                    }
                } catch (IOException e) {
                    System.out.println("Dato no valido.");
                } catch (XMLDBException e) {
                    System.out.println("No puedo acceder a CrearCliente.");
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                }

            } while (!(op > 0 && op < 18));

        } while (op != 17);

        System.out.println("FIN");
    }


    private static void mostrarMenu() {

        System.out.println("------------MENU------------- ");
        System.out.println(
                "1. Crear Cliente.\n" +
                        "2. Modificar Cliente.\n" +
                        "3. Eliminar Cliente.\n" +
                        "4. Listado Clientes.\n" +
                       /* "5. Crear Vendedor.\n" +
                        "6. Modificar Vendedor.\n" +
                        "7. Eliminar Vendedor.\n" +
                        "8. Listado Vendedor.\n" +
                        "9. Crear Articulo.\n" +
                        "10. Modificar Articulo.\n" +
                        "11. Eliminar Articulo.\n" +
                        "12. Listado Articulos.\n" +
                        "13. Listado Vendedor.\n" +
                        "14. Crear Compra-Venta\n" +
                        "15. Mostrar todas las compras hechas por cliente (Elegir Cliente) (Articulo-cantidad-Cliente - vendedor - total (cantidad * precio).\n" +
                        "16. Media de las ventas por vendedor (elegir vendedor).\n" +*/
                        "17. Media de ventas totales (sin interaccion con el usuario");
    }

}
