package com.aluracursos.desafio.principal;

import com.aluracursos.desafio.model.Datos;
import com.aluracursos.desafio.model.DatosLibros;
import com.aluracursos.desafio.service.ConsumoAPI;
import com.aluracursos.desafio.service.ConvierteDatos;

import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {


    private static  final String URL_BASE="https://gutendex.com/books/";
    private ConsumoAPI consumoAPI = new ConsumoAPI();

    private ConvierteDatos conversor = new ConvierteDatos();

    private Scanner teclado = new Scanner(System.in);

    public void muestraElMenu(){

        var json= consumoAPI.obtenerDatos(URL_BASE);
        System.out.println(json);
        var datos = conversor.obtenerDatos(json, Datos.class);
        System.out.println(datos);

        //top 10 libros mas descargados
        System.out.println("Top 10 libros mas descargados");
        datos.resultados().stream()
                .sorted(Comparator.comparing(DatosLibros::numeroDeDescargas).reversed())
                .limit(10)
                .map(l->l.titulo().toUpperCase())
                .forEach(System.out::println);

        //busqueda de libros por nombres

        System.out.println("ingrese el nombre del libro que desea buscar ");
         var tituloLibro = teclado.nextLine();
         json = consumoAPI.obtenerDatos(URL_BASE+"?search="+ tituloLibro.replace(" ","+"));

         var datosBusqueda=conversor.obtenerDatos(json, Datos.class);
        Optional<DatosLibros> libroBuscado= datosBusqueda.resultados().stream()
                .filter(l -> l.titulo().toUpperCase().contains(tituloLibro.toUpperCase()))
                .findFirst();

        if (libroBuscado.isPresent()){
            System.out.println("Libro Encontrado");
            System.out.println(libroBuscado.get());
        }else{
            System.out.println("Libro No Encontrado");
        }

        //trabajando con estadisticas
        DoubleSummaryStatistics est = datosBusqueda.resultados().stream()
                .filter(d ->d.numeroDeDescargas()>0)
                .collect(Collectors.summarizingDouble((DatosLibros::numeroDeDescargas)));
        System.out.println("Cantidad media de descargas:"+est.getAverage());
        System.out.println("cantidad maxima de de descargas :"+est.getMax());
        System.out.println("cantidad minima de descargas:"+est.getMin());
        System.out.println("cantidad de registros evaluados p/calcular las estadisticas :"+est.getCount());




    }
}
