package fr.formation.shop.application.service;

import fr.formation.shop.application.port.in.catalog.CreateProductCommand;
import fr.formation.shop.application.port.in.catalog.CreateProductUseCase;
import fr.formation.shop.application.port.in.catalog.FindProductUseCase;
import fr.formation.shop.application.port.out.ProductRepository;
import fr.formation.shop.domain.catalog.Product;
import fr.formation.shop.domain.exception.BusinessException;
import fr.formation.shop.domain.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProductService implements FindProductUseCase, CreateProductUseCase {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Produit", id));
    }

    @Override
    @Transactional
    public Product create(CreateProductCommand command) {
        if (productRepository.existsBySku(command.sku())) {
            throw new BusinessException("Le SKU %s existe déjà".formatted(command.sku()));
        }
        Product product = new Product(null, command.sku(), command.name(), command.category(), command.price(),
                command.promoPercent(), command.weightGrams(), command.stock());
        return productRepository.save(product);
    }
}
