package fr.formation.shop.domain.review;

import fr.formation.shop.domain.exception.BusinessException;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Avis d'un client sur un produit qu'il a acheté.
 */
public class Review {

    public static final int MIN_RATING = 1;
    public static final int MAX_RATING = 5;
    public static final int MAX_COMMENT_LENGTH = 500;
    private static final int NEGATIVE_THRESHOLD = 2;

    private final Long id;
    private final Long productId;
    private final Long customerId;
    private final int rating;
    private final String comment;
    private final LocalDateTime createdAt;

    public Review(Long id, Long productId, Long customerId, int rating, String comment, LocalDateTime createdAt) {
        if (rating < MIN_RATING || rating > MAX_RATING) {
            throw new BusinessException("La note doit être comprise entre %d et %d".formatted(MIN_RATING, MAX_RATING));
        }
        if (comment != null && comment.length() > MAX_COMMENT_LENGTH) {
            throw new BusinessException("Le commentaire ne doit pas dépasser %d caractères".formatted(MAX_COMMENT_LENGTH));
        }
        this.id = id;
        this.productId = Objects.requireNonNull(productId);
        this.customerId = Objects.requireNonNull(customerId);
        this.rating = rating;
        this.comment = comment;
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    public static Review create(Long productId, Long customerId, int rating, String comment) {
        return new Review(null, productId, customerId, rating, comment, LocalDateTime.now());
    }

    public boolean isNegative() {
        return rating <= NEGATIVE_THRESHOLD;
    }

    public Long getId() {
        return id;
    }

    public Long getProductId() {
        return productId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
