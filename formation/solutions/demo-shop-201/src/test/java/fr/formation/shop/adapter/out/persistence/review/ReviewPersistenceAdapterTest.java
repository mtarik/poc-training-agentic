package fr.formation.shop.adapter.out.persistence.review;

import fr.formation.shop.domain.review.Review;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(ReviewPersistenceAdapter.class)
class ReviewPersistenceAdapterTest {

    private static final Long PRODUCT_ID = 9_001L;

    @Autowired
    private ReviewPersistenceAdapter adapter;

    @Test
    void save_shouldPersistReview_andFindItByProduct() {
        adapter.save(new Review(null, PRODUCT_ID, 1L, 2, "Bof", LocalDateTime.now().minusDays(1)));
        adapter.save(new Review(null, PRODUCT_ID, 2L, 5, "Super", LocalDateTime.now()));

        assertThat(adapter.findByProductId(PRODUCT_ID))
                .extracting(Review::getRating)
                .containsExactly(5, 2);
        assertThat(adapter.existsByProductIdAndCustomerId(PRODUCT_ID, 1L)).isTrue();
        assertThat(adapter.existsByProductIdAndCustomerId(PRODUCT_ID, 3L)).isFalse();
    }
}
