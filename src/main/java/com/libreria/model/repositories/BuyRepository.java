package com.libreria.model.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.libreria.model.exchange.Buy;

public interface BuyRepository extends JpaRepository<Buy, Long>{
	

}
