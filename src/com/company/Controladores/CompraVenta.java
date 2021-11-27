package com.company.Controladores;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xmldb.api.base.*;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.XPathQueryService;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;

public class CompraVenta {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    /**
     * <Venta id>
     * <Cliente>nombre</Cliente>
     * <Vendedor>nombre</Vendedor>
     * <Compra>
     * <ProductoUnidades total = "precio*unidades">
     * <Articulo>nombre</Articulo>
     * <Cantidad>unidades</Cantidad>
     * </ProductoUnidades>
     * </Compra>
     * </Venta>
     */

    //Requiere de interaccion por parte del usuario.
    /*1- Mostrar todos los clientes
     * 2- Escoger cliente
     * 3- Mostrar Vendedores
     * 4- Escoger vendedor
     * 5- Mostrar articulos
     * 6- Escoger articulo
     * 7- Indicar cantidad
     * 8- preguntar si quiere seguir con la compra o finalizar.
     * 8a - si sigue comprando, volver al punto 5.
     * 9 - fin de compra. Generar el xml y añadir al existdb*/

    ControladorCliente cCliente = new ControladorCliente();
    ControladorVendedor cVendedor = new ControladorVendedor();
    ControladorArticulo cArticulo = new ControladorArticulo();

    public void CrearCompraVenta(Collection col) throws IOException, XMLDBException, TransformerException {


        //*************************** CLIENTE ************************************
        String clienteElegido = "";
        boolean existeCliente = false;
        do {
            //1- Mostrar todos los clientes
            System.out.println("Elige un cliente (ID): ");
            cCliente.ListarClientes(col);
            //2- Escoger cliente
            clienteElegido = br.readLine();
            existeCliente = cCliente.Comprobacion(clienteElegido, col);
        } while (!existeCliente);

        // Consulta para extraer solo el nombre  /Clientes/Cliente[@id=4]/NombreCliente/text()

        String nombreCliente = "";
        XPathQueryService servicioCli = (XPathQueryService) col.getService("XPathQueryService", "1.0");
        ResourceSet resultCliente = servicioCli.query("/Clientes/Cliente[@id=" + clienteElegido + "]/NombreCliente/text()");
        ResourceIterator j;
        j = resultCliente.getIterator();
        while (j.hasMoreResources()) {
            Resource h = j.nextResource();
            nombreCliente = (String) h.getContent();
        }

        //*************************** VENDEDOR ************************************

        String vendedorElegido = "";
        boolean existeVendedor = false;
        do {
            //3- Mostrar todos los vendedores
            System.out.println("Elige un vendedor (ID): ");
            cVendedor.ListarVendedores(col);
            //4- Escoger vendedor
            vendedorElegido = br.readLine();
            existeVendedor = cVendedor.Comprobacion(vendedorElegido, col);
        } while (!existeVendedor);

        String nombreVendedor = "";
        XPathQueryService servicioVend = (XPathQueryService) col.getService("XPathQueryService", "1.0");
        ResourceSet resultVendedor = servicioVend.query("/Vendedores/Vendedor[@id=" + vendedorElegido + "]/NombreVendedor/text()");
        ResourceIterator k;
        k = resultVendedor.getIterator();
        while (k.hasMoreResources()) {
            Resource l = k.nextResource();
            nombreVendedor = (String) l.getContent();
        }

        //*************************** COMPRA ************************************
        /* 5- Mostrar articulos
         * 6- Escoger articulo
         * 7- Indicar cantidad
         * 8- preguntar si quiere seguir con la compra o finalizar.*/

        /*Reutilización del codigo de la anterior practica, adaptado para ésta.*/

        String articuloElegido;
        ArrayList<Compra> compra = new ArrayList<Compra>();

        int cantidad = 0;
        boolean continuar = true;
        boolean existeArticulo = false;

        do {
            do {
                System.out.println("Elige un artículo (ID): ");
                cArticulo.ListarArticulos(col);
                articuloElegido = br.readLine();
                existeArticulo = cArticulo.Comprobacion(articuloElegido, col);
            } while (!existeArticulo);

            do {
                try {
                    System.out.println("Cuantos artículos quieres comprar: ");
                    cantidad = Integer.parseInt(br.readLine());
                } catch (NumberFormatException n) {
                    System.out.println("No has escrito un numero.");
                    cantidad = 0;
                }
            } while (cantidad == 0);

            Compra c = new Compra();
            String nombreArticulo = "";
            XPathQueryService servicioArt = (XPathQueryService) col.getService("XPathQueryService", "1.0");
            String consulta = "/Articulos/Articulo[@id=\"" + articuloElegido + "\"]/NombreArticulo/text()";
            ResourceSet resultArticulo = servicioArt.query(consulta);
            ResourceIterator a;
            a = resultArticulo.getIterator();
            while (a.hasMoreResources()) {
                Resource r = a.nextResource();
                nombreArticulo = (String) r.getContent();
            }

            c.setNombreArticulo(nombreArticulo);
            c.setCantidad(cantidad);
            compra.add(c);

            System.out.println("Quieres continuar con la compra (true/false): ");
            continuar = Boolean.parseBoolean(br.readLine());

        } while (continuar);


        //----------------- SABER ULTIMO ID.-----------------------

        int ultimoId = -1;

        XPathQueryService servicio;
        servicio = (XPathQueryService) col.getService("XPathQueryService", "1.0");

        //Consultar ultimo id de las compra-ventas

        String consulta = "/CompraVenta/Venta[position()=last()]/concat(@id, \"\")";
        ResourceSet result = servicio.query(consulta);

        // recorrer los datos del recurso.
        ResourceIterator i;
        i = result.getIterator();

        while (i.hasMoreResources()) {
            Resource r = i.nextResource();
            String resultado = r.getContent().toString();
            // System.out.println(resultado);
            if (!(resultado.equals(""))) {
                ultimoId = Integer.parseInt(resultado);
                // System.out.println(ultimoId);
                ultimoId++;

            } else {
                ultimoId = 1;
            }
            System.out.println("--------------------------------------------");
        }

        // System.out.println(ultimoId);

        // 9. Generar el xml y añadir al existdb
        File compraVenta = new File("CompraVenta.xml");
        // nos aseguramos que se puede leer. Si no existe se debe crear.

        if (!compraVenta.canRead()) {
            crearElemento(col);
        }

        //Generar el xml

        String cadenaCompra = lecturaCompra(compra, col);

        File compraVentas = new File("CompraVenta.xml");
        // nos aseguramos que se puede leer. Si no existe se debe crear.

        if (!compraVentas.canRead()) {
            crearElemento(col);
        }

        String NuevaCompraVenta = "<Venta id=\"" + String.valueOf(ultimoId) + "\"><Cliente>" + nombreCliente + "</Cliente><Vendedor>" + nombreVendedor + "</Vendedor><Compra>" + cadenaCompra + "</Compra></Venta>";
        XPathQueryService servicioCompraVenta;
        servicioCompraVenta = (XPathQueryService) col.getService("XPathQueryService", "1.0");

        ResourceSet insertarCompraVenta = servicioCompraVenta.query("update insert " + NuevaCompraVenta + " into /CompraVenta");

        //Generar fichero xml por si quisieramos exportarlo.
        generarFicheroXMLCompraVenta(col);
        col.close();
        System.out.println("Compra venta insertada.");

    }
    public void ModificarCompraVenta(Collection col){
        
    }
    public void EliminarCompraVenta(Collection col){}
    public void ListarCompraVenta(Collection col){}

