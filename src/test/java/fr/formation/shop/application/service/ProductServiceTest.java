package fr.formation.shop.application.service;

import fr.formation.shop.application.port.in.catalog.CreateProductCommand;
import fr.formation.shop.application.port.out.ProductRepository;
import fr.formation.shop.domain.catalog.Category;
import fr.formation.shop.domain.catalog.Product;
import fr.formation.shop.domain.exception.BusinessException;
import fr.formation.shop.domain.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void findById_shouldReturnProduct_whenItExists() {
        Product product = new Product(1L, "BOO-0001", "Clean Code", Category.BOOKS, new BigDecimal("32.50"), null,
                600, 25);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Product result = productService.findById(1L);

        assertThat(result.getSku()).isEqualTo("BOO-0001");
    }

    @Test
    void findById_shouldThrowNotFound_whenProductDoesNotExist() {
        when(productRepository.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.findById(42L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("42");
    }

    @Test
    void create_shouldRejectDuplicateSku() {
        CreateProductCommand command = new CreateProductCommand("BOO-0001", "Clean Code", Category.BOOKS,
                new BigDecimal("32.50"), null, 600, 25);
        when(productRepository.existsBySku("BOO-0001")).thenReturn(true);

        assertThatThrownBy(() -> productService.create(command))
                .isInstanceOf(BusinessException.class);
        verify(productRepository, never()).save(any());
    }

    @Test
    void create_shouldSaveProduct_whenSkuIsNew() {
        CreateProductCommand command = new CreateProductCommand("TST-0001", "Test", Category.TOYS,
                new BigDecimal("10.00"), null, 100, 5);
        when(productRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Product result = productService.create(command);

        assertThat(result.getSku()).isEqualTo("TST-0001");
        assertThat(result.getStock()).isEqualTo(5);
    }
}
