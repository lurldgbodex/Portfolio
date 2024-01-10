package tech.sgcor.product.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import tech.sgcor.product.model.Category;

public interface CategoryRepository extends MongoRepository<Category, String> {
}
