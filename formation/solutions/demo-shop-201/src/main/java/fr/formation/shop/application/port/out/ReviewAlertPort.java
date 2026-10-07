package fr.formation.shop.application.port.out;

import fr.formation.shop.domain.review.Review;

/**
 * Notification du service qualité lorsqu'un avis négatif est déposé.
 */
public interface ReviewAlertPort {

    void notifyNegativeReview(Review review);
}
