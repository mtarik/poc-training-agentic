package fr.formation.shop.application.port.in.order;

import fr.formation.shop.domain.order.Order;

public interface PlaceOrderUseCase {

    Order place(PlaceOrderCommand command);
}
