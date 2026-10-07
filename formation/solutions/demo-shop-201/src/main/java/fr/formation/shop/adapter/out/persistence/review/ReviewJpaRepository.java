package fr.formation.shop.adapter.out.persistence.review;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewJpaRepository extends JpaRepository<ReviewJpaEntity, Long> {

    List<ReviewJpaEntity> findByProductIdOrderByCreatedAtDesc(Long productId);

    boolean existsByProductIdAndCustomerId(Long productId, Long customerId);
}
