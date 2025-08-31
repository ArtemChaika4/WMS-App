package ua.edu.dnu.warehouse.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "event_logs")
public class EventLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private String result;
    @Enumerated(EnumType.STRING)
    private Category category;
    @Enumerated(EnumType.STRING)
    private Action action;
    @Column(nullable = false)
    private LocalDateTime timestamp;
    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    public EventLog(){
        id = 0;
        result = "";
        timestamp = LocalDateTime.now();
        employee = new Employee();
    }

    public EventLog(String result, Category category, Action action,
                    LocalDateTime timestamp, Employee employee) {
        this.result = result;
        this.action = action;
        this.timestamp = timestamp;
        this.employee = employee;
        this.category = category;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String activity) {
        this.result = activity;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category Category) {
        this.category = Category;
    }

    @Override
    public String toString() {
        return "EventLog{" +
                "id=" + id +
                ", result='" + result + '\'' +
                ", category=" + category +
                ", action=" + action +
                ", timestamp=" + timestamp +
                ", employee=" + employee +
                '}';
    }
}
