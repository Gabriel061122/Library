package com.libreria.model.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.libreria.model.book.*;

public interface BookRepository extends JpaRepository<Book, String>{
    
	public List<Book> findAllByTitleIgnoreCaseAndGenreAndAuthor(String title, Genre genre, String author);

}
