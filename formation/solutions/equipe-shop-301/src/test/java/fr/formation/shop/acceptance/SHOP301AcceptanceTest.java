package fr.formation.shop.acceptance;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItems;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Critères d'acceptation de SHOP-301, vérifiés sur le jeu de données de démonstration.
 */
@SpringBootTest
@AutoConfigureMockMvc
class SHOP301AcceptanceTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void defaultThreshold_shouldBe5_andSortByStockAscending() throws Exception {
        mockMvc.perform(get("/api/products/low-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].sku").value(contains("TOY-0002", "ELE-0003", "ELE-0002")))
                .andExpect(jsonPath("$[0].stock").value(0));
    }

    @Test
    void thresholdZero_shouldReturnOnlyOutOfStockProducts() throws Exception {
        mockMvc.perform(get("/api/products/low-stock").param("threshold", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Jeu de société Agile"));
    }

    @Test
    void threshold_shouldBeInclusive() throws Exception {
        mockMvc.perform(get("/api/products/low-stock").param("threshold", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[*].name").value(hasItems("Clavier mécanique", "Domain-Driven Design")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", "101"})
    void threshold_shouldBeRejected_whenOutOfRange(String threshold) throws Exception {
        mockMvc.perform(get("/api/products/low-stock").param("threshold", threshold))
                .andExpect(status().isBadRequest());
    }

    @Test
    void lowStock_shouldNotBeConfusedWithProductId() throws Exception {
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("BOO-0001")));
    }
}
