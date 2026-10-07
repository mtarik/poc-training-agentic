package fr.formation.shop.application.service;

import fr.formation.shop.application.port.in.review.PostReviewCommand;
import fr.formation.shop.application.port.out.CustomerRepository;
import fr.formation.shop.application.port.out.OrderRepository;
import fr.formation.shop.application.port.out.ProductRepository;
import fr.formation.shop.application.port.out.ReviewAlertPort;
import fr.formation.shop.application.port.out.ReviewRepository;
import fr.formation.shop.domain.catalog.Category;
import fr.formation.shop.domain.catalog.Product;
import fr.formation.shop.domain.customer.Customer;
import fr.formation.shop.domain.exception.BusinessException;
import fr.formation.shop.domain.exception.NotFoundException;
import fr.formation.shop.domain.review.ProductRating;
import fr.formation.shop.domain.review.Review;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    private static final Long PRODUCT_ID = 1L;
    private static final Long CUSTOMER_ID = 2L;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ReviewAlertPort reviewAlertPort;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    void post_shouldSaveReview_whenCustomerBoughtTheProduct() {
        givenExistingProductAndCustomer();
        when(orderRepository.hasCustomerPurchasedProduct(CUSTOMER_ID, PRODUCT_ID)).thenReturn(true);
        when(reviewRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Review review = reviewService.post(new PostReviewCommand(PRODUCT_ID, CUSTOMER_ID, 5, "Parfait"));

        assertThat(review.getRating()).isEqualTo(5);
        verifyNoInteractions(reviewAlertPort);
    }

    @Test
    void post_shouldNotifyQualityTeam_whenReviewIsNegative() {
        givenExistingProductAndCustomer();
        when(orderRepository.hasCustomerPurchasedProduct(CUSTOMER_ID, PRODUCT_ID)).thenReturn(true);
        when(reviewRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Review review = reviewService.post(new PostReviewCommand(PRODUCT_ID, CUSTOMER_ID, 1, "Déçu"));

        verify(reviewAlertPort).notifyNegativeReview(review);
    }

    @Test
    void post_shouldReject_whenCustomerNeverBoughtTheProduct() {
        givenExistingProductAndCustomer();
        when(orderRepository.hasCustomerPurchasedProduct(CUSTOMER_ID, PRODUCT_ID)).thenReturn(false);

        assertThatThrownBy(() -> reviewService.post(new PostReviewCommand(PRODUCT_ID, CUSTOMER_ID, 5, null)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("n'a pas acheté");
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void post_shouldReject_whenCustomerAlreadyReviewedTheProduct() {
        givenExistingProductAndCustomer();
        when(orderRepository.hasCustomerPurchasedProduct(CUSTOMER_ID, PRODUCT_ID)).thenReturn(true);
        when(reviewRepository.existsByProductIdAndCustomerId(PRODUCT_ID, CUSTOMER_ID)).thenReturn(true);

        assertThatThrownBy(() -> reviewService.post(new PostReviewCommand(PRODUCT_ID, CUSTOMER_ID, 5, null)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("déjà");
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void post_shouldThrowNotFound_whenProductDoesNotExist() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.post(new PostReviewCommand(PRODUCT_ID, CUSTOMER_ID, 5, null)))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Produit");
    }

    @Test
    void post_shouldThrowNotFound_whenCustomerDoesNotExist() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(aProduct()));
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.post(new PostReviewCommand(PRODUCT_ID, CUSTOMER_ID, 5, null)))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Client");
    }

    @Test
    void getRating_shouldAggregateReviewsOfTheProduct() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(aProduct()));
        when(reviewRepository.findByProductId(PRODUCT_ID)).thenReturn(List.of(
                Review.create(PRODUCT_ID, 2L, 5, null),
                Review.create(PRODUCT_ID, 3L, 4, null)));

        ProductRating rating = reviewService.getRating(PRODUCT_ID);

        assertThat(rating.averageRating()).isEqualByComparingTo("4.5");
        assertThat(rating.count()).isEqualTo(2);
    }

    private void givenExistingProductAndCustomer() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(aProduct()));
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(new Customer()));
    }

    private static Product aProduct() {
        return new Product(PRODUCT_ID, "BOO-0001", "Clean Code", Category.BOOKS, new BigDecimal("32.50"), null, 600,
                25);
    }
}
