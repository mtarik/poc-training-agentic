package fr.formation.shop.application.port.in.order;

import java.util.List;

public record PlaceOrderCommand(
        Long customerId,
        String country,
        boolean express,
        List<Line> lines) {

    public record Line(Long productId, int quantity) {
    }
}
