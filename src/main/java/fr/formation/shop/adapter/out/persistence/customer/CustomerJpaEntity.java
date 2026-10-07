package fr.formation.shop.adapter.out.persistence.customer;

import fr.formation.shop.domain.customer.Customer;
import fr.formation.shop.domain.customer.LoyaltyLevel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "customer")
public class CustomerJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String firstName;

    private String lastName;

    @Enumerated(EnumType.STRING)
    private LoyaltyLevel loyaltyLevel;

    private String internalNotes;

    private LocalDateTime createdAt;

    protected CustomerJpaEntity() {
    }

    public static CustomerJpaEntity fromDomain(Customer customer) {
        CustomerJpaEntity entity = new CustomerJpaEntity();
        entity.id = customer.getId();
        entity.email = customer.getEmail();
        entity.firstName = customer.getFirstName();
        entity.lastName = customer.getLastName();
        entity.loyaltyLevel = customer.getLoyaltyLevel();
        entity.internalNotes = customer.getInternalNotes();
        entity.createdAt = customer.getCreatedAt();
        return entity;
    }

    public Customer toDomain() {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setEmail(email);
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setLoyaltyLevel(loyaltyLevel);
        customer.setInternalNotes(internalNotes);
        customer.setCreatedAt(createdAt);
        return customer;
    }
}
