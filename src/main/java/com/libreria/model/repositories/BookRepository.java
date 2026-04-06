package com.libreria.model.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.libreria.model.book.*;

public interface BookRepository extends JpaRepository<Book, Long>{
    
}
