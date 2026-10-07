package fr.formation.shop.domain.review;

import fr.formation.shop.domain.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReviewTest {

    @ParameterizedTest
    @ValueSource(ints = {0, 6, -1})
    void create_shouldRejectRating_whenOutOfRange(int rating) {
        assertThatThrownBy(() -> Review.create(1L, 2L, rating, "commentaire"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("entre 1 et 5");
    }

    @Test
    void create_shouldRejectComment_whenLongerThan500Characters() {
        String comment = "a".repeat(501);

        assertThatThrownBy(() -> Review.create(1L, 2L, 4, comment))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("500");
    }

    @Test
    void create_shouldAcceptReview_whenCommentIsAbsentOrAtLimit() {
        assertThat(Review.create(1L, 2L, 5, null).getComment()).isNull();
        assertThat(Review.create(1L, 2L, 5, "a".repeat(500)).getComment()).hasSize(500);
    }

    @ParameterizedTest
    @CsvSource({"1, true", "2, true", "3, false", "5, false"})
    void isNegative_shouldBeTrue_whenRatingIsTwoOrLess(int rating, boolean expected) {
        assertThat(Review.create(1L, 2L, rating, null).isNegative()).isEqualTo(expected);
    }
}
