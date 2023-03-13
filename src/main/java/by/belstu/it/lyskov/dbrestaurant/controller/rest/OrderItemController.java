package by.belstu.it.lyskov.dbrestaurant.controller.rest;

import by.belstu.it.lyskov.dbrestaurant.controller.mapper.DtoMapper;
import by.belstu.it.lyskov.dbrestaurant.dto.newdto.NewOrderItemDto;
import by.belstu.it.lyskov.dbrestaurant.dto.OrderItemDto;
import by.belstu.it.lyskov.dbrestaurant.dto.updatedto.UpdateOrderItemDto;
import by.belstu.it.lyskov.dbrestaurant.entity.OrderItem;
import by.belstu.it.lyskov.dbrestaurant.exception.BadReferenceException;
import by.belstu.it.lyskov.dbrestaurant.exception.OrderItemNotFoundException;
import by.belstu.it.lyskov.dbrestaurant.exception.ServiceException;
import by.belstu.it.lyskov.dbrestaurant.service.OrderItemService;
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

@OpenAPIDefinition(info = @Info(title = "Restaurant API", version = "1.0", description = "Restaurant REST API"))
@ApiResponses(value = {
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "500", description = "Internal Server error")
})
@RestController
@RequestMapping("/api/order-items")
public class OrderItemController {

    private final DtoMapper dtoMapper;
    private final OrderItemService orderItemService;

    public OrderItemController(DtoMapper dtoMapper, OrderItemService orderItemService) {
        this.dtoMapper = dtoMapper;
        this.orderItemService = orderItemService;
    }

    @PostConstruct
    private void configure() {
        dtoMapper.addTypeMapping(OrderItem.class, OrderItemDto.class, OrderItem::getDish, OrderItemDto::setDish);
        dtoMapper.addTypeMapping(OrderItem.class, OrderItemDto.class, OrderItem::getOrder, OrderItemDto::setOrder);
        dtoMapper.addTypeMapping(NewOrderItemDto.class, OrderItem.class, NewOrderItemDto::getDish, OrderItem::setDish);
        dtoMapper.addTypeMapping(NewOrderItemDto.class, OrderItem.class, NewOrderItemDto::getOrder, OrderItem::setOrder);
    }

    @Operation(summary = "Find order item by order and dish")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @GetMapping("/find")
    public OrderItemDto findOrderItemByOrderAndDish(@RequestParam Long order, @RequestParam Long dish)
            throws BadReferenceException, OrderItemNotFoundException, ServiceException {
        return dtoMapper.map(orderItemService.findOrderItemByOrderAndDish(order, dish), OrderItemDto.class);
    }

    @Operation(summary = "Find order items by order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @GetMapping({"", "/"})
    public Page<OrderItemDto> findOrderItemsByOrder(@PageableDefault Pageable pageable, @RequestParam Long order) throws BadReferenceException, ServiceException {
        Page<OrderItem> orderItems = orderItemService.findOrderItemsByOrder(order, pageable);
        return new PageImpl<>(dtoMapper.mapAll(orderItems.getContent(), OrderItemDto.class), orderItems.getPageable(), orderItems.getTotalElements());
    }

    @Operation(summary = "Get order items total price by order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @GetMapping("/total")
    public double countOrderItemTotalPrice(@RequestParam Long order) throws BadReferenceException, ServiceException {
        return orderItemService.countOrderItemTotalPrice(order);
    }

    @Operation(summary = "Add a new order item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully added"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @PostMapping("/add")
    public void addOrderItem(@Valid @RequestBody NewOrderItemDto orderItemDto) throws BadReferenceException, ServiceException {
        try {
            OrderItem orderItem = orderItemService.findOrderItemByOrderAndDish(orderItemDto.getOrder().getId(), orderItemDto.getDish().getId());
            orderItemDto.setQuantity(orderItem.getQuantity() + orderItemDto.getQuantity());
            orderItemService.updateOrderItem(orderItem.getId(), dtoMapper.map(orderItemDto, OrderItem.class));
        } catch (OrderItemNotFoundException e) {
            orderItemService.addOrderItem(dtoMapper.map(orderItemDto, OrderItem.class));
        }
    }

    @Operation(summary = "Update an existing order item by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @PutMapping("/update/{id}")
    public void updateOrderItem(@PathVariable Long id, @Valid @RequestBody UpdateOrderItemDto orderItemDto)
            throws OrderItemNotFoundException, ServiceException {
        orderItemService.updateOrderItem(id, dtoMapper.map(orderItemDto, OrderItem.class));
    }

    @Operation(summary = "Delete an existing order item by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @DeleteMapping("/delete/{id}")
    public void deleteOrderItem(@PathVariable Long id) throws OrderItemNotFoundException, ServiceException {
        orderItemService.deleteOrderItem(id);
    }
}
