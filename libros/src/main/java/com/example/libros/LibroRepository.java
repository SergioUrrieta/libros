package com.example.libros;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

@Repository
public class LibroRepository {
    private List<Libro> libros = new ArrayList<>();

    public void guardar(Libro libro) {
        libros.add(libro);
    }

    public List<Libro> obtenerTodos() {
        return new ArrayList<>(libros);
    }

    public List<Libro> buscarPorIdioma(String idioma) {
        return libros.stream()
                .filter(libro -> !libro.getIdiomas().isEmpty() && libro.getIdiomas().get(0).equalsIgnoreCase(idioma))
                .collect(Collectors.toList());
    }
}
