package fr.formation.shop.adapter.out.persistence.review;

import fr.formation.shop.domain.review.Review;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;

@Entity
@Table(name = "review", uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "customer_id"}))
public class ReviewJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    private int rating;

    @Column(length = Review.MAX_COMMENT_LENGTH)
    private String comment;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected ReviewJpaEntity() {
    }

    static ReviewJpaEntity fromDomain(Review review) {
        ReviewJpaEntity entity = new ReviewJpaEntity();
        entity.id = review.getId();
        entity.productId = review.getProductId();
        entity.customerId = review.getCustomerId();
        entity.rating = review.getRating();
        entity.comment = review.getComment();
        entity.createdAt = review.getCreatedAt();
        return entity;
    }

    Review toDomain() {
        return new Review(id, productId, customerId, rating, comment, createdAt);
    }
}
