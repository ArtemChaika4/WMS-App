package ua.edu.dnu.warehouse.model;

import jakarta.persistence.*;

@Entity
@Table(name = "goods")
public class Goods {
    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private int amount;
    @Column(nullable = false)
    private long price;
    @Enumerated(EnumType.STRING)
    private GoodsStatus status;
    @ManyToOne
    @JoinColumn(name = "type_id")
    private GoodsType type;

    public Goods(){
        name = "";
        description = "";
        amount = 0;
        price = 0;
        status = GoodsStatus.AVAILABLE;
        type = new GoodsType();
    }

    public Goods(String name, String description, int amount, long price,
                 GoodsStatus status, GoodsType type) {
        this.name = name;
        this.description = description;
        this.amount = amount;
        this.price = price;
        this.status = status;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String title) {
        this.name = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String author) {
        this.description = author;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public GoodsStatus getStatus() {
        return status;
    }

    public void setStatus(GoodsStatus status) {
        this.status = status;
    }

    public GoodsType getType() {
        return type;
    }

    public void setType(GoodsType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Goods{" +
                "id=" + id +
                ", title='" + name + '\'' +
                ", author='" + description + '\'' +
                ", amount=" + amount +
                ", price=" + price +
                ", status=" + status +
                ", type=" + type +
                '}';
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
