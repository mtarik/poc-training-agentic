package fr.formation.shop.domain.order;

import fr.formation.shop.domain.catalog.Product;
import fr.formation.shop.domain.customer.Customer;
import fr.formation.shop.domain.customer.LoyaltyLevel;
import fr.formation.shop.domain.shipping.ShippingCostCalculator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Agrégat commande.
 */
public class Order {

    private static final BigDecimal GOLD_DISCOUNT_RATE = new BigDecimal("0.95");

    private final Long id;
    private final Customer customer;
    private final List<OrderLine> lines;
    private OrderStatus status;
    private final String country;
    private final boolean express;
    private int totalWeightGrams;
    private BigDecimal subtotal;
    private BigDecimal shippingCost;
    private BigDecimal total;
    private final LocalDateTime createdAt;

    public Order(Long id, Customer customer, List<OrderLine> lines, OrderStatus status, String country,
                 boolean express, int totalWeightGrams, BigDecimal subtotal, BigDecimal shippingCost,
                 BigDecimal total, LocalDateTime createdAt) {
        this.id = id;
        this.customer = customer;
        this.lines = new ArrayList<>(lines);
        this.status = status;
        this.country = country;
        this.express = express;
        this.totalWeightGrams = totalWeightGrams;
        this.subtotal = subtotal;
        this.shippingCost = shippingCost;
        this.total = total;
        this.createdAt = createdAt;
    }

    /**
     * Crée une nouvelle commande vide, au statut CREATED.
     */
    public static Order create(Customer customer, String country, boolean express) {
        return new Order(null, customer, List.of(), OrderStatus.CREATED, country, express, 0,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, LocalDateTime.now());
    }

    public void addLine(Product product, int quantity) {
        lines.add(OrderLine.of(product, quantity));
        totalWeightGrams += product.getWeightGrams() * quantity;
    }

    /**
     * Calcule sous-total (remise GOLD incluse), frais de port et total.
     */
    public void computeTotals() {
        BigDecimal sum = lines.stream()
                .map(OrderLine::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (customer.getLoyaltyLevel() == LoyaltyLevel.GOLD) {
            sum = sum.multiply(GOLD_DISCOUNT_RATE).setScale(2, RoundingMode.HALF_UP);
        }
        double shipping = ShippingCostCalculator.calc(country, sum.doubleValue(), totalWeightGrams,
                customer.getLoyaltyLevel().name(), express);

        this.subtotal = sum;
        this.shippingCost = BigDecimal.valueOf(shipping).setScale(2, RoundingMode.HALF_UP);
        this.total = subtotal.add(shippingCost);
    }

    public Long getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public List<OrderLine> getLines() {
        return Collections.unmodifiableList(lines);
    }

    public OrderStatus getStatus() {
        return status;
    }

    public String getCountry() {
        return country;
    }

    public boolean isExpress() {
        return express;
    }

    public int getTotalWeightGrams() {
        return totalWeightGrams;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public BigDecimal getShippingCost() {
        return shippingCost;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
