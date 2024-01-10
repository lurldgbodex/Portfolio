package tech.sgcor.product.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import tech.sgcor.product.model.Product;

import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product>findByCategoryId(String categoryId);
}