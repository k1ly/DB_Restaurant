package by.belstu.it.lyskov.dbrestaurant.controller.rest;

import by.belstu.it.lyskov.dbrestaurant.controller.mapper.DtoMapper;
import by.belstu.it.lyskov.dbrestaurant.dto.OrderDto;
import by.belstu.it.lyskov.dbrestaurant.dto.updatedto.ConfirmOrderDto;
import by.belstu.it.lyskov.dbrestaurant.dto.updatedto.UpdateOrderDto;
import by.belstu.it.lyskov.dbrestaurant.entity.Order;
import by.belstu.it.lyskov.dbrestaurant.exception.BadReferenceException;
import by.belstu.it.lyskov.dbrestaurant.exception.OrderNotFoundException;
import by.belstu.it.lyskov.dbrestaurant.exception.ServiceException;
import by.belstu.it.lyskov.dbrestaurant.service.OrderService;
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
@RequestMapping("/api/orders")
public class OrderController {

    private final DtoMapper dtoMapper;
    private final OrderService orderService;

    public OrderController(DtoMapper dtoMapper, OrderService orderService) {
        this.dtoMapper = dtoMapper;
        this.orderService = orderService;
    }

    @PostConstruct
    private void configure() {
        dtoMapper.addTypeMapping(Order.class, OrderDto.class, Order::getAddress, OrderDto::setAddress);
        dtoMapper.addTypeMapping(Order.class, OrderDto.class, Order::getCustomer, OrderDto::setCustomer);
        dtoMapper.addTypeMapping(Order.class, OrderDto.class, Order::getManager, OrderDto::setManager);
        dtoMapper.addTypeMapping(ConfirmOrderDto.class, Order.class, ConfirmOrderDto::getAddress, Order::setAddress);
        dtoMapper.addTypeMapping(UpdateOrderDto.class, Order.class, UpdateOrderDto::getManager, Order::setManager);
    }

    @Operation(summary = "Find order by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @GetMapping("/{id}")
    public OrderDto findOrderById(@PathVariable Long id) throws OrderNotFoundException, ServiceException {
        return dtoMapper.map(orderService.findOrderById(id), OrderDto.class);
    }

    @Operation(summary = "Find orders")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @GetMapping({"", "/"})
    public Page<OrderDto> findOrders(@PageableDefault Pageable pageable, @RequestParam Long status,
                                     @RequestParam(required = false) Long customer) throws BadReferenceException, ServiceException {
        Page<Order> orders = customer != null ? orderService.findOrdersByStatusAndCustomer(status, customer, pageable)
                : orderService.findOrdersByStatus(status, pageable);
        return new PageImpl<>(dtoMapper.mapAll(orders.getContent(), OrderDto.class), orders.getPageable(), orders.getTotalElements());
    }

    @Operation(summary = "Confirm an existing order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @PutMapping("/confirm/{id}")
    public void confirmOrder(@PathVariable Long id, @Valid @RequestBody ConfirmOrderDto orderDto)
            throws OrderNotFoundException, BadReferenceException, ServiceException {
        orderService.confirmOrder(id, dtoMapper.map(orderDto, Order.class));
    }

    @Operation(summary = "Cancel an existing order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @PutMapping("/cancel/{id}")
    public void cancelOrder(@PathVariable Long id) throws OrderNotFoundException, ServiceException {
        orderService.cancelOrder(id);
    }

    @Operation(summary = "Update an existing order by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @PutMapping("/update/{id}")
    public void updateOrder(@PathVariable Long id, @Valid @RequestBody UpdateOrderDto orderDto)
            throws OrderNotFoundException, BadReferenceException, ServiceException {
        orderService.updateOrder(id, dtoMapper.map(orderDto, Order.class));
    }
}
