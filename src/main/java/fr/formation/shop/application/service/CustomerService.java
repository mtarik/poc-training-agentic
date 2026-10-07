package fr.formation.shop.application.service;

import fr.formation.shop.application.port.in.customer.FindCustomerUseCase;
import fr.formation.shop.application.port.in.customer.RegisterCustomerUseCase;
import fr.formation.shop.application.port.out.CustomerRepository;
import fr.formation.shop.application.port.out.CustomerSearchPort;
import fr.formation.shop.domain.customer.Customer;
import fr.formation.shop.domain.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CustomerService implements FindCustomerUseCase, RegisterCustomerUseCase {

    private final CustomerRepository customerRepository;
    private final CustomerSearchPort customerSearchPort;

    public CustomerService(CustomerRepository customerRepository, CustomerSearchPort customerSearchPort) {
        this.customerRepository = customerRepository;
        this.customerSearchPort = customerSearchPort;
    }

    @Override
    public Customer findById(Long id) {
        return customerRepository.findById(id).orElseThrow(() -> new NotFoundException("Client", id));
    }

    @Override
    public List<Customer> searchByName(String name) {
        return customerSearchPort.searchByName(name);
    }

    @Override
    @Transactional
    public Customer register(Customer customer) {
        return customerRepository.save(customer);
    }
}