    String lecturaCompra(ArrayList<Compra> compra, Collection col) throws XMLDBException, IOException {

        /* esta es la cadena que debo generar
         <ProductoUnidades total = "precio*unidades">
         * <Articulo>nombre</Articulo>
         * <Cantidad>unidades</Cantidad>
         * </ProductoUnidades>  */

        double precio, total;

        String cadenaDevolver = "";

        //Consulta para obtener el precio
        // /Articulos/Articulo[NombreArticulo="Sacapuntas"]/Precio/text()
        String resultado = "";
        double price;
        for (Compra c : compra) {
            XPathQueryService sPrecio = (XPathQueryService) col.getService("XPathQueryService", "1.0");
            ResourceSet resultadoPrecio = sPrecio.query("/Articulos/Articulo[NombreArticulo=\"" + c.getNombreArticulo() + "\"]/Precio/text()");
            System.out.println(c.getNombreArticulo());
            ResourceIterator i = resultadoPrecio.getIterator();
            while (i.hasMoreResources()) {
                Resource r = i.nextResource();
                resultado = r.getContent().toString();
            }
            price = Double.parseDouble(resultado);
            total = c.getCantidad() * price;  // calculamos el total de unidades comprada por el precio del articulo
            cadenaDevolver = cadenaDevolver.concat("<ProductoUnidades total = \"" + String.format("%.2f", total) + "\"><Articulo>" + c.getNombreArticulo() + "</Articulo><Cantidad>" + c.getCantidad() + "</Cantidad></ProductoUnidades>");
        }
        return cadenaDevolver;


    }

