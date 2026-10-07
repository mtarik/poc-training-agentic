package fr.formation.shop.adapter.in.web.review;

import fr.formation.shop.domain.review.Review;

import java.time.LocalDateTime;

public record ReviewResponse(Long id, Long productId, Long customerId, int rating, String comment,
                             LocalDateTime createdAt) {

    static ReviewResponse from(Review review) {
        return new ReviewResponse(review.getId(), review.getProductId(), review.getCustomerId(), review.getRating(),
                review.getComment(), review.getCreatedAt());
    }
}
