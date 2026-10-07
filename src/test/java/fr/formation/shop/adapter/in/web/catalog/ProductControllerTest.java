package fr.formation.shop.adapter.in.web.catalog;

import fr.formation.shop.application.port.in.catalog.CreateProductUseCase;
import fr.formation.shop.application.port.in.catalog.FindProductUseCase;
import fr.formation.shop.domain.catalog.Category;
import fr.formation.shop.domain.catalog.Product;
import fr.formation.shop.domain.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FindProductUseCase findProductUseCase;

    @MockitoBean
    private CreateProductUseCase createProductUseCase;

    @Test
    void getProduct_shouldReturn200() throws Exception {
        when(findProductUseCase.findById(1L)).thenReturn(new Product(1L, "BOO-0001", "Clean Code", Category.BOOKS,
                new BigDecimal("32.50"), null, 600, 25));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("BOO-0001"));
    }

    @Test
    void getProduct_shouldReturn404_whenUnknown() throws Exception {
        when(findProductUseCase.findById(42L)).thenThrow(new NotFoundException("Produit", 42L));

        mockMvc.perform(get("/api/products/42"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createProduct_shouldReturn400_whenSkuIsInvalid() throws Exception {
        String body = """
                {"sku": "bad", "name": "Test", "category": "BOOKS", "price": 10.00, "weightGrams": 100, "stock": 1}
                """;

        mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }
}
