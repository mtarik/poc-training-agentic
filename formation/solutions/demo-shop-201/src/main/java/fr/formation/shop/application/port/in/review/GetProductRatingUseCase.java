package fr.formation.shop.application.port.in.review;

import fr.formation.shop.domain.review.ProductRating;

public interface GetProductRatingUseCase {

    ProductRating getRating(Long productId);
}
