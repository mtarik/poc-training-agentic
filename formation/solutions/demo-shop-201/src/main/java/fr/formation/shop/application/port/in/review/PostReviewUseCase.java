package fr.formation.shop.application.port.in.review;

import fr.formation.shop.domain.review.Review;

public interface PostReviewUseCase {

    Review post(PostReviewCommand command);
}
