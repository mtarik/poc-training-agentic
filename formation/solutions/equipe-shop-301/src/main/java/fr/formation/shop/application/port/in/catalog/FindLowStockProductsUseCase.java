package fr.formation.shop.application.port.in.catalog;

import fr.formation.shop.domain.catalog.Product;

import java.util.List;

public interface FindLowStockProductsUseCase {

    /**
     * Produits dont le stock est inférieur ou égal au seuil, les plus urgents en premier.
     */
    List<Product> findLowStock(int threshold);
}
