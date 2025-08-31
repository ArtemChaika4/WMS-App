package ua.edu.dnu.warehouse.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.dnu.warehouse.aspect.LogAction;
import ua.edu.dnu.warehouse.aspect.LogCategory;
import ua.edu.dnu.warehouse.model.*;
import ua.edu.dnu.warehouse.repository.*;
import ua.edu.dnu.warehouse.user.SessionStorage;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@LogCategory(Category.ORDERS)
@Transactional
@Service
public class OrderService extends AbstractSearchService<Order>{
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final GoodsRepository goodsRepository;
    private final OrderItemRepository itemRepository;
    private final OrderLogRepository orderLogRepository;
    private final SessionStorage sessionStorage;

    public OrderService(OrderRepository orderRepository, CustomerRepository customerRepository,
                        GoodsRepository goodsRepository, OrderItemRepository itemRepository,
                        OrderLogRepository orderLogRepository,
                        SessionStorage sessionStorage) {
        super(orderRepository);
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.goodsRepository = goodsRepository;
        this.itemRepository = itemRepository;
        this.orderLogRepository = orderLogRepository;
        this.sessionStorage = sessionStorage;
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    private void logOrderChange(Order order, OrderStatus status) {
        Employee employee = sessionStorage.getUser();
        LocalDateTime timestamp = LocalDateTime.now();
        OrderLog orderLog = new OrderLog(order, status, employee, timestamp);
        orderLogRepository.save(orderLog);
    }

    public List<OrderLog> getOrderLogs(Order order){
        return orderLogRepository.findByOrderId(order.getId());
    }

    public List<OrderItem> getOrderItems(Order order){
        return itemRepository.findByOrderId(order.getId());
    }

    @LogAction(value = Action.CREATE, message = "Створено нове замовлення")
    public Order createOrder(Order order) {
        Customer customer = order.getCustomer();
        if(customer == null || !customerRepository.existsById(customer.getId())){
            throw new EntityNotFoundException("Не знайдено замовника");
        }
        updateGoodsAmount(order);
        order = orderRepository.save(order);
        logOrderChange(order, OrderStatus.CREATED);
        return order;
    }


    private void updateGoodsAmount(Order order){
        for (OrderItem item : order.getItems()) {
            Goods goods = findGoodsOrElseThrow(item.getGoods());
            if(goods.getStatus() != GoodsStatus.AVAILABLE){
                throw new IllegalArgumentException("Не має в наяності товару: " + goods.getName());
            }
            int updateAmount = goods.getAmount() - item.getNumber();
            if (updateAmount < 0) {
                throw new IllegalArgumentException("Не достатня кількість товару на складі: " + goods.getName());
            }
            if (updateAmount == 0){
                goods.setStatus(GoodsStatus.EXPECTED);
            }
            goods.setAmount(updateAmount);
            goodsRepository.save(goods);
        }
    }

    private Goods findGoodsOrElseThrow(Goods goods){
        return goodsRepository.findById(goods.getId())
                .orElseThrow(() -> new EntityNotFoundException("Не знайдено товар: " + goods.getName()));
    }

    private void returnOrderItems(Order order){
        for (OrderItem item : order.getItems()) {
            Goods goods = findGoodsOrElseThrow(item.getGoods());
            int number = item.getNumber();
            goods.setAmount(goods.getAmount() + number);
            goodsRepository.save(goods);
        }
    }

    @LogAction(value = Action.UPDATE, message = "Змінено статус замовлення")
    public Order changeOrderStatus(Long orderId, OrderStatus status) {
        Order order = findOrderByIdOrElseThrow(orderId);
        if(!order.getStatus().isCanceled() && status.isCanceled()){
            returnOrderItems(order);
        }
        order.setStatus(status);
        orderRepository.save(order);
        logOrderChange(order, status);
        return order;
    }

    private Order findOrderByIdOrElseThrow(Long id){
        return orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Не знайдено замовлення: " + id));
    }

    @LogAction(value = Action.DELETE, message = "Видалено замовлення")
    public Order deleteOrder(Long id) {
        Order order = findOrderByIdOrElseThrow(id);
        orderRepository.deleteById(id);
        return order;
    }

    public Long getTotalProfit() {
        Long total = orderRepository.calculateTotalProfitByStatus(OrderStatus.COMPLETED);
        return Optional.ofNullable(total).orElse(0L);
    }

    public Double getAverageOrderPrice() {
        Double avg = orderRepository.calculateAverageOrderPrice();
        return Optional.ofNullable(avg).orElse(0.0);
    }

    public long getOrdersCount(){
        return orderRepository.count();
    }

    public long getCompletedOrdersCount() {
        return orderRepository.countOrdersByStatus(OrderStatus.COMPLETED);
    }

    public Map<OrderStatus, Long> getOrdersCountByStatus() {
        List<Object[]> results = orderRepository.getOrdersCountByStatus();
        return results.stream()
                .collect(Collectors.toMap(row -> (OrderStatus) row[0], row -> (Long) row[1]));
    }

    public List<OrderItem> getTopSellingGoods(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        Page<Object[]> results = orderRepository.findTopGoodsByStatus(OrderStatus.COMPLETED, pageable);
        return results.getContent().stream()
                .map(row -> new OrderItem(
                        (Goods) row[0],
                        ((Number) row[1]).intValue(),
                        ((Number) row[2]).longValue()))
                .toList();
    }

    public Map<LocalDateTime, Long> getCompletedOrdersByDay(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> results = orderRepository.getOrderCountByDay(startDate, endDate, OrderStatus.COMPLETED);
        Map<LocalDateTime, Long> ordersByDayMap = new LinkedHashMap<>();
        for (Object[] result : results) {
            LocalDateTime date = (LocalDateTime) result[0];
            Long count = (Long) result[1];
            ordersByDayMap.put(date, count);
        }
        return ordersByDayMap;
    }
}
