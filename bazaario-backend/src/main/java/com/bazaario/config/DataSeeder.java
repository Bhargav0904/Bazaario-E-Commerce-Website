package com.bazaario.config;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.bazaario.entity.Category;
import com.bazaario.entity.Product;
import com.bazaario.entity.Role;
import com.bazaario.entity.User;
import com.bazaario.repository.CategoryRepository;
import com.bazaario.repository.ProductRepository;
import com.bazaario.repository.UserRepository;

/*
 * CommandLineRunner: Spring calls run() once, automatically, right after startup.
 * This seeds an admin account and a little sample data so you can log in and see
 * a working shop immediately instead of hand-writing INSERT statements.
 *
 * Every block is guarded by an existence check, so restarting the app won't create
 * duplicates. Delete this class (or the product block) once you have real data.
 */
@Component
public class DataSeeder implements CommandLineRunner {

	private final UserRepository userRepository;
	private final CategoryRepository categoryRepository;
	private final ProductRepository productRepository;
	private final PasswordEncoder passwordEncoder;

	public DataSeeder(UserRepository userRepository, CategoryRepository categoryRepository,
			ProductRepository productRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.categoryRepository = categoryRepository;
		this.productRepository = productRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public void run(String... args) {

		if (!userRepository.existsByEmail("admin@bazaario.com")) {
			User admin = new User();
			admin.setFullName("Store Admin");
			admin.setEmail("admin@bazaario.com");
			admin.setPassword(passwordEncoder.encode("admin123"));
			admin.setPhone("9000000000");
			admin.setAddress("Head Office");
			admin.setRole(Role.ADMIN);
			userRepository.save(admin);

			System.out.println(">>> Seeded admin account: admin@bazaario.com / admin123");
			System.out.println(">>> CHANGE THIS PASSWORD before showing the project to anyone.");
		}

		if (categoryRepository.count() == 0) {
			List<Category> categories = List.of(
					new Category("Electronics", "Gadgets, devices and accessories"),
					new Category("Clothing", "Apparel and footwear"),
					new Category("Home & Kitchen", "Everyday household items"),
					new Category("Books", "Fiction, non-fiction and textbooks")
			);
			categoryRepository.saveAll(categories);
			System.out.println(">>> Seeded " + categories.size() + " categories");
		}

		if (productRepository.count() == 0) {
			Category electronics = categoryRepository.findAll().get(0);
			Category clothing = categoryRepository.findAll().get(1);

			productRepository.saveAll(List.of(
					new Product("Wireless Mouse", "2.4GHz wireless mouse with silent clicks",
							new BigDecimal("699.00"), 50, electronics, null),
					new Product("Mechanical Keyboard", "Hot-swappable switches, white backlight",
							new BigDecimal("3499.00"), 20, electronics, null),
					new Product("Cotton T-Shirt", "Pre-shrunk, regular fit",
							new BigDecimal("499.00"), 100, clothing, null)
			));
			System.out.println(">>> Seeded 3 sample products");
		}
	}
}
