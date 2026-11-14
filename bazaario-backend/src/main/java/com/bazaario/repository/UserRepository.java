package com.bazaario.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bazaario.entity.User;

// Extending JpaRepository gives you findAll(), findById(), save(), deleteById(), etc.
// for free - no SQL, no DAO class needed. findByEmail()/existsByEmail() below are
// "derived query methods" - Spring Data reads the method NAME and generates the SQL
// (SELECT * FROM users WHERE email = ?) automatically.
public interface UserRepository extends JpaRepository<User, Integer> {

	Optional<User> findByEmail(String email);

	boolean existsByEmail(String email);
}
