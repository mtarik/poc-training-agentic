package fr.formation.shop.adapter.out.persistence.order;

import fr.formation.shop.adapter.out.persistence.customer.CustomerJpaEntity;
import fr.formation.shop.domain.order.Order;
import fr.formation.shop.domain.order.OrderStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class OrderJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private CustomerJpaEntity customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderLineJpaEntity> lines = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false, length = 2)
    private String country;

    private boolean express;

    private int totalWeightGrams;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal shippingCost;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    private LocalDateTime createdAt;

    protected OrderJpaEntity() {
    }

    OrderJpaEntity(Order order, CustomerJpaEntity customer) {
        this.id = order.getId();
        this.customer = customer;
        this.status = order.getStatus();
        this.country = order.getCountry();
        this.express = order.isExpress();
        this.totalWeightGrams = order.getTotalWeightGrams();
        this.subtotal = order.getSubtotal();
        this.shippingCost = order.getShippingCost();
        this.total = order.getTotal();
        this.createdAt = order.getCreatedAt();
    }

    void addLine(OrderLineJpaEntity line) {
        lines.add(line);
    }

    Order toDomain() {
        return new Order(id, customer.toDomain(), lines.stream().map(OrderLineJpaEntity::toDomain).toList(),
                status, country, express, totalWeightGrams, subtotal, shippingCost, total, createdAt);
    }
}
