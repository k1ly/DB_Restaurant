package by.belstu.it.lyskov.dbrestaurant.controller.rest;

import by.belstu.it.lyskov.dbrestaurant.controller.mapper.DtoMapper;
import by.belstu.it.lyskov.dbrestaurant.dto.newdto.NewReviewDto;
import by.belstu.it.lyskov.dbrestaurant.dto.ReviewDto;
import by.belstu.it.lyskov.dbrestaurant.entity.Review;
import by.belstu.it.lyskov.dbrestaurant.exception.BadReferenceException;
import by.belstu.it.lyskov.dbrestaurant.exception.ServiceException;
import by.belstu.it.lyskov.dbrestaurant.service.ReviewService;
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
@RequestMapping("/api/reviews")
public class ReviewController {

    private final DtoMapper dtoMapper;
    private final ReviewService reviewService;

    public ReviewController(DtoMapper dtoMapper, ReviewService reviewService) {
        this.dtoMapper = dtoMapper;
        this.reviewService = reviewService;
    }

    @PostConstruct
    private void configure() {
        dtoMapper.addTypeMapping(Review.class, ReviewDto.class, Review::getUser, ReviewDto::setUser);
        dtoMapper.addTypeMapping(NewReviewDto.class, Review.class, NewReviewDto::getUser, Review::setUser);
    }

    @Operation(summary = "Find reviews")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved")
    })
    @GetMapping({"", "/"})
    public Page<ReviewDto> findReviews(@PageableDefault Pageable pageable) throws ServiceException {
        Page<Review> reviews = reviewService.getReviews(pageable);
        return new PageImpl<>(dtoMapper.mapAll(reviews.getContent(), ReviewDto.class), reviews.getPageable(), reviews.getTotalElements());
    }

    @Operation(summary = "Add a new review")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully added"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @PostMapping("/add")
    public void addReview(@Valid @RequestBody NewReviewDto reviewDto) throws BadReferenceException, ServiceException {
        reviewService.addReview(dtoMapper.map(reviewDto, Review.class));
    }
}
