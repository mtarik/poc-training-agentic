package fr.formation.shop.adapter.in.web.catalog;

import fr.formation.shop.domain.catalog.Product;

public record LowStockProductResponse(Long id, String sku, String name, int stock) {

    static LowStockProductResponse from(Product product) {
        return new LowStockProductResponse(product.getId(), product.getSku(), product.getName(), product.getStock());
    }
}
