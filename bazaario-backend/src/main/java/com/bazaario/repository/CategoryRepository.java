package com.bazaario.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bazaario.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
