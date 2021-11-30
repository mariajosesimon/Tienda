package com.company.Controladores;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.xmldb.api.base.*;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.XPathQueryService;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;


public class ControladorCliente {

    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));


    /**
     * <Clientes>
     * <Cliente id=>
     * <NombreCliente></NombreCliente>
     * </Cliente>
     * </Clientes>
     */

    public void CrearCliente(Collection col) throws XMLDBException, IOException, ParserConfigurationException {

        //  Collection col = Conexion.conectar();

        /* 1 - revisar si hay clientes y saber el ultimo id
         * 2- solicitar el nombre del cliente
         * 3- crear cliente( id + nombre)
         * */

        if (col != null) {  // Estamos conectados

            //----------------- SABER ULTIMO ID.-----------------------
            int ultimoId = -1;

            XPathQueryService servicio;
            servicio = (XPathQueryService) col.getService("XPathQueryService", "1.0");

            //Consulta de mi ultimo id de cliente.

            // /Clientes/Cliente[@id=last()]/concat(@id,\"\")
            // /Clientes/Cliente[position()=last()]/concat(@id, "")

            String consulta = "/Clientes/Cliente[position()=last()]/concat(@id, \"\")";
            ResourceSet result = servicio.query(consulta);

            //   ResourceSet result= servicio.query("/Clientes/Cliente[@idCliente=last()]/NombreCliente");
            // recorrer los datos del recurso.
            ResourceIterator i;
            i = result.getIterator();

            while (i.hasMoreResources()) {
                Resource r = i.nextResource();
                String resultado = r.getContent().toString();
                // System.out.println(resultado);
                if (!(resultado.equals(""))) {
                    ultimoId = Integer.parseInt(resultado);
                    //   System.out.println(ultimoId);
                    ultimoId++;

                } else {
                    ultimoId = 1;
                }
                System.out.println("--------------------------------------------");
            }

            // System.out.println(ultimoId);

            //------------------- CREAR EL OBJETO PARA DAR DE ALTA EN LA BD.

            System.out.println("Nombre del cliente: ");
            String NombreCliente = br.readLine();

            File clientes = new File("Clientes.xml");
            // nos aseguramos que se puede leer. Si no existe se debe crear.

            if (!clientes.canRead()) {
                crearElemento(col);
            }

            //Generar el xml
            String NuevoCliente = "<Cliente id=\"" + String.valueOf(ultimoId) + "\"><NombreCliente>" + NombreCliente + "</NombreCliente></Cliente>";

            XPathQueryService servicio2;
            servicio2 = (XPathQueryService) col.getService("XPathQueryService", "1.0");

            ResourceSet insertarCliente = servicio2.query("update insert " + NuevoCliente + " into /Clientes");

            col.close();
            System.out.println("Cliente insertado.");


        }


    }

    public void ModificarCliente(Collection col) throws IOException, XMLDBException {

        /* 1 - Mostrar todos los clientes
         *  2 - Solicitar al usuario que nos indique que id desea modificar
         *  3 - Solo se puede modificar el nombre, así que solicito nombre nuevo*/

        ListarClientes(col);

        boolean existe = false;

        do {
            System.out.println("Escribe el ID del cliente a modificar: ");
            String clienteElegido = br.readLine();

            existe = Comprobacion(clienteElegido, col);

            System.out.println("Escribe el nombre de nuevo: ");
            String nombre = br.readLine();
            XPathQueryService servicio = (XPathQueryService) col.getService("XPathQueryService", "1.0");

            ResourceSet result = servicio.query(
                    "update value /Clientes/Cliente[@id=" + clienteElegido + "]/NombreCliente with data('" + nombre + "') ");

            col.close();
            System.out.println("Cliente actualizado.");

        } while (!existe);

    }

    public void EliminarCliente(Collection col) throws IOException, XMLDBException {
        /* 1 - Mostrar todos los clientes
         *  2 - Solicitar al usuario que nos indique que id desea eliminar
         *  3 - comprobar si existe y si se puede eliminar, que no tenga relaciones*/

        ListarClientes(col);

        boolean existe = false;

        do {

            System.out.println("Escribe el ID del cliente a eliminar: ");
            String clienteElegido = br.readLine();
            existe = Comprobacion(clienteElegido, col);
            XPathQueryService servicio = (XPathQueryService) col.getService("XPathQueryService", "1.0");
            //Consulta para borrar un departamento --> update delete
            ResourceSet result = servicio.query(
                    "update delete /Clientes/Cliente[@id=" + clienteElegido + "]");
            col.close();
            System.out.println("Cliente  eliminado.");


        } while (!existe);

    }

    public boolean Comprobacion(String cliente, Collection col) {

        boolean resultado = false;
        try {
            XPathQueryService servicio = (XPathQueryService) col.getService("XPathQueryService", "1.0");
            //Consulta para consultar la información de un cliente
            ResourceSet result = servicio.query("/Clientes/Cliente[@id=" + cliente + "]");
            ResourceIterator i;
            i = result.getIterator();
            col.close();
            if (!i.hasMoreResources()) {
                resultado = false;
            } else {
                resultado = true;
            }
        } catch (Exception e) {
            System.out.println("Error al consultar.");

        }

        return resultado;

    }

    public void ListarClientes(Collection col) {

        try {
            XPathQueryService servicio;
            servicio = (XPathQueryService) col.getService("XPathQueryService", "1.0");
            //Preparamos la consulta
            ResourceSet result = servicio.query("for $cli in /Clientes/Cliente/concat(@id, \" - \", NombreCliente) return $cli");
            // recorrer los datos del recurso.
            ResourceIterator i;
            i = result.getIterator();
            if (!i.hasMoreResources()) {
                System.out.println(" LA CONSULTA NO DEVUELVE NADA O ESTÁ MAL ESCRITA");
            }
            System.out.println("ID - NOMBRE");
            while (i.hasMoreResources()) {
                Resource r = i.nextResource();
                //System.out.println("--------------------------------------------");
                System.out.println((String) r.getContent());
            }
            col.close();
        } catch (XMLDBException e) {
            System.out.println(" ERROR AL CONSULTAR DOCUMENTO.");
            e.printStackTrace();
        }


    }

    void crearElemento(Collection col) {
        try {

            // Inicializamos el recurso
            XMLResource res = null;

            // Creamos el recurso -> recibe 2 parámetros tipo String:
            // s: nombre.xml (si lo dejamos null, pondrá un nombre aleatorio)
            // s1: tipo recurso (en este caso, siempre será XMLResource)
            res = (XMLResource) col.createResource("Clientes.xml", "XMLResource");

            // Elegimos el fichero .xml que queremos añadir a la colección
            File f = new File("Clientes.xml");

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
        Document dcliente = implementation.createDocument(null, "Clientes", null);
        ;
        //Se escribe el contenido del XML en un archivo
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
        } catch (
                TransformerConfigurationException e) {
            System.out.println("Problemas en la configuracion");
        }
        DOMSource source = new DOMSource(dcliente);
        StreamResult result = new StreamResult(f);
        try {
            transformer.transform(source, result);
        } catch (
                TransformerException e) {
            System.out.println("Problemas al generar tu xml");
        }
    }

}
