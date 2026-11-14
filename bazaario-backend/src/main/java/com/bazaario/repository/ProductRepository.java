package com.bazaario.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bazaario.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

	List<Product> findByCategory_CategoryId(Integer categoryId);

	// Derived query with wildcards on two columns - equivalent to the ProductDAO
	// "LIKE ? OR description LIKE ?" search, but with zero hand-written SQL.
	List<Product> findByProductNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
			String nameKeyword, String descriptionKeyword);
}
