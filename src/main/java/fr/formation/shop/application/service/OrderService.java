package fr.formation.shop.application.service;

import fr.formation.shop.application.port.in.order.FindOrderUseCase;
import fr.formation.shop.application.port.in.order.PlaceOrderCommand;
import fr.formation.shop.application.port.in.order.PlaceOrderUseCase;
import fr.formation.shop.application.port.out.CustomerRepository;
import fr.formation.shop.application.port.out.OrderRepository;
import fr.formation.shop.application.port.out.ProductRepository;
import fr.formation.shop.domain.catalog.Product;
import fr.formation.shop.domain.customer.Customer;
import fr.formation.shop.domain.exception.NotFoundException;
import fr.formation.shop.domain.order.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class OrderService implements PlaceOrderUseCase, FindOrderUseCase {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository,
                        CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Override
    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Commande", id));
    }

    @Override
    @Transactional
    public Order place(PlaceOrderCommand command) {
        Customer customer = customerRepository.findById(command.customerId())
                .orElseThrow(() -> new NotFoundException("Client", command.customerId()));

        Order order = Order.create(customer, command.country(), command.express());
        for (PlaceOrderCommand.Line line : command.lines()) {
            Product product = productRepository.findById(line.productId())
                    .orElseThrow(() -> new NotFoundException("Produit", line.productId()));
            order.addLine(product, line.quantity());
        }
        order.computeTotals();

        return orderRepository.save(order);
    }
}
