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


public class ControladorArticulo {

    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));


    /**
     * <Articulos>
     * <Articulo id=>
     * <NombreArticulo></NombreArticulo>
     * <Precio></Precio>
     * </Articulo>
     * </Articulos>
     */

    public void CrearArticulo(Collection col) throws XMLDBException, IOException, ParserConfigurationException {

        //  Collection col = Conexion.conectar();

        /* 1 - revisar si hay articulos y saber el ultimo id
         * 2- solicitar el nombre del articulo
         * 3- crear articulo( id + nombre)
         * */

        if (col != null) {  // Estamos conectados

            //----------------- SABER ULTIMO ID.-----------------------
            int ultimoId = -1;

            XPathQueryService servicio;
            servicio = (XPathQueryService) col.getService("XPathQueryService", "1.0");

            //Consulta de mi ultimo id de articulo.

            // /Articulos/Articulo[@id=last()]/concat(@id,\"\")
            // /Articulos/Articulo[position()=last()]/concat(@id, "")

            String consulta = "/Articulos/Articulo[position()=last()]/concat(@id, \"\")";
            ResourceSet result = servicio.query(consulta);

            //   ResourceSet result= servicio.query("/Articulos/Articulo[@idArticulo=last()]/NombreArticulo");
            // recorrer los datos del recurso.
            ResourceIterator i;
            i = result.getIterator();

            while (i.hasMoreResources()) {
                Resource r = i.nextResource();
                String resultado = r.getContent().toString();
                System.out.println(resultado);
                if (!(resultado.equals(""))) {
                    ultimoId = Integer.parseInt(resultado);
                    System.out.println(ultimoId);
                    ultimoId++;

                } else {
                    ultimoId = 1;
                }
                System.out.println("--------------------------------------------");
            }

            System.out.println(ultimoId);

            //------------------- CREAR EL OBJETO PARA DAR DE ALTA EN LA BD.

            System.out.println("Nombre del articulo: ");
            String NombreArticulo = br.readLine();
            double precio =  0.2;
            do {
                try {
                    System.out.println("Precio del articulo: ");
                    precio = Double.parseDouble(br.readLine());
                } catch (NumberFormatException n) {
                    System.out.println("Dato no valido");

                }
            } while (precio < 0.0);

            File articulos = new File("Articulos.xml");
            // nos aseguramos que se puede leer. Si no existe se debe crear.

            if (!articulos.canRead()) {
                crearElemento(col);
            }

            //Generar el xml
            String NuevoArticulo = "<Articulo id=\"" + String.valueOf(ultimoId) + "\"><NombreArticulo>" + NombreArticulo + "</NombreArticulo><Precio>" + precio + "</Precio></Articulo>";

            XPathQueryService servicio2;
            servicio2 = (XPathQueryService) col.getService("XPathQueryService", "1.0");

            ResourceSet insertarArticulo = servicio2.query("update insert " + NuevoArticulo + " into /Articulos");

            col.close();
            System.out.println("Articulo insertado.");


        }


    }

    public void ModificarArticulo(Collection col) throws IOException, XMLDBException {

        /* 1 - Mostrar todos los articulos
         *  2 - Solicitar al usuario que nos indique que id desea modificar
         *  3 - Solo se puede modificar el nombre, así que solicito nombre nuevo*/

        ListarArticulos(col);

        System.out.println("Escribe el ID del articulo a modificar: ");
        String articuloElegido = br.readLine();

        boolean existe = Comprobacion(articuloElegido, col);

        if (existe) {

            System.out.println("Escribe el nombre de nuevo: ");
            String nombre = br.readLine();
            XPathQueryService servicio = (XPathQueryService) col.getService("XPathQueryService", "1.0");

            ResourceSet result = servicio.query(
                    "update value /Articulos/Articulo[@id=" + articuloElegido + "]/NombreArticulo with data('" + nombre + "') ");

            col.close();
            System.out.println("Articulo actualizado.");

        } else {
            System.out.println("No existe el articulo.");
        }

    }

    public void EliminarArticulo(Collection col) throws IOException, XMLDBException {
        /* 1 - Mostrar todos los articulos
         *  2 - Solicitar al usuario que nos indique que id desea eliminar
         *  3 - comprobar si existe y si se puede eliminar, que no tenga relaciones*/

        ListarArticulos(col);
        boolean existe = false;


       do {
            System.out.println("Escribe el ID del articulo a eliminar: ");
            String articuloElegido = br.readLine();

            existe = Comprobacion(articuloElegido, col);
            XPathQueryService servicio = (XPathQueryService) col.getService("XPathQueryService", "1.0");
            //Consulta para borrar un departamento --> update delete
            ResourceSet result = servicio.query(
                    "update delete /Articulos/Articulo[@id=" + articuloElegido + "]");
            col.close();
            System.out.println("Articulo  eliminado.");


        } while(!existe);

    }

    boolean Comprobacion(String articulo, Collection col) {

        boolean resultado = false;
        try {
            XPathQueryService servicio = (XPathQueryService) col.getService("XPathQueryService", "1.0");
            //Consulta para consultar la información de un departamento
            ResourceSet result = servicio.query("/Articulos/Articulo[@id=" + articulo + "]");
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

    public void ListarArticulos(Collection col) {

        try {
            XPathQueryService servicio;
            servicio = (XPathQueryService) col.getService("XPathQueryService", "1.0");
            //Preparamos la consulta
            ResourceSet result = servicio.query("for $cli in /Articulos/Articulo/concat(@id, \" - \", NombreArticulo) return $cli");
            // recorrer los datos del recurso.
            ResourceIterator i;
            i = result.getIterator();
            if (!i.hasMoreResources()) {
                System.out.println(" LA CONSULTA NO DEVUELVE NADA O ESTÁ MAL ESCRITA");
            }
            System.out.println("ID -   NOMBRE  -  PRECIO ");
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
            res = (XMLResource) col.createResource("Articulos.xml", "XMLResource");

            // Elegimos el fichero .xml que queremos añadir a la colección
            File f = new File("Articulos.xml");

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
        Document darticulo = implementation.createDocument(null, "Articulos", null);
        ;
        //Se escribe el contenido del XML en un archivo
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
        } catch (
                TransformerConfigurationException e) {
            e.printStackTrace();
        }
        DOMSource source = new DOMSource(darticulo);
        StreamResult result = new StreamResult(f);
        try {
            transformer.transform(source, result);
        } catch (
                TransformerException e) {
            e.printStackTrace();
        }
    }

}
