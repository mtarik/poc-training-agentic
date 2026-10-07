package fr.formation.shop.application.port.in.order;

import fr.formation.shop.domain.order.Order;

import java.util.List;

public interface FindOrderUseCase {

    List<Order> findAll();

    Order findById(Long id);
}
