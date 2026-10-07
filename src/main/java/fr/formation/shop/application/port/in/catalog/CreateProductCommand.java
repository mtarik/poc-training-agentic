package fr.formation.shop.application.port.in.catalog;

import fr.formation.shop.domain.catalog.Category;

import java.math.BigDecimal;

public record CreateProductCommand(
        String sku,
        String name,
        Category category,
        BigDecimal price,
        Integer promoPercent,
        int weightGrams,
        int stock) {
}
