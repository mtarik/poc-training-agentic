package fr.formation.shop.application.port.out;

import fr.formation.shop.domain.review.Review;

import java.util.List;

public interface ReviewRepository {

    Review save(Review review);

    List<Review> findByProductId(Long productId);

    boolean existsByProductIdAndCustomerId(Long productId, Long customerId);
}
