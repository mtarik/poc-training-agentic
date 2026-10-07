package fr.formation.shop.adapter.out.notification;

import fr.formation.shop.application.port.out.ReviewAlertPort;
import fr.formation.shop.domain.review.Review;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Implémentation provisoire : trace l'alerte dans les logs.
 * A remplacer par un adapter email ou Kafka sans modifier le domaine ni l'application.
 */
@Component
class LoggingReviewAlertAdapter implements ReviewAlertPort {

    private static final Logger log = LoggerFactory.getLogger(LoggingReviewAlertAdapter.class);

    @Override
    public void notifyNegativeReview(Review review) {
        log.warn("ALERTE QUALITE - avis négatif {}/5 sur le produit {} par le client {} : {}",
                review.getRating(), review.getProductId(), review.getCustomerId(), review.getComment());
    }
}
