package by.belstu.it.lyskov.dbrestaurant.controller.rest;

import by.belstu.it.lyskov.dbrestaurant.controller.mapper.DtoMapper;
import by.belstu.it.lyskov.dbrestaurant.dto.AddressDto;
import by.belstu.it.lyskov.dbrestaurant.dto.newdto.NewAddressDto;
import by.belstu.it.lyskov.dbrestaurant.entity.Address;
import by.belstu.it.lyskov.dbrestaurant.exception.AddressNotFoundException;
import by.belstu.it.lyskov.dbrestaurant.exception.BadReferenceException;
import by.belstu.it.lyskov.dbrestaurant.exception.ServiceException;
import by.belstu.it.lyskov.dbrestaurant.service.AddressService;
import by.belstu.it.lyskov.dbrestaurant.util.sort.Sort;
import by.belstu.it.lyskov.dbrestaurant.util.sort.SortDefault;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.util.List;

@OpenAPIDefinition(info = @Info(title = "Restaurant API", version = "1.0", description = "Restaurant REST API"))
@ApiResponses(value = {
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "500", description = "Internal Server error")
})
@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    private final DtoMapper dtoMapper;
    private final AddressService addressService;

    public AddressController(DtoMapper dtoMapper, AddressService addressService) {
        this.dtoMapper = dtoMapper;
        this.addressService = addressService;
    }

    @PostConstruct
    private void configure() {
        dtoMapper.addTypeMapping(Address.class, AddressDto.class, Address::getUser, AddressDto::setUser);
        dtoMapper.addTypeMapping(NewAddressDto.class, Address.class, NewAddressDto::getUser, Address::setUser);
    }

    @Operation(summary = "Find address by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @GetMapping("/{id}")
    public AddressDto findAddressById(@PathVariable Long id) throws AddressNotFoundException, ServiceException {
        return dtoMapper.map(addressService.findAddressById(id), AddressDto.class);
    }

    @Operation(summary = "Find addresses by user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @GetMapping({"", "/"})
    public List<AddressDto> findAddressesByUser(@SortDefault Sort sort, @RequestParam Long user) throws BadReferenceException, ServiceException {
        return dtoMapper.mapAll(addressService.findAddressesByUser(user, sort), AddressDto.class);
    }

    @Operation(summary = "Add a new address")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully added"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @PostMapping("/add")
    public void addAddress(@Valid @RequestBody NewAddressDto addressDto) throws BadReferenceException, ServiceException {
        addressService.addAddress(dtoMapper.map(addressDto, Address.class));
    }
}
