package fr.formation.shop.adapter.in.web.review;

import fr.formation.shop.domain.review.ProductRating;

import java.math.BigDecimal;
import java.util.List;

public record ProductReviewsResponse(Long productId, BigDecimal averageRating, int count,
                                     List<ReviewResponse> reviews) {

    static ProductReviewsResponse from(ProductRating rating) {
        return new ProductReviewsResponse(rating.productId(), rating.averageRating(), rating.count(),
                rating.reviews().stream().map(ReviewResponse::from).toList());
    }
}