    void crearElemento(Collection col) {
        try {

            // Inicializamos el recurso
            XMLResource res = null;

            // Creamos el recurso -> recibe 2 parámetros tipo String:
            // s: nombre.xml (si lo dejamos null, pondrá un nombre aleatorio)
            // s1: tipo recurso (en este caso, siempre será XMLResource)
            res = (XMLResource) col.createResource("CompraVenta.xml", "XMLResource");

            // Elegimos el fichero .xml que queremos añadir a la colección
            File f = new File("CompraVenta.xml");

            crearFicheroXML(f);
            // Fijamos como contenido ese archivo .xml elegido
            res.setContent(f);
            col.storeResource(res); // lo añadimos a la colección


        } catch (Exception e) {
            System.out.println("Error al consultar.");
            // e.printStackTrace();
        }

    }

    void crearFicheroXML(File f) throws ParserConfigurationException {

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        DOMImplementation implementation = docBuilder.getDOMImplementation();

        //Elemento raíz
        Document dcompraVenta = implementation.createDocument(null, "CompraVenta", null);

        //Se escribe el contenido del XML en un archivo
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
        } catch (
                TransformerConfigurationException e) {
            e.printStackTrace();
        }
        DOMSource source = new DOMSource(dcompraVenta);
        StreamResult result = new StreamResult(f);
        try {
            transformer.transform(source, result);
        } catch (
                TransformerException e) {
            e.printStackTrace();
        }
    }

    void generarFicheroXMLCompraVenta(Collection col) throws XMLDBException, TransformerException {

        XMLResource res = (XMLResource) col.getResource("CompraVenta.xml");

        File file = new File("CompraVentasExport.xml");
        // Volcado del documento a un arbol DOM
        Node document = res.getContentAsDOM();
        Source source = new DOMSource(document);
        Result resultado = new StreamResult(String.valueOf(file));
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.transform(source, resultado);

      /* DE ESTA FORMA MUESTRA POR PANTALLA EL XML COMPLETO Y NO QUIERO QUE SE MUESTRE POR PANTALLA, SOLO QUIERO ALMACENAR
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        // Volcado del documento de memoria a consola
        Result console = new StreamResult(System.out);
        transformer.transform(source, console);
        // Volcado del documento a un fichero
        Result fichero = new StreamResult(new java.io.File("CompraVentasExport.xml"));
        transformer = TransformerFactory.newInstance().newTransformer();
        transformer.transform(source, fichero);
*/
    }

}
