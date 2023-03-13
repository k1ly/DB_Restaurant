package by.belstu.it.lyskov.dbrestaurant.controller.rest;

import by.belstu.it.lyskov.dbrestaurant.controller.mapper.DtoMapper;
import by.belstu.it.lyskov.dbrestaurant.dto.CategoryDto;
import by.belstu.it.lyskov.dbrestaurant.dto.newdto.NewCategoryDto;
import by.belstu.it.lyskov.dbrestaurant.entity.Category;
import by.belstu.it.lyskov.dbrestaurant.exception.CategoryNotFoundException;
import by.belstu.it.lyskov.dbrestaurant.exception.ServiceException;
import by.belstu.it.lyskov.dbrestaurant.service.CategoryService;
import by.belstu.it.lyskov.dbrestaurant.util.page.Page;
import by.belstu.it.lyskov.dbrestaurant.util.page.PageImpl;
import by.belstu.it.lyskov.dbrestaurant.util.page.Pageable;
import by.belstu.it.lyskov.dbrestaurant.util.page.PageableDefault;
import by.belstu.it.lyskov.dbrestaurant.util.sort.Sort;
import by.belstu.it.lyskov.dbrestaurant.util.sort.SortDefault;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@OpenAPIDefinition(info = @Info(title = "Restaurant API", version = "1.0", description = "Restaurant REST API"))
@ApiResponses(value = {
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "500", description = "Internal Server error")
})
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final DtoMapper dtoMapper;
    private final CategoryService categoryService;

    public CategoryController(DtoMapper dtoMapper, CategoryService categoryService) {
        this.dtoMapper = dtoMapper;
        this.categoryService = categoryService;
    }

    @Operation(summary = "Find category by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @GetMapping("/{id}")
    public CategoryDto findCategoryById(@PathVariable Long id) throws CategoryNotFoundException, ServiceException {
        return dtoMapper.map(categoryService.findCategoryById(id), CategoryDto.class);
    }

    @Operation(summary = "Get list of categories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved")
    })
    @GetMapping("/all")
    public List<CategoryDto> getCategories(@SortDefault Sort sort) throws ServiceException {
        return dtoMapper.mapAll(categoryService.getCategories(sort), CategoryDto.class);
    }

    @Operation(summary = "Find categories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved")
    })
    @GetMapping({"", "/"})
    public Page<CategoryDto> findCategories(@PageableDefault Pageable pageable, @RequestParam(required = false) String filter) throws ServiceException {
        Page<Category> categories = filter != null ? categoryService.findCategoriesByFilter(filter, pageable) : categoryService.getCategories(pageable);
        return new PageImpl<>(dtoMapper.mapAll(categories.getContent(), CategoryDto.class), categories.getPageable(), categories.getTotalElements());
    }

    @Operation(summary = "Add a new category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully added")
    })
    @PostMapping("/add")
    public void addCategory(@Valid @RequestBody NewCategoryDto categoryDto) throws ServiceException {
        categoryService.addCategory(dtoMapper.map(categoryDto, Category.class));
    }

    @Operation(summary = "Update an existing category by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @PutMapping("/update/{id}")
    public void updateCategory(@PathVariable Long id, @Valid @RequestBody NewCategoryDto categoryDto) throws CategoryNotFoundException, ServiceException {
        categoryService.updateCategory(id, dtoMapper.map(categoryDto, Category.class));
    }

    @Operation(summary = "Delete an existing category by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @DeleteMapping("/delete/{id}")
    public void deleteCategory(@PathVariable Long id) throws CategoryNotFoundException, ServiceException {
        categoryService.deleteCategory(id);
    }
}
