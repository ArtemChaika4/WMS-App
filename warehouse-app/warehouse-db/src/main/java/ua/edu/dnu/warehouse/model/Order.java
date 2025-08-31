package ua.edu.dnu.warehouse.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {
    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    @Column(nullable = false)
    private LocalDateTime date;
    @Column(nullable = false)
    private long total;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<OrderItem> items;

    public Order(){
        customer = new Customer();
        date = LocalDateTime.now();
        status = null;
        total = 0;
        items = new ArrayList<>();
    }

    public Order(Customer customer, LocalDateTime date, OrderStatus status, long total) {
        this.customer = customer;
        this.date = date;
        this.status = status;
        this.total = total;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public LocalDateTime getDate() {
        return date;
    }
    public void setDate(LocalDateTime orderDate) {
        this.date = orderDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long orderPrice) {
        this.total = orderPrice;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        items.forEach(item -> item.setOrder(this));
        this.items = items;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", customer=" + customer +
                ", date=" + date +
                ", status=" + status +
                ", total=" + total +
                '}';
    }
}
