package fr.formation.shop.application.port.out;

import fr.formation.shop.domain.order.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    List<Order> findAll();

    Optional<Order> findById(Long id);

    Order save(Order order);
}
