package fr.formation.shop.adapter.in.web.customer;

import fr.formation.shop.application.port.in.customer.FindCustomerUseCase;
import fr.formation.shop.application.port.in.customer.RegisterCustomerUseCase;
import fr.formation.shop.domain.customer.Customer;
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
@RequestMapping("/api/customers")
public class CustomerController {

    private final FindCustomerUseCase findCustomerUseCase;
    private final RegisterCustomerUseCase registerCustomerUseCase;

    public CustomerController(FindCustomerUseCase findCustomerUseCase,
                              RegisterCustomerUseCase registerCustomerUseCase) {
        this.findCustomerUseCase = findCustomerUseCase;
        this.registerCustomerUseCase = registerCustomerUseCase;
    }

    @GetMapping("/{id}")
    public Customer findById(@PathVariable Long id) {
        return findCustomerUseCase.findById(id);
    }

    @GetMapping("/search")
    public List<Customer> search(@RequestParam String name) {
        return findCustomerUseCase.searchByName(name);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Customer create(@RequestBody Customer customer) {
        return registerCustomerUseCase.register(customer);
    }
}
