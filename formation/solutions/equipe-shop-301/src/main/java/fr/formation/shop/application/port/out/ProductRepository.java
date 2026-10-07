package fr.formation.shop.application.port.out;

import fr.formation.shop.domain.catalog.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    List<Product> findAll();

    Optional<Product> findById(Long id);

    boolean existsBySku(String sku);

    Product save(Product product);

    /**
     * Produits dont le stock est inférieur ou égal au seuil, triés par stock croissant puis par nom.
     */
    List<Product> findByStockAtMost(int threshold);
}
