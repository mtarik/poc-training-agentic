package fr.formation.shop.adapter.in.web.review;

import fr.formation.shop.application.port.in.review.GetProductRatingUseCase;
import fr.formation.shop.application.port.in.review.PostReviewUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products/{productId}/reviews")
public class ReviewController {

    private final PostReviewUseCase postReviewUseCase;
    private final GetProductRatingUseCase getProductRatingUseCase;

    public ReviewController(PostReviewUseCase postReviewUseCase, GetProductRatingUseCase getProductRatingUseCase) {
        this.postReviewUseCase = postReviewUseCase;
        this.getProductRatingUseCase = getProductRatingUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewResponse post(@PathVariable Long productId, @Valid @RequestBody PostReviewRequest request) {
        return ReviewResponse.from(postReviewUseCase.post(request.toCommand(productId)));
    }

    @GetMapping
    public ProductReviewsResponse getReviews(@PathVariable Long productId) {
        return ProductReviewsResponse.from(getProductRatingUseCase.getRating(productId));
    }
}
