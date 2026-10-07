package fr.formation.shop.application.port.in.customer;

import fr.formation.shop.domain.customer.Customer;

import java.util.List;

public interface FindCustomerUseCase {

    Customer findById(Long id);

    List<Customer> searchByName(String name);
}
