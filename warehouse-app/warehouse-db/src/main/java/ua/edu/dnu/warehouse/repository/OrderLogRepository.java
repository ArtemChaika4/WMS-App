package ua.edu.dnu.warehouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.edu.dnu.warehouse.model.OrderItem;
import ua.edu.dnu.warehouse.model.OrderLog;

import java.util.List;

@Repository
public interface OrderLogRepository extends JpaRepository<OrderLog, Long> {
    List<OrderLog> findByOrderId(Long orderId);
}
