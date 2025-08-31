package ua.edu.dnu.warehouse.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.edu.dnu.warehouse.model.Order;
import ua.edu.dnu.warehouse.model.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    @Query("SELECT SUM(o.total) FROM Order o WHERE o.status = :status")
    Long calculateTotalProfitByStatus(@Param("status") OrderStatus status);
    @Query("SELECT AVG(o.total) FROM Order o")
    Double calculateAverageOrderPrice();
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    Long countOrdersByStatus(@Param("status") OrderStatus status);
    @Query("SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status")
    List<Object[]> getOrdersCountByStatus();
    @Query("SELECT oi.goods, SUM(oi.number), SUM(oi.price * oi.number) AS total " +
            "FROM OrderItem oi " +
            "JOIN oi.order o " +
            "WHERE o.status = :status " +
            "GROUP BY oi.goods " +
            "ORDER BY total DESC")
    Page<Object[]> findTopGoodsByStatus(@Param("status") OrderStatus status,  Pageable pageable);
    @Query("SELECT DATE_TRUNC('day', o.date) as date_day, COUNT(o) FROM Order o " +
            "WHERE o.status = :status " +
            "AND o.date BETWEEN :startDate AND :endDate " +
            "GROUP BY date_day " +
            "ORDER BY DATE_TRUNC('day', o.date) ASC")
    List<Object[]> getOrderCountByDay(LocalDateTime startDate, LocalDateTime endDate, OrderStatus status);
}
