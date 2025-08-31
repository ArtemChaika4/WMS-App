package ua.edu.dnu.warehouse.model;

import jakarta.persistence.*;
@Entity
@Table(name = "customers")
public class Customer {
    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String surname;
    @Column(nullable = false)
    private String phone;
    private String patronymic;
    @Column(nullable = false)
    private String address;

    public Customer(){
        name = "";
        surname = "";
        patronymic = "";
        address = "";
        phone = "";
    }

    public Customer(String name, String surname, String patronymic, String address, String phone) {
        this.name = name;
        this.surname = surname;
        this.patronymic = patronymic;
        this.address = address;
        this.phone = phone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }
    public String getFullName(){
        return surname + " " + name + " " + patronymic;
    }
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", patronymic='" + patronymic + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }

}
