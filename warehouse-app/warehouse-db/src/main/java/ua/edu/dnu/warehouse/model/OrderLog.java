package ua.edu.dnu.warehouse.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_logs")
public class OrderLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    private Order order;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;
    @Column(nullable = false)
    private LocalDateTime timestamp;

    public OrderLog(){
        order = new Order();
        employee = new Employee();
        timestamp = LocalDateTime.now();
    }

    public OrderLog(Order order, OrderStatus status, Employee employee, LocalDateTime timestamp) {
        this.order = order;
        this.status = status;
        this.employee = employee;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "OrderLog{" +
                "id=" + id +
                ", status=" + status +
                ", employee=" + employee +
                ", timestamp=" + timestamp +
                '}';
    }
}
