package com.libreria.model.book;

import org.hibernate.annotations.IdGeneratorType;

import jakarta.annotation.Generated;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class BorrowingCopy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "book_id")
    @ToString.Exclude
    private Book book;
    
    private Avaliavility avlbl;

    public BorrowingCopy(Book book, Avaliavility avlbl){
        this.book = book;
        this.avlbl = avlbl;
    }
}
