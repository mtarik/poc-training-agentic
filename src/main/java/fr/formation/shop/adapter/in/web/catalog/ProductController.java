package fr.formation.shop.adapter.in.web.catalog;

import fr.formation.shop.application.port.in.catalog.CreateProductUseCase;
import fr.formation.shop.application.port.in.catalog.FindProductUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final FindProductUseCase findProductUseCase;
    private final CreateProductUseCase createProductUseCase;

    public ProductController(FindProductUseCase findProductUseCase, CreateProductUseCase createProductUseCase) {
        this.findProductUseCase = findProductUseCase;
        this.createProductUseCase = createProductUseCase;
    }

    @GetMapping
    public List<ProductResponse> findAll() {
        return findProductUseCase.findAll().stream()
                .map(ProductResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public ProductResponse findById(@PathVariable Long id) {
        return ProductResponse.from(findProductUseCase.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse create(@Valid @RequestBody CreateProductRequest request) {
        return ProductResponse.from(createProductUseCase.create(request.toCommand()));
    }
}
