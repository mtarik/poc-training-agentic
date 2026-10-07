package fr.formation.shop.adapter.out.persistence.review;

import fr.formation.shop.application.port.out.ReviewRepository;
import fr.formation.shop.domain.review.Review;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class ReviewPersistenceAdapter implements ReviewRepository {

    private final ReviewJpaRepository jpaRepository;

    ReviewPersistenceAdapter(ReviewJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Review save(Review review) {
        return jpaRepository.save(ReviewJpaEntity.fromDomain(review)).toDomain();
    }

    @Override
    public List<Review> findByProductId(Long productId) {
        return jpaRepository.findByProductIdOrderByCreatedAtDesc(productId).stream()
                .map(ReviewJpaEntity::toDomain)
                .toList();
    }

    @Override
    public boolean existsByProductIdAndCustomerId(Long productId, Long customerId) {
        return jpaRepository.existsByProductIdAndCustomerId(productId, customerId);
    }
}
