package fr.formation.shop.adapter.in.web.catalog;

import fr.formation.shop.domain.catalog.Category;
import fr.formation.shop.domain.catalog.Product;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String sku,
        String name,
        Category category,
        BigDecimal price,
        Integer promoPercent,
        int stock) {

    static ProductResponse from(Product product) {
        return new ProductResponse(product.getId(), product.getSku(), product.getName(), product.getCategory(),
                product.getPrice(), product.getPromoPercent(), product.getStock());
    }
}
