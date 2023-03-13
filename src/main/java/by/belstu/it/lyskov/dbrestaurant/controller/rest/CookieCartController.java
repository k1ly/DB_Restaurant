package by.belstu.it.lyskov.dbrestaurant.controller.rest;

import by.belstu.it.lyskov.dbrestaurant.controller.cookie.CookieManager;
import by.belstu.it.lyskov.dbrestaurant.dto.CookieCartItemDto;
import by.belstu.it.lyskov.dbrestaurant.dto.DishDto;
import by.belstu.it.lyskov.dbrestaurant.dto.OrderItemDto;
import by.belstu.it.lyskov.dbrestaurant.exception.BadReferenceException;
import by.belstu.it.lyskov.dbrestaurant.exception.CartItemNotFoundException;
import by.belstu.it.lyskov.dbrestaurant.exception.ServiceException;
import by.belstu.it.lyskov.dbrestaurant.service.CookieCartService;
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

import javax.servlet.http.HttpServletResponse;
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
@RequestMapping("/api/cookie/cart")
public class CookieCartController {

    private final CookieManager cookieManager;
    private final CookieCartService cookieCartService;

    public CookieCartController(CookieManager cookieManager, CookieCartService cookieCartService) {
        this.cookieManager = cookieManager;
        this.cookieCartService = cookieCartService;
    }

    @Operation(summary = "Find cart item by dish")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @GetMapping("/{dish}")
    public OrderItemDto findCartItemByDish(@PathVariable Long dish, @CookieValue String cart)
            throws BadReferenceException, CartItemNotFoundException {
        if (cart == null)
            throw new BadReferenceException("No cart cookie was provided");
        CookieCartItemDto cartItemDto = cookieCartService.findCartItemByDish(cookieManager.parseList(cart, new CookieCartItemDto[]{}), dish);
        OrderItemDto orderItemDto = new OrderItemDto();
        orderItemDto.setQuantity(cartItemDto.getQuantity());
        DishDto dishDto = new DishDto();
        dishDto.setId(cartItemDto.getDish());
        orderItemDto.setDish(dishDto);
        return orderItemDto;

    }

    @Operation(summary = "Find cart items")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved")
    })
    @GetMapping({"", "/"})
    public Page<OrderItemDto> findCartItems(@PageableDefault Pageable pageable,
                                            @CookieValue(required = false) String cart) {
        List<CookieCartItemDto> cartItems = cart != null ? cookieManager.parseList(cart, new CookieCartItemDto[]{}) : new ArrayList<>();
        List<OrderItemDto> list = cookieCartService.getCartItems(cartItems, pageable).stream().map(c -> {
            OrderItemDto orderItemDto = new OrderItemDto();
            orderItemDto.setQuantity(c.getQuantity());
            DishDto dishDto = new DishDto();
            dishDto.setId(c.getDish());
            orderItemDto.setDish(dishDto);
            return orderItemDto;
        }).toList();
        return new PageImpl<>(list, pageable, cartItems.size());
    }

    @Operation(summary = "Add a new cart item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully added"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @PostMapping("/add")
    public void addCartItem(@Valid @RequestBody CookieCartItemDto cartItemDto,
                            @CookieValue(required = false) String cart,
                            HttpServletResponse response) throws BadReferenceException, ServiceException {
        List<CookieCartItemDto> cartItems = cart != null ? cookieManager.parseList(cart, new CookieCartItemDto[]{}) : new ArrayList<>();
        try {
            CookieCartItemDto cartItem = cookieCartService.findCartItemByDish(cartItems, cartItemDto.getDish());
            cartItemDto.setQuantity(cartItem.getQuantity() + cartItemDto.getQuantity());
            cookieCartService.updateCartItemByDish(cartItems, cartItem.getDish(), cartItemDto);
        } catch (CartItemNotFoundException e) {
            cookieCartService.addCartItem(cartItems, cartItemDto);
        }
        response.addCookie(cookieManager.create("cart", null, 0));
        response.addCookie(cookieManager.create("cart", cartItems));
    }

    @Operation(summary = "Update an existing cart item by dish")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @PutMapping("/update/{dish}")
    public void updateCookieCartItem(@PathVariable Long dish, @Valid @RequestBody CookieCartItemDto cartItemDto,
                                     @CookieValue String cart, HttpServletResponse response) throws BadReferenceException, CartItemNotFoundException {
        if (cart == null)
            throw new BadReferenceException("No cart cookie was provided");
        List<CookieCartItemDto> cartItems = cookieManager.parseList(cart, new CookieCartItemDto[]{});
        cookieCartService.updateCartItemByDish(cartItems, dish, cartItemDto);
        response.addCookie(cookieManager.create("cart", null, 0));
        response.addCookie(cookieManager.create("cart", cartItems));
    }

    @Operation(summary = "Delete an existing cart item by dish")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @DeleteMapping("/delete/{dish}")
    public void deleteCookieCartItem(@PathVariable Long dish, @CookieValue String cart,
                                     HttpServletResponse response) throws BadReferenceException, CartItemNotFoundException {
        if (cart == null)
            throw new BadReferenceException("No cart cookie was provided");
        List<CookieCartItemDto> cartItems = cookieManager.parseList(cart, new CookieCartItemDto[]{});
        cookieCartService.deleteCartItemByDish(cartItems, dish);
        response.addCookie(cookieManager.create("cart", null, 0));
        response.addCookie(cookieManager.create("cart", cartItems));
    }
}
