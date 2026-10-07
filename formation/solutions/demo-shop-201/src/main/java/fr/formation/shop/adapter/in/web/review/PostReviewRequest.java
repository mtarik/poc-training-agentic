package fr.formation.shop.adapter.in.web.review;

import fr.formation.shop.application.port.in.review.PostReviewCommand;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PostReviewRequest(
        @NotNull Long customerId,
        @Min(1) @Max(5) int rating,
        @Size(max = 500) String comment) {

    PostReviewCommand toCommand(Long productId) {
        return new PostReviewCommand(productId, customerId, rating, comment);
    }
}
