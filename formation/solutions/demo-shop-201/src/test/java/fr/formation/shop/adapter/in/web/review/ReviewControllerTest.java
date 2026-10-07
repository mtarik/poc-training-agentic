package fr.formation.shop.adapter.in.web.review;

import fr.formation.shop.application.port.in.review.GetProductRatingUseCase;
import fr.formation.shop.application.port.in.review.PostReviewUseCase;
import fr.formation.shop.domain.exception.BusinessException;
import fr.formation.shop.domain.exception.NotFoundException;
import fr.formation.shop.domain.review.ProductRating;
import fr.formation.shop.domain.review.Review;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PostReviewUseCase postReviewUseCase;

    @MockitoBean
    private GetProductRatingUseCase getProductRatingUseCase;

    @Test
    void post_shouldReturn201_whenReviewIsAccepted() throws Exception {
        when(postReviewUseCase.post(any())).thenReturn(new Review(10L, 1L, 2L, 5, "Top", LocalDateTime.now()));

        mockMvc.perform(post("/api/products/1/reviews").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"customerId": 2, "rating": 5, "comment": "Top"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.rating").value(5));
    }

    @Test
    void post_shouldReturn400_whenRatingIsOutOfRange() throws Exception {
        mockMvc.perform(post("/api/products/1/reviews").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"customerId": 2, "rating": 6}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void post_shouldReturn422_whenBusinessRuleIsViolated() throws Exception {
        when(postReviewUseCase.post(any())).thenThrow(new BusinessException("Le client 2 n'a pas acheté le produit 1"));

        mockMvc.perform(post("/api/products/1/reviews").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"customerId": 2, "rating": 4}
                                """))
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    void getReviews_shouldReturnAverageAndReviews() throws Exception {
        when(getProductRatingUseCase.getRating(1L)).thenReturn(ProductRating.of(1L, List.of(
                new Review(10L, 1L, 2L, 5, null, LocalDateTime.now()),
                new Review(11L, 1L, 3L, 4, null, LocalDateTime.now()))));

        mockMvc.perform(get("/api/products/1/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageRating").value(4.5))
                .andExpect(jsonPath("$.count").value(2))
                .andExpect(jsonPath("$.reviews.length()").value(2));
    }

    @Test
    void getReviews_shouldReturn404_whenProductIsUnknown() throws Exception {
        when(getProductRatingUseCase.getRating(42L)).thenThrow(new NotFoundException("Produit", 42L));

        mockMvc.perform(get("/api/products/42/reviews"))
                .andExpect(status().isNotFound());
    }
}
