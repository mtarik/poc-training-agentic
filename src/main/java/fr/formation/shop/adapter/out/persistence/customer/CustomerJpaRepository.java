package fr.formation.shop.adapter.out.persistence.customer;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerJpaRepository extends JpaRepository<CustomerJpaEntity, Long> {
}
