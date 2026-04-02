package com.libreria.model.book;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private int price;
    private int stock;
    private int minStock;
    private int maxStock;
    private int criticalStock;
    private int minCriticalStock;
    private int maxCriticalStock;

    public Book(
            String title,
            String author,
            String isbn,
            int price,
            int stock,
            int minStock,
            int maxStock,
            int criticalStock,
            int minCriticalStock,
            int maxCriticalStock
    ) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.price = price;
        this.stock = stock;
        this.minStock = minStock;
        this.maxStock = maxStock;
        this.criticalStock = criticalStock;
        this.minCriticalStock = minCriticalStock;
        this.maxCriticalStock = maxCriticalStock;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", isbn='" + isbn + '\'' +
                ", price=" + price +
                ", stock=" + stock +
                ", minStock=" + minStock +
                ", maxStock=" + maxStock +
                ", criticalStock=" + criticalStock +
                ", minCriticalStock=" + minCriticalStock +
                ", maxCriticalStock=" + maxCriticalStock +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        boolean isEqual = false;
        if (o instanceof Book) {
            isEqual = this.id.equals(((Book) o).id);
        }
        return isEqual;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
