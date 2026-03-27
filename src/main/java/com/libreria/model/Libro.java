package com.libreria.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    private String autor;
    private String isbn;
    private int precio;
    private int stock;
    private int stockMinimo;
    private int stockMaximo;
    private int stockCritico;
    private int stockCriticoMinimo;
    private int stockCriticoMaximo;

    public Libro(String titulo, String autor, String isbn, int precio, int stock, int stockMinimo, int stockMaximo, int stockCritico, int stockCriticoMinimo, int stockCriticoMaximo) {
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.precio = precio;
        this.stock = stock;
        this.stockMinimo = stockMinimo;
        this.stockMaximo = stockMaximo;
        this.stockCritico = stockCritico;
        this.stockCriticoMinimo = stockCriticoMinimo;
        this.stockCriticoMaximo = stockCriticoMaximo;
    }

    @Override
    public String toString() {
        return "Libro{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", autor='" + autor + '\'' +
                ", isbn='" + isbn + '\'' +
                ", precio=" + precio +
                ", stock=" + stock +
                ", stockMinimo=" + stockMinimo +
                ", stockMaximo=" + stockMaximo +
                ", stockCritico=" + stockCritico +
                ", stockCriticoMinimo=" + stockCriticoMinimo +
                ", stockCriticoMaximo=" + stockCriticoMaximo +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        boolean isEqual = false;
        if (o instanceof Libro) {
            isEqual = this.id.equals(((Libro) o).id);
        }
        return isEqual;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}