package fr.formation.shop.adapter.out.persistence.catalog;

import fr.formation.shop.domain.catalog.Category;
import fr.formation.shop.domain.catalog.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "product")
public class ProductJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String sku;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    private Integer promoPercent;

    private int weightGrams;

    private int stock;

    protected ProductJpaEntity() {
    }

    public static ProductJpaEntity fromDomain(Product product) {
        ProductJpaEntity entity = new ProductJpaEntity();
        entity.id = product.getId();
        entity.sku = product.getSku();
        entity.name = product.getName();
        entity.category = product.getCategory();
        entity.price = product.getPrice();
        entity.promoPercent = product.getPromoPercent();
        entity.weightGrams = product.getWeightGrams();
        entity.stock = product.getStock();
        return entity;
    }

    public Product toDomain() {
        return new Product(id, sku, name, category, price, promoPercent, weightGrams, stock);
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
}
