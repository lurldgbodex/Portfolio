package tech.sgcor.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import tech.sgcor.product.dto.CategoryDto;
import tech.sgcor.product.dto.CategoryUpdate;
import tech.sgcor.product.dto.CustomResponse;
import tech.sgcor.product.exception.BadRequestException;
import tech.sgcor.product.exception.ResourceNotFoundException;
import tech.sgcor.product.model.Category;
import tech.sgcor.product.repository.CategoryRepository;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    /**
     * description: get all categories
     *
     * @return list of all categories
     */
    public List<CategoryDto> getAllCategories() {
        List<Category> category = categoryRepository.findAll();

        return category.stream().map(this::mapToCategoryDto).toList();
    }

    /**
     * description: helper method to map category to categoryDto
     *
     * @param category: category to map to categoryDto
     * @return categoryDto of mapped category
     */
    private CategoryDto mapToCategoryDto(Category category) {
        return CategoryDto
                .builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }

    /**
     * description: get category by id
     *
     * @param categoryId: id of the category to get
     * @return category of the id
     */
    public CategoryDto getCategoryById(String categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("category not found with id"));

        return CategoryDto
                .builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }

    /**
     * description: create category and save in database
     *
     * @param categoryDto: request body for creating category
     * @return location of the created category
     */
    public URI createCategory(CategoryDto categoryDto) {
        // create category entity
        Category category = Category
                .builder()
                .name(categoryDto.getName())
                .description(categoryDto.getDescription())
                .build();

        // save create category to database
        categoryRepository.save(category);
        String urlPath = "/api/categories/" + category.getId();
        return UriComponentsBuilder.fromPath(urlPath).build().toUri();
    }

    /**
     * description: update a category based on id.
     *
     * @param categoryId: id of the category to update
     * @param request: request body for updating category
     * @return customResponse of 200 status and success message if successful otherwise throws exception
     */
    public CustomResponse updateCategory(String categoryId, CategoryUpdate request) {
        // retrieve the category to update from database
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("category not found with id"));

        // check if there is a field to update
        boolean allFieldsBlank = Stream.of(
                Objects.toString(request.getName(), ""),
                Objects.toString(request.getDescription(), "")
        ).allMatch(String::isBlank);

        if (allFieldsBlank) {
            throw new BadRequestException("you need to provide the field to update");
        }

        category.setName(request.getName() != null ? request.getName() : category.getName());
        category.setDescription(request.getDescription() != null ? request.getDescription() : category.getDescription());

        //save the updated category to database and return a success message
        categoryRepository.save(category);

        return new CustomResponse(200, "category updated successfully");
    }

    /**
     * description: delete a category
     *
     * @param categoryId: id of the category to delete
     * @return customResponse with status 200 and success message if successful else throws exception
     */
    public CustomResponse deleteCategory(String categoryId) {
        // retrieve category to delete. throw exception if not found
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(()-> new ResourceNotFoundException("category not found with id"));

        // delete the category and return a success message
        categoryRepository.delete(category);
        return new CustomResponse(200, "category deleted successfully");
    }
}
