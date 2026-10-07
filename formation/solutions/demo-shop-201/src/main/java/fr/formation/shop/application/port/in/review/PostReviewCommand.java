package fr.formation.shop.application.port.in.review;

public record PostReviewCommand(Long productId, Long customerId, int rating, String comment) {
}
