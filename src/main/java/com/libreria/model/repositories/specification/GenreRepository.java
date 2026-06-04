package com.libreria.model.repositories.specification;

import com.libreria.model.book.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Long> {
}
