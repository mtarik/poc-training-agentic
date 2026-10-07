package fr.formation.shop.adapter.in.web.order;

import fr.formation.shop.application.port.in.order.FindOrderUseCase;
import fr.formation.shop.application.port.in.order.PlaceOrderUseCase;
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
@RequestMapping("/api/orders")
public class OrderController {

    private final PlaceOrderUseCase placeOrderUseCase;
    private final FindOrderUseCase findOrderUseCase;

    public OrderController(PlaceOrderUseCase placeOrderUseCase, FindOrderUseCase findOrderUseCase) {
        this.placeOrderUseCase = placeOrderUseCase;
        this.findOrderUseCase = findOrderUseCase;
    }

    @GetMapping
    public List<OrderResponse> findAll() {
        return findOrderUseCase.findAll().stream()
                .map(OrderResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public OrderResponse findById(@PathVariable Long id) {
        return OrderResponse.from(findOrderUseCase.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse create(@Valid @RequestBody CreateOrderRequest request) {
        return OrderResponse.from(placeOrderUseCase.place(request.toCommand()));
    }
}
