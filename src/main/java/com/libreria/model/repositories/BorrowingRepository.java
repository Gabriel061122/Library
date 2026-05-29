package com.libreria.model.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.libreria.model.exchange.Borrowing;

public interface BorrowingRepository extends JpaRepository<Borrowing, Long>{

}
