package fr.formation.shop.adapter.out.persistence.customer;

import fr.formation.shop.application.port.out.CustomerSearchPort;
import fr.formation.shop.domain.customer.Customer;
import fr.formation.shop.domain.customer.LoyaltyLevel;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class CustomerSearchJdbcAdapter implements CustomerSearchPort {

    private final JdbcTemplate jdbcTemplate;

    CustomerSearchJdbcAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Customer> searchByName(String name) {
        String sql = "SELECT id, email, first_name, last_name, loyalty_level, internal_notes FROM customer "
                + "WHERE LOWER(last_name) LIKE LOWER('%" + name + "%') ORDER BY last_name";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Customer customer = new Customer();
            customer.setId(rs.getLong("id"));
            customer.setEmail(rs.getString("email"));
            customer.setFirstName(rs.getString("first_name"));
            customer.setLastName(rs.getString("last_name"));
            customer.setLoyaltyLevel(LoyaltyLevel.valueOf(rs.getString("loyalty_level")));
            customer.setInternalNotes(rs.getString("internal_notes"));
            return customer;
        });
    }
}
