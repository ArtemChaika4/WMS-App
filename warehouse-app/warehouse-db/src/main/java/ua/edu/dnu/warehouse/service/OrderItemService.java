package ua.edu.dnu.warehouse.service;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Component;
import ua.edu.dnu.warehouse.model.OrderItem;
import ua.edu.dnu.warehouse.repository.OrderItemRepository;

@Component
public class OrderItemService extends AbstractSearchService<OrderItem> {
    private final OrderItemRepository orderItemRepository;

    protected OrderItemService(OrderItemRepository orderItemRepository) {
        super(orderItemRepository);
        this.orderItemRepository = orderItemRepository;
    }
}
