package com.bazaario.service;

import java.util.List;

import com.bazaario.dto.ProductRequest;
import com.bazaario.dto.ProductResponse;

public interface ProductService {
	List<ProductResponse> getAllProducts();
	ProductResponse getProductById(Integer id);
	List<ProductResponse> getProductsByCategory(Integer categoryId);
	List<ProductResponse> searchProducts(String keyword);
	ProductResponse addProduct(ProductRequest request);
	ProductResponse updateProduct(Integer id, ProductRequest request);
	void deleteProduct(Integer id);
}
