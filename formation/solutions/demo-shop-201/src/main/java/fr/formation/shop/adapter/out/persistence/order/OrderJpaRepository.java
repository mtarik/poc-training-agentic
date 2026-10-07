package fr.formation.shop.adapter.out.persistence.order;

import fr.formation.shop.domain.order.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderJpaRepository extends JpaRepository<OrderJpaEntity, Long> {

    @Query("""
            select count(o) > 0 from OrderJpaEntity o join o.lines l
            where o.customer.id = :customerId and l.product.id = :productId and o.status <> :excludedStatus
            """)
    boolean existsPurchase(@Param("customerId") Long customerId, @Param("productId") Long productId,
                           @Param("excludedStatus") OrderStatus excludedStatus);
}
