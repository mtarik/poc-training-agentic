package fr.formation.shop.adapter.out.persistence.catalog;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductJpaRepository extends JpaRepository<ProductJpaEntity, Long> {

    boolean existsBySku(String sku);
}
