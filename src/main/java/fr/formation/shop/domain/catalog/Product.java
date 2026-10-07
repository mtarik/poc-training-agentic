package fr.formation.shop.domain.catalog;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class Product {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    private final Long id;
    private final String sku;
    private final String name;
    private final Category category;
    private final BigDecimal price;
    /** Pourcentage de remise (0-90), null si le produit n'est pas en promotion. */
    private final Integer promoPercent;
    /** Poids unitaire en grammes, utilisé pour les frais de port. */
    private final int weightGrams;
    private int stock;

    public Product(Long id, String sku, String name, Category category, BigDecimal price, Integer promoPercent,
                   int weightGrams, int stock) {
        this.id = id;
        this.sku = Objects.requireNonNull(sku);
        this.name = Objects.requireNonNull(name);
        this.category = Objects.requireNonNull(category);
        this.price = Objects.requireNonNull(price);
        this.promoPercent = promoPercent;
        this.weightGrams = weightGrams;
        this.stock = stock;
    }

    public boolean isOnPromotion() {
        return promoPercent != null && promoPercent > 0;
    }

    /**
     * Prix unitaire après promotion, arrondi au centime.
     */
    public BigDecimal discountedPrice() {
        if (!isOnPromotion()) {
            return price;
        }
        return price.multiply(HUNDRED.subtract(BigDecimal.valueOf(promoPercent)))
                .divide(HUNDRED, 2, RoundingMode.HALF_UP);
    }

    public Long getId() {
        return id;
    }

    public String getSku() {
        return sku;
    }

    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getPromoPercent() {
        return promoPercent;
    }

    public int getWeightGrams() {
        return weightGrams;
    }

    public int getStock() {
        return stock;
    }
}
