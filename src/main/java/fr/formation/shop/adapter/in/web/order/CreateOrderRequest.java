package fr.formation.shop.adapter.in.web.order;

import fr.formation.shop.application.port.in.order.PlaceOrderCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public record CreateOrderRequest(
        @NotNull Long customerId,
        @NotNull @Pattern(regexp = "[A-Z]{2}", message = "code pays ISO à 2 lettres attendu") String country,
        Boolean express,
        @NotEmpty List<@Valid LineRequest> lines) {

    public record LineRequest(
            @NotNull Long productId,
            @Min(1) int quantity) {
    }

    PlaceOrderCommand toCommand() {
        return new PlaceOrderCommand(customerId, country, Boolean.TRUE.equals(express),
                lines.stream().map(line -> new PlaceOrderCommand.Line(line.productId(), line.quantity())).toList());
    }
}
