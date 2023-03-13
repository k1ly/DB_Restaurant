package by.belstu.it.lyskov.dbrestaurant.controller.rest;

import by.belstu.it.lyskov.dbrestaurant.controller.mapper.DtoMapper;
import by.belstu.it.lyskov.dbrestaurant.dto.DishDto;
import by.belstu.it.lyskov.dbrestaurant.dto.newdto.NewDishDto;
import by.belstu.it.lyskov.dbrestaurant.entity.Dish;
import by.belstu.it.lyskov.dbrestaurant.exception.BadReferenceException;
import by.belstu.it.lyskov.dbrestaurant.exception.DishNotFoundException;
import by.belstu.it.lyskov.dbrestaurant.exception.ServiceException;
import by.belstu.it.lyskov.dbrestaurant.service.DishImageService;
import by.belstu.it.lyskov.dbrestaurant.service.DishService;
import by.belstu.it.lyskov.dbrestaurant.util.page.Page;
import by.belstu.it.lyskov.dbrestaurant.util.page.PageImpl;
import by.belstu.it.lyskov.dbrestaurant.util.page.Pageable;
import by.belstu.it.lyskov.dbrestaurant.util.page.PageableDefault;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.validation.Valid;

@OpenAPIDefinition(info = @Info(title = "Restaurant API", version = "1.0", description = "Restaurant REST API"))
@ApiResponses(value = {
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "500", description = "Internal Server error")
})
@RestController
@RequestMapping("/api/dishes")
public class DishController {

    private final DtoMapper dtoMapper;
    private final DishService dishService;
    private final DishImageService dishImageService;

    public DishController(DtoMapper dtoMapper, DishService dishService, DishImageService dishImageService) {
        this.dtoMapper = dtoMapper;
        this.dishService = dishService;
        this.dishImageService = dishImageService;
    }

    @PostConstruct
    private void configure() {
        dtoMapper.addTypeMapping(Dish.class, DishDto.class, Dish::getCategory, DishDto::setCategory);
        dtoMapper.addTypeMapping(NewDishDto.class, Dish.class, NewDishDto::getCategory, Dish::setCategory);
    }

    @Operation(summary = "Find dish by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @GetMapping("/{id}")
    public DishDto findDishById(@PathVariable Long id) throws DishNotFoundException, ServiceException {
        return dtoMapper.map(dishService.findDishById(id), DishDto.class);
    }

    @Operation(summary = "Find dishes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @GetMapping({"", "/"})
    public Page<DishDto> findDishes(@PageableDefault Pageable pageable, @RequestParam(required = false) Long category,
                                    @RequestParam(required = false) String filter) throws BadReferenceException, ServiceException {
        Page<Dish> dishes = filter != null ? dishService.findDishesByFilter(filter, pageable) :
                (category != null ? dishService.findDishesByCategory(category, pageable) : dishService.getDishes(pageable));
        return new PageImpl<>(dtoMapper.mapAll(dishes.getContent(), DishDto.class), dishes.getPageable(), dishes.getTotalElements());
    }

    @Operation(summary = "Add a new dish")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully added"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @PostMapping("/add")
    public void addDish(@Valid @RequestBody NewDishDto dishDto)
            throws BadReferenceException, ServiceException {
        dishService.addDish(dtoMapper.map(dishDto, Dish.class));
    }

    @Operation(summary = "Update an existing dish by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @PutMapping("/update/{id}")
    public void updateDish(@PathVariable Long id, @Valid @RequestBody NewDishDto dishDto)
            throws DishNotFoundException, BadReferenceException, ServiceException {
        dishService.updateDish(id, dtoMapper.map(dishDto, Dish.class));
    }

    @Operation(summary = "Delete an existing dish by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @DeleteMapping("/delete/{id}")
    public void deleteDish(@PathVariable Long id) throws DishNotFoundException, ServiceException {
        dishService.deleteDish(id);
    }

    @Operation(summary = "Save dish image")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully added"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @PostMapping("/image")
    public String saveImage(@RequestBody MultipartFile image) throws ServiceException {
        return dishImageService.saveFile(image);
    }
}
