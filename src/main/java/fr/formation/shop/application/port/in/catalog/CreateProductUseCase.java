package fr.formation.shop.application.port.in.catalog;

import fr.formation.shop.domain.catalog.Product;

public interface CreateProductUseCase {

    Product create(CreateProductCommand command);
}
