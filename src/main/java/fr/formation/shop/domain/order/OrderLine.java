package fr.formation.shop.domain.order;

import fr.formation.shop.domain.catalog.Product;

import java.math.BigDecimal;

/**
 * Ligne de commande. Le SKU et le nom du produit sont conservés tels qu'au moment de la commande.
 */
public record OrderLine(
        Long productId,
        String sku,
        String productName,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal) {

    public static OrderLine of(Product product, int quantity) {
        BigDecimal unitPrice = product.getPrice();
        BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
        if (product.isOnPromotion()) {
            unitPrice = product.discountedPrice();
            lineTotal = product.discountedPrice();
        }
        return new OrderLine(product.getId(), product.getSku(), product.getName(), quantity, unitPrice, lineTotal);
    }
}
