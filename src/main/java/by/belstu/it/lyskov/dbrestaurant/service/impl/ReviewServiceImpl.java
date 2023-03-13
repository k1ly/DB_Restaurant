package by.belstu.it.lyskov.dbrestaurant.service.impl;

import by.belstu.it.lyskov.dbrestaurant.entity.Review;
import by.belstu.it.lyskov.dbrestaurant.exception.BadReferenceException;
import by.belstu.it.lyskov.dbrestaurant.exception.RepositoryException;
import by.belstu.it.lyskov.dbrestaurant.exception.ServiceException;
import by.belstu.it.lyskov.dbrestaurant.repository.ReviewRepository;
import by.belstu.it.lyskov.dbrestaurant.repository.UserRepository;
import by.belstu.it.lyskov.dbrestaurant.service.ReviewService;
import by.belstu.it.lyskov.dbrestaurant.util.page.Page;
import by.belstu.it.lyskov.dbrestaurant.util.page.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Page<Review> getReviews(Pageable pageable) throws ServiceException {
        try {
            return reviewRepository.findAll(pageable);
        } catch (RepositoryException e) {
            throw new ServiceException("Failed to get reviews", e);
        }
    }

    @Override
    public void addReview(Review review) throws BadReferenceException, ServiceException {
        try {
            if (!userRepository.existsById(review.getUser().getId()))
                throw new BadReferenceException("User with id \"" + review.getUser().getId() + "\" doesn't exist!");
            reviewRepository.save(review);
        } catch (RepositoryException e) {
            throw new ServiceException("Failed to add review", e);
        }
    }
}
