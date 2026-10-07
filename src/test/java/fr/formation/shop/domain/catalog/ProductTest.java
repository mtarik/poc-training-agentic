package fr.formation.shop.domain.catalog;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ProductTest {

    @Test
    void discountedPrice_shouldApplyPromotion_whenProductIsOnPromotion() {
        Product product = new Product(1L, "BOO-0003", "Effective Java", Category.BOOKS, new BigDecimal("45.00"), 20,
                800, 15);

        assertThat(product.isOnPromotion()).isTrue();
        assertThat(product.discountedPrice()).isEqualByComparingTo("36.00");
    }

    @Test
    void discountedPrice_shouldReturnPrice_whenNoPromotion() {
        Product product = new Product(1L, "BOO-0001", "Clean Code", Category.BOOKS, new BigDecimal("32.50"), null,
                600, 25);

        assertThat(product.isOnPromotion()).isFalse();
        assertThat(product.discountedPrice()).isEqualByComparingTo("32.50");
    }

    @Test
    void discountedPrice_shouldRoundHalfUp() {
        Product product = new Product(1L, "ELE-0002", "Casque audio", Category.ELECTRONICS, new BigDecimal("129.00"),
                15, 350, 5);

        assertThat(product.discountedPrice()).isEqualByComparingTo("109.65");
    }
}
