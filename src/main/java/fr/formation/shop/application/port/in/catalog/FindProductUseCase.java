package fr.formation.shop.application.port.in.catalog;

import fr.formation.shop.domain.catalog.Product;

import java.util.List;

public interface FindProductUseCase {

    List<Product> findAll();

    Product findById(Long id);
}
