package fr.formation.shop.domain.review;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Synthèse des avis d'un produit.
 *
 * @param averageRating note moyenne arrondie à 1 décimale, null s'il n'y a aucun avis
 */
public record ProductRating(Long productId, BigDecimal averageRating, int count, List<Review> reviews) {

    public static ProductRating of(Long productId, List<Review> reviews) {
        if (reviews.isEmpty()) {
            return new ProductRating(productId, null, 0, List.of());
        }
        int sum = reviews.stream().mapToInt(Review::getRating).sum();
        BigDecimal average = BigDecimal.valueOf(sum)
                .divide(BigDecimal.valueOf(reviews.size()), 1, RoundingMode.HALF_UP);
        return new ProductRating(productId, average, reviews.size(), List.copyOf(reviews));
    }
}
