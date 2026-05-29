package com.libreria.model.repositories;

import com.libreria.model.exchange.Order;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long>{

}


