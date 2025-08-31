package ua.edu.dnu.warehouse.model;

import jakarta.persistence.*;

@Entity
@Table(name = "employees")
public class Employee {
    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String surname;
    private String patronymic;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    @Enumerated(EnumType.STRING)
    private Post post;

    public Employee(){
        name = "";
        surname = "";
        patronymic = "";
        email = "";
        password = "";
    }

    public Employee(String name, String surname, String patronymic,
                    String email, String password, Post post) {
        this.name = name;
        this.surname = surname;
        this.patronymic = patronymic;
        this.email = email;
        this.post = post;
        this.password = password;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String getFullName(){
        return surname + " " + name + " " + patronymic;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", patronymic='" + patronymic + '\'' +
                ", email='" + email + '\'' +
                ", position=" + post +
                '}';
    }
}
