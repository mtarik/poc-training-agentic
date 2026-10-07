package fr.formation.shop.domain.review;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ProductRatingTest {

    @Test
    void of_shouldHaveNullAverage_whenNoReview() {
        ProductRating rating = ProductRating.of(1L, List.of());

        assertThat(rating.averageRating()).isNull();
        assertThat(rating.count()).isZero();
    }

    @Test
    void of_shouldRoundAverageHalfUpToOneDecimal() {
        List<Review> reviews = List.of(
                Review.create(1L, 1L, 5, null),
                Review.create(1L, 2L, 4, null),
                Review.create(1L, 3L, 4, null),
                Review.create(1L, 4L, 4, null));

        ProductRating rating = ProductRating.of(1L, reviews);

        assertThat(rating.averageRating()).isEqualByComparingTo("4.3");
        assertThat(rating.count()).isEqualTo(4);
    }
}
