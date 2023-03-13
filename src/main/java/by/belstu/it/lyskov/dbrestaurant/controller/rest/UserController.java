package by.belstu.it.lyskov.dbrestaurant.controller.rest;

import by.belstu.it.lyskov.dbrestaurant.controller.cookie.CookieManager;
import by.belstu.it.lyskov.dbrestaurant.controller.mapper.DtoMapper;
import by.belstu.it.lyskov.dbrestaurant.dto.CookieCartItemDto;
import by.belstu.it.lyskov.dbrestaurant.dto.newdto.NewUserDto;
import by.belstu.it.lyskov.dbrestaurant.dto.UserDto;
import by.belstu.it.lyskov.dbrestaurant.dto.updatedto.EditUserDto;
import by.belstu.it.lyskov.dbrestaurant.dto.updatedto.UpdateUserDto;
import by.belstu.it.lyskov.dbrestaurant.entity.Dish;
import by.belstu.it.lyskov.dbrestaurant.entity.Order;
import by.belstu.it.lyskov.dbrestaurant.entity.OrderItem;
import by.belstu.it.lyskov.dbrestaurant.entity.User;
import by.belstu.it.lyskov.dbrestaurant.exception.*;
import by.belstu.it.lyskov.dbrestaurant.service.UserService;
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

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@OpenAPIDefinition(info = @Info(title = "Restaurant API", version = "1.0", description = "Restaurant REST API"))
@ApiResponses(value = {
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "500", description = "Internal Server error")
})
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final CookieManager cookieManager;
    private final DtoMapper dtoMapper;
    private final UserService userService;

    public UserController(CookieManager cookieManager, DtoMapper dtoMapper, UserService userService) {
        this.cookieManager = cookieManager;
        this.dtoMapper = dtoMapper;
        this.userService = userService;
    }

    @PostConstruct
    private void configure() {
        dtoMapper.addTypeMapping(User.class, UserDto.class, User::getOrder, UserDto::setOrder);
    }

    @Operation(summary = "Find user by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @GetMapping("/{id}")
    public UserDto findUserById(@PathVariable Long id) throws UserNotFoundException, ServiceException {
        return dtoMapper.map(userService.findUserById(id), UserDto.class);
    }

    @Operation(summary = "Find users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved")
    })
    @GetMapping({"", "/"})
    public Page<UserDto> findUsers(@PageableDefault Pageable pageable, @RequestParam(required = false) String filter) throws ServiceException {
        Page<User> users = filter != null ? userService.findUsersByFilter(filter, pageable) : userService.getUsers(pageable);
        return new PageImpl<>(dtoMapper.mapAll(users.getContent(), UserDto.class), users.getPageable(), users.getTotalElements());
    }

    @Operation(summary = "Register a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully added"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @PostMapping("/register")
    public void registerUser(@Valid @RequestBody NewUserDto userDto, @CookieValue(required = false) String cart) throws UserAlreadyExistsException, ServiceException {
        User user = dtoMapper.map(userDto, User.class);
        if (cart != null) {
            Order order = new Order();
            List<OrderItem> orderItems = new ArrayList<>();
            cookieManager.parseList(cart, new CookieCartItemDto[]{}).forEach(o -> {
                OrderItem orderItem = new OrderItem();
                orderItem.setQuantity(o.getQuantity());
                Dish dish = new Dish();
                dish.setId(o.getDish());
                orderItem.setDish(dish);
                orderItems.add(orderItem);
            });
            order.setOrderItems(orderItems);
            user.setOrder(order);
        }
        userService.registerUser(user);
    }

    @Operation(summary = "Update an existing user by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @PutMapping("/update/{id}")
    public void updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserDto userDto)
            throws UserNotFoundException, BadReferenceException, ServiceException {
        userService.updateUser(id, dtoMapper.map(userDto, User.class));
    }

    @Operation(summary = "Edit an existing user by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @PutMapping("/edit/{id}")
    public void editUser(@PathVariable Long id, @Valid @RequestBody EditUserDto userDto)
            throws UserNotFoundException, BadReferenceException, ServiceException {
        userService.editUser(id, dtoMapper.map(userDto, User.class));
    }
}
