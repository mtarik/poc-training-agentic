package fr.formation.shop.adapter.out.persistence.catalog;

import fr.formation.shop.application.port.out.ProductRepository;
import fr.formation.shop.domain.catalog.Product;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
class ProductPersistenceAdapter implements ProductRepository {

    private final ProductJpaRepository jpaRepository;

    ProductPersistenceAdapter(ProductJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<Product> findAll() {
        return jpaRepository.findAll().stream()
                .map(ProductJpaEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<Product> findById(Long id) {
        return jpaRepository.findById(id).map(ProductJpaEntity::toDomain);
    }

    @Override
    public boolean existsBySku(String sku) {
        return jpaRepository.existsBySku(sku);
    }

    @Override
    public Product save(Product product) {
        return jpaRepository.save(ProductJpaEntity.fromDomain(product)).toDomain();
    }

    @Override
    public List<Product> findByStockAtMost(int threshold) {
        return jpaRepository.findByStockLessThanEqualOrderByStockAscNameAsc(threshold).stream()
                .map(ProductJpaEntity::toDomain)
                .toList();
    }
}
