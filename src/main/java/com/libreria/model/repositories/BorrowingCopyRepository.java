package com.libreria.model.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.libreria.model.book.BorrowingCopy;

public interface BorrowingCopyRepository extends JpaRepository<BorrowingCopy, Long>{

} 