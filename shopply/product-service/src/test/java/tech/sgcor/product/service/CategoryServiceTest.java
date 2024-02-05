package tech.sgcor.product.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.sgcor.product.dto.CategoryDto;
import tech.sgcor.product.dto.CategoryUpdate;
import tech.sgcor.product.dto.CustomResponse;
import tech.sgcor.product.dto.UpdateProduct;
import tech.sgcor.product.exception.BadRequestException;
import tech.sgcor.product.exception.ResourceNotFoundException;
import tech.sgcor.product.model.Category;
import tech.sgcor.product.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    private CategoryService underTest;
    @Mock
    private CategoryRepository categoryRepository;

    @Test
    void getAllCategoriesTest() {
        // create category instance
        Category category = Category
                .builder()
                .name("Beverages")
                .description("drinks and beverages")
                .build();
        Category anotherCategory = Category
                .builder()
                .name("Automobiles")
                .description("transportation products such as cars, trucks, bicycle e.t.c")
                .build();

        when(categoryRepository.findAll()).thenReturn(List.of(category, anotherCategory));

        List<CategoryDto> res = underTest.getAllCategories();

        List<Category> categories = res.stream().map(cat -> Category
                .builder()
                .name(cat.getName())
                .description(cat.getDescription())
                .build()).toList();

        assertThat(res).isNotEmpty();
        assertThat(res).hasSize(2);
        assertThat(categories).contains(category, anotherCategory);
    }

    @Test
    void getCategoryById() {
        Category category = Category
                .builder()
                .id("id")
                .name("gadget")
                .description("gadgets")
                .build();

        when(categoryRepository.findById("id")).thenReturn(Optional.of(category));

        var res = underTest.getCategoryById("id");

        assertThat(res).isNotNull();
        assertThat(res.getId()).isEqualTo(category.getId());
        assertThat(res.getName()).isEqualTo(category.getName());
        assertThat(res.getDescription()).isEqualTo(category.getDescription());

        verify(categoryRepository, times(1)).findById("id");
    }

    @Test
    void getCategoryNotFoundTest() {
        assertThatThrownBy(()-> underTest.getCategoryById("invalidId"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("category not found with id");
    }

    @Test
    void createCategoryTest() {
        CategoryDto request = CategoryDto
                .builder()
                .name("category")
                .description("category description")
                .build();

        var res = underTest.createCategory(request);

        ArgumentCaptor<Category> categoryArgumentCaptor = ArgumentCaptor.forClass(Category.class);
        verify(categoryRepository, times(1)).save(categoryArgumentCaptor.capture());
    }

    @Test
    void updateCategoryTest() {
        Category category = Category
                .builder()
                .name("category")
                .description("description")
                .build();

        when(categoryRepository.findById("update")).thenReturn(Optional.of(category));

        CategoryUpdate updateRequest = new CategoryUpdate();
        updateRequest.setName("update category");
        updateRequest.setDescription("update category description");

        when(categoryRepository.save(any(Category.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        var res = underTest.updateCategory("update", updateRequest);


        assertThat(res.code()).isEqualTo(200);
        assertThat(res.message()).isEqualTo("category updated successfully");
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void updateCategoryNotFoundTest() {
        when(categoryRepository.findById("update")).thenReturn(Optional.empty());

        CategoryUpdate updateRequest = new CategoryUpdate();
        updateRequest.setName("update category");
        updateRequest.setDescription("update category description");

        assertThatThrownBy(()-> underTest.updateCategory("update", updateRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("category not found with id");
    }

    @Test
    void updateCategoryBlankFieldTest() {
        // Mock product
        var category = Category
                .builder()
                .name("name")
                .build();

        // Mock find product by id
        when(categoryRepository.findById("productId")).thenReturn(Optional.of(category));

        // create request to update
        CategoryUpdate request = new CategoryUpdate();

        assertThatThrownBy(()-> underTest.updateCategory("productId", request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("you need to provide the field to update");
    }

    @Test
    void deleteCategoryTest() {
        // Mock product
        Category category = Category
                .builder()
                .name("name")
                .build();

        // mock find by id
        when(categoryRepository.findById("Id")).thenReturn(Optional.of(category));

        // call delete method
        CustomResponse res = underTest.deleteCategory("Id");

        // verify and asserts
        verify(categoryRepository, times(1)).findById("Id");
        verify(categoryRepository).delete(category);
        assertThat(res.code()).isEqualTo(200);
        assertThat(res.message()).isEqualTo("category deleted successfully");
    }

    @Test
    void deleteProductNotFoundTest() {
        // mock find by id
        when(categoryRepository.findById("id")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.deleteCategory("id"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("category not found with id");
    }
}