package fr.formation.shop.application.port.out;

import fr.formation.shop.domain.customer.Customer;

import java.util.List;

public interface CustomerSearchPort {

    List<Customer> searchByName(String name);
}
