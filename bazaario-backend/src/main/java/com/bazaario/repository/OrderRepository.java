package com.bazaario.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bazaario.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Integer> {

	List<Order> findByUser_UserIdOrderByOrderDateDesc(Integer userId);
}
