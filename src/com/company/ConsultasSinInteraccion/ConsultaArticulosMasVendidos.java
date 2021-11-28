package com.company.ConsultasSinInteraccion;


import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmldb.api.base.*;
import org.xmldb.api.modules.XPathQueryService;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.*;

public class ConsultaArticulosMasVendidos {


    //Mostrar los articulos que se han vendido más e indicar la cantidad vendida.
    //Sin interaccion del usuario

    /*for $art in distinct-values(/Articulos/Articulo/NombreArticulo/text())
    let $maximo:= max(sum(/CompraVenta/Venta/Compra/ProductoUnidades[Articulo=$art]/Cantidad))
            return concat($maximo, " ", $art)*/

    public void ArticuloMasVendido(Collection col) throws XMLDBException, ParserConfigurationException, IOException, SAXException {
        String consulta = "for $art in distinct-values(/Articulos/Articulo/NombreArticulo/text())\n" +
                "let $maximo:= max(sum(/CompraVenta/Venta/Compra/ProductoUnidades[Articulo=$art]/Cantidad))\n" +
                "return <Result><Total>{data($maximo)}</Total>\n" +
                "<Art>{data($art)}</Art></Result>";


        //Me va a devolver un formato xml


        XPathQueryService servicio = (XPathQueryService) col.getService("XPathQueryService", "1.0");
        ResourceSet result = servicio.query(consulta);

        ResourceIterator i;

        i = result.getIterator();

        FileWriter f = new FileWriter("Result.xml");

        String resultado = "";
        f.write("<Resultados>"); // Necesito un nodo raiz
        while (i.hasMoreResources()) {
            Resource r = i.nextResource();
            resultado = r.getContent().toString();
          //  System.out.println(resultado);
            f.write(resultado);
        }
        f.write("</Resultados>"); // Fin del nodo raiz
        f.close();


        File fileReader = new File("Result.xml");

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.parse(fileReader);
        document.getDocumentElement().normalize();

        NodeList nList = document.getElementsByTagName("Result");
        String articuloMayor="";
        String articuloMenor="";
        int cantidadMayor=Integer.MIN_VALUE;
        int cantidadMenor=Integer.MAX_VALUE;
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                if(cantidadMayor< Integer.parseInt(eElement.getElementsByTagName("Total").item(0).getTextContent())){
                    cantidadMayor = Integer.parseInt(eElement.getElementsByTagName("Total").item(0).getTextContent());
                    articuloMayor = eElement.getElementsByTagName("Art").item(0).getTextContent();
                }
                if(cantidadMenor> Integer.parseInt(eElement.getElementsByTagName("Total").item(0).getTextContent())){
                    cantidadMenor = Integer.parseInt(eElement.getElementsByTagName("Total").item(0).getTextContent());
                    articuloMenor = eElement.getElementsByTagName("Art").item(0).getTextContent();
                }

            }

        }// Fin del for

        System.out.println("El articulo más vendido es " + articuloMayor + ". Se han vendido " + cantidadMayor + " unidades.");
        System.out.println("El articulo menos vendido es " + articuloMenor + ". Se han vendido " + cantidadMenor + " unidades.");


    } // fin public void


}



