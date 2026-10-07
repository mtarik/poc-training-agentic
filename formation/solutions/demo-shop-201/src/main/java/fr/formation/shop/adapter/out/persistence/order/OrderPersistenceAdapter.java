package fr.formation.shop.adapter.out.persistence.order;

import fr.formation.shop.adapter.out.persistence.catalog.ProductJpaRepository;
import fr.formation.shop.adapter.out.persistence.customer.CustomerJpaRepository;
import fr.formation.shop.application.port.out.OrderRepository;
import fr.formation.shop.domain.order.Order;
import fr.formation.shop.domain.order.OrderLine;
import fr.formation.shop.domain.order.OrderStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
class OrderPersistenceAdapter implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;
    private final CustomerJpaRepository customerJpaRepository;
    private final ProductJpaRepository productJpaRepository;

    OrderPersistenceAdapter(OrderJpaRepository orderJpaRepository, CustomerJpaRepository customerJpaRepository,
                            ProductJpaRepository productJpaRepository) {
        this.orderJpaRepository = orderJpaRepository;
        this.customerJpaRepository = customerJpaRepository;
        this.productJpaRepository = productJpaRepository;
    }

    @Override
    public List<Order> findAll() {
        return orderJpaRepository.findAll().stream()
                .map(OrderJpaEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<Order> findById(Long id) {
        return orderJpaRepository.findById(id).map(OrderJpaEntity::toDomain);
    }

    @Override
    public Order save(Order order) {
        OrderJpaEntity entity = new OrderJpaEntity(order,
                customerJpaRepository.getReferenceById(order.getCustomer().getId()));
        for (OrderLine line : order.getLines()) {
            entity.addLine(new OrderLineJpaEntity(entity, productJpaRepository.getReferenceById(line.productId()),
                    line.quantity(), line.unitPrice(), line.lineTotal()));
        }
        return orderJpaRepository.save(entity).toDomain();
    }

    @Override
    public boolean hasCustomerPurchasedProduct(Long customerId, Long productId) {
        return orderJpaRepository.existsPurchase(customerId, productId, OrderStatus.CANCELLED);
    }
}
