package ua.edu.dnu.warehouse.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    private Order order;

    @ManyToOne
    @JoinColumn(name = "goods_id", nullable = false)
    private Goods goods;

    @Column(nullable = false)
    private int number;

    @Column(nullable = false)
    private long price;


    public OrderItem(){
        goods = new Goods();
        number = 0;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public OrderItem(Goods goods, int number, long price) {
        this.goods = goods;
        this.number = number;
        this.price = price;
    }

    public OrderItem(Long id, Goods goods, int number) {
        this.id = id;
        this.goods = goods;
        this.number = number;
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

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void addNumber(int number){
        this.number += number;
    }

    public long getTotalPrice(){
        return number * price;
    }


    @Override
    public String toString() {
        return "OrderItem{" +
                "id=" + id +
                ", goods=" + goods +
                ", number=" + number +
                ", price=" + price +
                '}';
    }
}
