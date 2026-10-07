package fr.formation.shop.application.port.in.customer;

import fr.formation.shop.domain.customer.Customer;

public interface RegisterCustomerUseCase {

    Customer register(Customer customer);
}
