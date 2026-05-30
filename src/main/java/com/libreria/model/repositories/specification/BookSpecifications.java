package com.libreria.model.repositories.specification;

import com.libreria.model.book.Book;

import org.springframework.data.jpa.domain.Specification;

import com.libreria.model.book.Genre;

public class BookSpecifications {

	public static Specification<Book> hasTitle (String title){
		return (root, query, cb) -> 
			title == null ? null : cb.equal(root.get("title"), title);
	}

	public static Specification<Book> hasAuthor (String author){
		return (root, query, cb) -> {
			return author == null ? null : cb.equal(root.get("author"), author);
		};
	}

	public static Specification<Book> hasGenre (Genre genre){
		return (root, query, cb) -> {
			return genre == null ? null : cb.equal(root.get("genre"), genre);
		};
	}
}
