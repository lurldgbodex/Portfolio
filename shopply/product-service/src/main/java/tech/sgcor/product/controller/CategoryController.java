package tech.sgcor.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.sgcor.product.dto.CategoryDto;
import tech.sgcor.product.dto.CategoryUpdate;
import tech.sgcor.product.dto.CustomResponse;
import tech.sgcor.product.service.CategoryService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryDto> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto getCategory(@PathVariable(name = "categoryId") String id) {
        return categoryService.getCategoryById(id);
    }

    @PostMapping("/create")
    public ResponseEntity<CustomResponse> createCategory(
            @RequestBody @Valid CategoryDto request) {
        return ResponseEntity.created(categoryService.createCategory(request))
                .body(new CustomResponse(201, "Category created successfully"));
    }

    @PutMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponse updateCategory(
            @PathVariable(name = "categoryId") String id, @RequestBody CategoryUpdate request) {
        return categoryService.updateCategory(id, request);
    }

    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponse deleteCategory(@PathVariable(name = "categoryId") String id) {
        return categoryService.deleteCategory(id);
    }
}
