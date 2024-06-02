package com.example.libros;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class GutendexApiRequest implements CommandLineRunner {

    private static List<Libro> libros;

    public static void main(String[] args) {
        SpringApplication.run(GutendexApiRequest.class, args);
    }

    @Override
    public void run(String... args) {
        cargarLibros();  // Cargar los libros al iniciar
        Scanner scanner = new Scanner(System.in);
        while (true) {
            mostrarMenu();
            int opcion = scanner.nextInt();
            scanner.nextLine(); // Consumir la nueva línea

            switch (opcion) {
                case 1:
                    buscarLibroPorTitulo(scanner);
                    break;
                case 2:
                    listarLibrosRegistrados();
                    break;
                case 3:
                    listarAutoresRegistrados();
                    break;
                case 4:
                    listarAutoresYLibrosPorAno(scanner);
                    break;
                case 5:
                    listarLibrosPorIdiomas(scanner);
                    break;
                case 6:
                    System.out.println("Saliendo...");
                    scanner.close();
                    System.exit(0);
                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
            }
        }
    }

    private void mostrarMenu() {
        System.out.println("Seleccione una opción:");
        System.out.println("1. Buscar libro por título");
        System.out.println("2. Listar libros registrados");
        System.out.println("3. Listar autores registrados");
        System.out.println("4. Listar autores y libros por año");
        System.out.println("5. Listar libros por idioma");
        System.out.println("6. Salir");
    }

    private void cargarLibros() {
        try {
            // Crear un cliente HTTP
            HttpClient client = HttpClient.newHttpClient();

            // Crear una solicitud HTTP
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://gutendex.com/books/"))
                    .GET() // Usar el método GET
                    .build();

            // Enviar la solicitud y manejar la respuesta
            String responseBody = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
            Gson gson = new GsonBuilder().create();
            GutendexResponse response = gson.fromJson(responseBody, GutendexResponse.class);
            libros = response.getLibros();
        } catch (Exception e) {
            System.err.println("Error en la solicitud: " + e.getMessage());
        }
    }

    private void buscarLibroPorTitulo(Scanner scanner) {
        System.out.print("Ingrese el título del libro: ");
        String titulo = scanner.nextLine().toLowerCase();
        List<Libro> resultados = libros.stream()
                .filter(libro -> libro.getTitulo().toLowerCase().contains(titulo))
                .collect(Collectors.toList());
        if (resultados.isEmpty()) {
            System.out.println("No se encontraron libros con ese título.");
        } else {
            resultados.forEach(System.out::println);
        }
    }

    private void listarLibrosRegistrados() {
        if (libros == null || libros.isEmpty()) {
            System.out.println("No hay libros registrados.");
        } else {
            libros.forEach(System.out::println);
        }
    }

    private void listarAutoresRegistrados() {
        if (libros == null || libros.isEmpty()) {
            System.out.println("No hay autores registrados.");
        } else {
            libros.stream()
                    .flatMap(libro -> libro.getAutores().stream())
                    .distinct()
                    .forEach(System.out::println);
        }
    }

    private void listarAutoresYLibrosPorAno(Scanner scanner) {
        System.out.print("Ingrese el año: ");
        int ano = scanner.nextInt();
        scanner.nextLine(); // Consumir la nueva línea
        List<Libro> resultados = libros.stream()
                .filter(libro -> libro.getAno() == ano)
                .collect(Collectors.toList());
        if (resultados.isEmpty()) {
            System.out.println("No se encontraron libros para ese año.");
        } else {
            resultados.forEach(libro -> {
                System.out.println("Autor(es) de " + libro.getTitulo() + ":");
                libro.getAutores().forEach(System.out::println);
            });
        }
    }

    private void listarLibrosPorIdiomas(Scanner scanner) {
        System.out.print("Ingrese el idioma (Ejemplo:(en),para libros en ingles: ");
        String idioma = scanner.nextLine().toLowerCase();
        List<Libro> resultados = libros.stream()
                .filter(libro -> libro.getIdiomas().stream().anyMatch(idiomaLibro -> idiomaLibro.equalsIgnoreCase(idioma)))
                .collect(Collectors.toList());
        if (resultados.isEmpty()) {
            System.out.println("No se encontraron libros en ese idioma.");
        } else {
            resultados.forEach(System.out::println);
        }
    }
}
