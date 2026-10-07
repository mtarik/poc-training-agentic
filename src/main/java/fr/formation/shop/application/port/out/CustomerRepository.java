package fr.formation.shop.application.port.out;

import fr.formation.shop.domain.customer.Customer;

import java.util.Optional;

public interface CustomerRepository {

    Optional<Customer> findById(Long id);

    Customer save(Customer customer);
}
