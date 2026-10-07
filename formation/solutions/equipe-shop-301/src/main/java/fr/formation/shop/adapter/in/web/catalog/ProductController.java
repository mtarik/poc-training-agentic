package fr.formation.shop.adapter.in.web.catalog;

import fr.formation.shop.application.port.in.catalog.CreateProductUseCase;
import fr.formation.shop.application.port.in.catalog.FindLowStockProductsUseCase;
import fr.formation.shop.application.port.in.catalog.FindProductUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final FindProductUseCase findProductUseCase;
    private final CreateProductUseCase createProductUseCase;
    private final FindLowStockProductsUseCase findLowStockProductsUseCase;

    public ProductController(FindProductUseCase findProductUseCase, CreateProductUseCase createProductUseCase,
                             FindLowStockProductsUseCase findLowStockProductsUseCase) {
        this.findProductUseCase = findProductUseCase;
        this.createProductUseCase = createProductUseCase;
        this.findLowStockProductsUseCase = findLowStockProductsUseCase;
    }

    @GetMapping("/low-stock")
    public List<LowStockProductResponse> findLowStock(
            @RequestParam(defaultValue = "5") @Min(0) @Max(100) int threshold) {
        return findLowStockProductsUseCase.findLowStock(threshold).stream()
                .map(LowStockProductResponse::from)
                .toList();
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
