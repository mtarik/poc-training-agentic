package fr.formation.shop.adapter.out.persistence.catalog;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductJpaRepository extends JpaRepository<ProductJpaEntity, Long> {

    boolean existsBySku(String sku);

    List<ProductJpaEntity> findByStockLessThanEqualOrderByStockAscNameAsc(int threshold);
}
