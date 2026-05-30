package com.libreria.model.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.libreria.model.book.*;

public interface BookRepository extends JpaRepository<Book, String>, JpaSpecificationExecutor<Book>{
    

}
