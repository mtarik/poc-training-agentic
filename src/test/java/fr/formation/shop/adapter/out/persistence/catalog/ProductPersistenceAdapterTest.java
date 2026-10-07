package fr.formation.shop.adapter.out.persistence.catalog;

import fr.formation.shop.domain.catalog.Category;
import fr.formation.shop.domain.catalog.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(ProductPersistenceAdapter.class)
class ProductPersistenceAdapterTest {

    @Autowired
    private ProductPersistenceAdapter adapter;

    @Test
    void save_shouldPersistAndMapBackToDomain() {
        Product product = new Product(null, "TST-0001", "Produit de test", Category.TOYS, new BigDecimal("12.34"), 10,
                250, 7);

        Product saved = adapter.save(product);

        assertThat(saved.getId()).isNotNull();
        assertThat(adapter.findById(saved.getId()))
                .hasValueSatisfying(found -> {
                    assertThat(found.getSku()).isEqualTo("TST-0001");
                    assertThat(found.getPrice()).isEqualByComparingTo("12.34");
                    assertThat(found.getPromoPercent()).isEqualTo(10);
                });
        assertThat(adapter.existsBySku("TST-0001")).isTrue();
    }
}
