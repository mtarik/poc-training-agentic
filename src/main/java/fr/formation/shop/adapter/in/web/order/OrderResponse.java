package fr.formation.shop.adapter.in.web.order;

import fr.formation.shop.domain.order.Order;
import fr.formation.shop.domain.order.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        String customerEmail,
        OrderStatus status,
        String country,
        boolean express,
        List<LineResponse> lines,
        BigDecimal subtotal,
        BigDecimal shippingCost,
        BigDecimal total,
        LocalDateTime createdAt) {

    public record LineResponse(String sku, String productName, int quantity, BigDecimal unitPrice,
                               BigDecimal lineTotal) {
    }

    static OrderResponse from(Order order) {
        List<LineResponse> lines = order.getLines().stream()
                .map(line -> new LineResponse(line.sku(), line.productName(), line.quantity(), line.unitPrice(),
                        line.lineTotal()))
                .toList();
        return new OrderResponse(order.getId(), order.getCustomer().getEmail(), order.getStatus(),
                order.getCountry(), order.isExpress(), lines, order.getSubtotal(), order.getShippingCost(),
                order.getTotal(), order.getCreatedAt());
    }
}
