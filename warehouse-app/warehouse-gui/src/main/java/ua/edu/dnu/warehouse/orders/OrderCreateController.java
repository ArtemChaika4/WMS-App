package ua.edu.dnu.warehouse.orders;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;
import ua.edu.dnu.warehouse.controls.SceneController;
import ua.edu.dnu.warehouse.model.Customer;
import ua.edu.dnu.warehouse.model.Order;
import ua.edu.dnu.warehouse.model.OrderItem;
import ua.edu.dnu.warehouse.model.OrderStatus;
import ua.edu.dnu.warehouse.report.ReportCreator;
import ua.edu.dnu.warehouse.service.CustomerService;
import ua.edu.dnu.warehouse.service.OrderService;
import ua.edu.dnu.warehouse.util.AlertUtil;
import ua.edu.dnu.warehouse.validator.Validator;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class OrderCreateController {
    public TableView<OrderItem> table;
    public TableColumn<OrderItem, String> nameColumn;
    public TableColumn<OrderItem, String> descriptionColumn;
    public TableColumn<OrderItem, String> typeColumn;
    public TableColumn<OrderItem, String> amountColumn;
    public TableColumn<OrderItem, String> priceColumn;
    public TextField phone;
    public TextField name;
    public TextField address;
    public TextField total;
    public VBox nameBox;
    public VBox addressBox;
    private List<OrderItem> items;
    private long orderPrice;
    private Customer customer;
    private final OrderService orderService;
    private final CustomerService customerService;
    private final SceneController sceneController;

    public OrderCreateController(OrderService orderService, CustomerService customerService,
                                 SceneController sceneController) {
        this.orderService = orderService;
        this.customerService = customerService;
        this.sceneController = sceneController;
    }

    public void setItems(List<OrderItem> items){
        this.items = items;
        orderPrice = items.stream()
                .mapToLong(OrderItem::getTotalPrice)
                .sum();
        table.setItems(FXCollections.observableArrayList(items));
        total.setText(String.valueOf(orderPrice));
    }

    @FXML
    public void initialize(){
        OrderUtil.setOrderItemColumns(nameColumn, descriptionColumn, priceColumn, amountColumn, typeColumn);
        Validator.addTextFieldValidation(phone, Validator.PHONE_REGEX);
    }

    public void onCreateOrder() {
        if(customer == null){
            AlertUtil.alertError("Замовлення не містить замовника.\n" +
                    "Введіть номер телефону замовника та натисніть кнопку пошуку");
            return;
        }
        Order order = new Order(customer, LocalDateTime.now(), OrderStatus.CREATED, orderPrice);
        order.setItems(items);
        try {
            orderService.createOrder(order);
            createOrderReport(order);
            sceneController.setRootContent();
        }catch (Exception e){
            AlertUtil.alertError("Не вдалося створити замолвення: " + e.getMessage());
            throw e;
        }
    }

    private void createOrderReport(Order order){
        String path = System.getProperty("user.home") + "/Downloads";
        try {
            ReportCreator report = OrderUtil.createOrderReport(new File(path), order, table);
            report.saveAndShow();
        } catch (Exception e) {
            AlertUtil.alertError("Помилка при створенні квитанції на замовлення");
        }
    }

    public void onCreateCustomer() {
        sceneController.setNextContent("customer-create.fxml");
    }

    private boolean isValidPhone(){
        return Validator.validateTextField(phone, Validator.PHONE_REGEX);
    }

    public void searchCustomer() {
        if(!isValidPhone()){
            AlertUtil.alertError("Некоректно введений номер телефону");
            reset();
            return;
        }
        String newPhone = "+38" + phone.getText();
        Optional<Customer> newCustomer = customerService.getCustomerByPhone(newPhone);
        if(newCustomer.isEmpty()){
            AlertUtil.alertError("Не знайдено замовника з вказаним номером телефону: " + newPhone +
                    "\nНатисніть кнопку 'Зареєструвати замовника' для реєстрації нового замовника");
            reset();
            return;
        }
        customer = newCustomer.get();
        nameBox.setDisable(false);
        addressBox.setDisable(false);
        name.setText(customer.getFullName());
        address.setText(customer.getAddress());
    }

    private void reset(){
        if(customer != null){
            phone.setText(customer.getPhone().substring(3));
            name.setText(customer.getFullName());
            address.setText(customer.getAddress());
        }
    }

}
