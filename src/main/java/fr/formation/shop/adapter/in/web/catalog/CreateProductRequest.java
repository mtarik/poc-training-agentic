package fr.formation.shop.adapter.in.web.catalog;

import fr.formation.shop.application.port.in.catalog.CreateProductCommand;
import fr.formation.shop.domain.catalog.Category;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public record CreateProductRequest(
        @NotBlank @Pattern(regexp = "[A-Z]{3}-\\d{4}", message = "doit respecter le format ABC-1234") String sku,
        @NotBlank String name,
        @NotNull Category category,
        @NotNull @DecimalMin("0.01") BigDecimal price,
        @Min(0) @Max(90) Integer promoPercent,
        @Min(1) int weightGrams,
        @Min(0) int stock) {

    CreateProductCommand toCommand() {
        return new CreateProductCommand(sku, name, category, price, promoPercent, weightGrams, stock);
    }
}
