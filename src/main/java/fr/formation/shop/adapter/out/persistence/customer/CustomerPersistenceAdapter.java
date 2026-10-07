package fr.formation.shop.adapter.out.persistence.customer;

import fr.formation.shop.application.port.out.CustomerRepository;
import fr.formation.shop.domain.customer.Customer;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class CustomerPersistenceAdapter implements CustomerRepository {

    private final CustomerJpaRepository jpaRepository;

    CustomerPersistenceAdapter(CustomerJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Customer> findById(Long id) {
        return jpaRepository.findById(id).map(CustomerJpaEntity::toDomain);
    }

    @Override
    public Customer save(Customer customer) {
        return jpaRepository.save(CustomerJpaEntity.fromDomain(customer)).toDomain();
    }
}
