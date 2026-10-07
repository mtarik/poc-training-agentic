package fr.formation.shop.application.service;

import fr.formation.shop.application.port.in.review.GetProductRatingUseCase;
import fr.formation.shop.application.port.in.review.PostReviewCommand;
import fr.formation.shop.application.port.in.review.PostReviewUseCase;
import fr.formation.shop.application.port.out.CustomerRepository;
import fr.formation.shop.application.port.out.OrderRepository;
import fr.formation.shop.application.port.out.ProductRepository;
import fr.formation.shop.application.port.out.ReviewAlertPort;
import fr.formation.shop.application.port.out.ReviewRepository;
import fr.formation.shop.domain.exception.BusinessException;
import fr.formation.shop.domain.exception.NotFoundException;
import fr.formation.shop.domain.review.ProductRating;
import fr.formation.shop.domain.review.Review;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ReviewService implements PostReviewUseCase, GetProductRatingUseCase {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final ReviewAlertPort reviewAlertPort;

    public ReviewService(ReviewRepository reviewRepository, ProductRepository productRepository,
                         CustomerRepository customerRepository, OrderRepository orderRepository,
                         ReviewAlertPort reviewAlertPort) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
        this.reviewAlertPort = reviewAlertPort;
    }

    @Override
    @Transactional
    public Review post(PostReviewCommand command) {
        ensureProductExists(command.productId());
        if (customerRepository.findById(command.customerId()).isEmpty()) {
            throw new NotFoundException("Client", command.customerId());
        }
        if (!orderRepository.hasCustomerPurchasedProduct(command.customerId(), command.productId())) {
            throw new BusinessException("Le client %d n'a pas acheté le produit %d"
                    .formatted(command.customerId(), command.productId()));
        }
        if (reviewRepository.existsByProductIdAndCustomerId(command.productId(), command.customerId())) {
            throw new BusinessException("Le client %d a déjà donné un avis sur le produit %d"
                    .formatted(command.customerId(), command.productId()));
        }

        Review review = reviewRepository.save(
                Review.create(command.productId(), command.customerId(), command.rating(), command.comment()));
        if (review.isNegative()) {
            reviewAlertPort.notifyNegativeReview(review);
        }
        return review;
    }

    @Override
    public ProductRating getRating(Long productId) {
        ensureProductExists(productId);
        return ProductRating.of(productId, reviewRepository.findByProductId(productId));
    }

    private void ensureProductExists(Long productId) {
        if (productRepository.findById(productId).isEmpty()) {
            throw new NotFoundException("Produit", productId);
        }
    }
}
