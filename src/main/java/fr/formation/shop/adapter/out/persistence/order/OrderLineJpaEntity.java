package fr.formation.shop.adapter.out.persistence.order;

import fr.formation.shop.adapter.out.persistence.catalog.ProductJpaEntity;
import fr.formation.shop.domain.order.OrderLine;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "order_line")
public class OrderLineJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private OrderJpaEntity order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private ProductJpaEntity product;

    private int quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal lineTotal;

    protected OrderLineJpaEntity() {
    }

    OrderLineJpaEntity(OrderJpaEntity order, ProductJpaEntity product, int quantity, BigDecimal unitPrice,
                       BigDecimal lineTotal) {
        this.order = order;
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.lineTotal = lineTotal;
    }

    OrderLine toDomain() {
        return new OrderLine(product.getId(), product.getSku(), product.getName(), quantity, unitPrice, lineTotal);
    }
}
