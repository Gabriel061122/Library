package com.libreria.model.repositories.specification;

import com.libreria.model.book.Book;

import com.libreria.model.book.Genre;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class BookSpecifications {

	public static Specification<Book> hasTitle (String title){
		return (root, query, cb) ->
			(title == null || title.isEmpty()) ? null : cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
	}

	public static Specification<Book> hasAuthor (String author){
		return (root, query, cb) -> {
			return (author == null || author.isEmpty()) ? null : cb.like(cb.lower(root.get("author")), "%" + author.toLowerCase() + "%");
		};
	}

	public static Specification<Book> hasGenre (List<Long> genres){
		return (root, query, cb) -> {
			if (genres == null || genres.isEmpty()) {
				return cb.conjunction();}
			query.distinct(true);

			Join<Book, Genre> bookGenreJoin = root.join("genreo", JoinType.INNER);

			return bookGenreJoin.get("id").in(genres);
		};
	};
}
