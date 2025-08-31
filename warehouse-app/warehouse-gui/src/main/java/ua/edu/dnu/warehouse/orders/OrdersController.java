package ua.edu.dnu.warehouse.orders;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.stereotype.Component;
import ua.edu.dnu.warehouse.controls.SceneController;
import ua.edu.dnu.warehouse.field.CustomerField;
import ua.edu.dnu.warehouse.field.EntityField;
import ua.edu.dnu.warehouse.field.OrderField;
import ua.edu.dnu.warehouse.filter.OrderSpecification;
import ua.edu.dnu.warehouse.loader.TableDataLoader;
import ua.edu.dnu.warehouse.model.Order;
import ua.edu.dnu.warehouse.model.OrderStatus;
import ua.edu.dnu.warehouse.model.Post;
import ua.edu.dnu.warehouse.report.ReportCreator;
import ua.edu.dnu.warehouse.report.Reportable;
import ua.edu.dnu.warehouse.service.OrderService;
import ua.edu.dnu.warehouse.user.SessionStorage;
import ua.edu.dnu.warehouse.util.AlertUtil;
import ua.edu.dnu.warehouse.util.FormatUtil;

import java.io.File;

@Component
public class OrdersController implements Reportable {
    @FXML
    private TableColumn<Order, Integer> idColumn;
    @FXML
    private TableColumn<Order, String> nameColumn;
    @FXML
    private TableColumn<Order, String> phoneColumn;
    @FXML
    private TableColumn<Order, String> dateColumn;
    @FXML
    private TableColumn<Order, Integer> priceColumn;
    @FXML
    private TableView<Order> table;
    @FXML
    private ComboBox<EntityField<?>> sortList;
    @FXML
    private ComboBox<OrderStatus> statusList;
    @FXML
    private TextField searchField;
    private final OrderService orderService;
    private OrderSpecification specifications;
    private final SceneController sceneController;
    private final SessionStorage sessionStorage;
    private TableDataLoader<Order> tableDataLoader;

    public OrdersController(OrderService orderService, SceneController sceneController,
                            SessionStorage sessionStorage) {
        this.orderService = orderService;
        this.sceneController = sceneController;
        this.sessionStorage = sessionStorage;
    }

    @FXML
    public void sort(){
        specifications.sortedBy(sortList.getValue());
        tableDataLoader.update();
    }

    public void setStatus(){
        specifications.withStatus(statusList.getValue());
        tableDataLoader.update();
    }

    @FXML
    public void onCreate() {
        sceneController.setNextContent("order-form.fxml");
    }

    @FXML
    private void onView(){
        Order order = table.getSelectionModel().getSelectedItem();
        if (order != null) {
            sceneController.setNextContent("order-view.fxml", OrderViewController.class)
                    .loadOrder(order);
        }
    }

    @FXML
    private void onCancel()  {
        Order order = table.getSelectionModel().getSelectedItem();
        if (order != null && !order.getStatus().isCanceled()) {
            AlertUtil.alertWarning(
                    "Попередження про скасування замовлення",
                    "Після виконання операції замовлення буде скасовано")
                    .ifPresent(b -> {
                        orderService.changeOrderStatus(order.getId(), OrderStatus.CANCELED);
                        tableDataLoader.update();
                    });
        }
    }

    @FXML
    private void initialize() {
        specifications = new OrderSpecification();
        tableDataLoader = new TableDataLoader<>(orderService, table, specifications);
        ObservableList<EntityField<?>> sorts = FXCollections.observableArrayList(
                OrderField.DATE, OrderField.TOTAL, CustomerField.SURNAME, CustomerField.NAME);
        sortList.setItems(sorts);
        idColumn.setCellValueFactory(new PropertyValueFactory<>(OrderField.ID.getName()));
        nameColumn.setCellValueFactory(order -> new SimpleStringProperty(
                order.getValue().getCustomer().getFullName()));
        phoneColumn.setCellValueFactory(order -> new SimpleStringProperty(
                order.getValue().getCustomer().getPhone()));
        dateColumn.setCellValueFactory(order -> new SimpleStringProperty(
                FormatUtil.formatDateTime(order.getValue().getDate())));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>(OrderField.TOTAL.getName()));
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            specifications.containsValue(newValue);
            tableDataLoader.update();
        });
        ObservableList<OrderStatus> statuses = FXCollections.observableArrayList();
        Post post = sessionStorage.getUser().getPost();
        if(post == Post.WAREHOUSEMAN){
            statuses.addAll(OrderStatus.CREATED, OrderStatus.PROCESSED, OrderStatus.READY);
            statusList.setValue(OrderStatus.CREATED);
        }else {
            statuses.addAll(OrderStatus.values());
        }
        statusList.setItems(statuses);
        setStatus();
    }

    public void onDelete() {
        Order order = table.getSelectionModel().getSelectedItem();
        if (order != null) {
            AlertUtil.alertWarning(
                    "Попередження про видалення замовлення",
                    "Після виконання операції інформація про замовлення буде назавжди втрачена")
                    .ifPresent(b -> {
                        orderService.deleteOrder(order.getId());
                        table.getItems().remove(order);
                    });
        }
    }


    public void onEdit() {
        Order order = table.getSelectionModel().getSelectedItem();
        if (order != null) {
            sceneController.setNextContent("order-edit.fxml", OrderEditController.class)
                    .loadOrder(order);
        }
    }

    @Override
    public void createReport(File directory) throws Exception {
        ReportCreator report = new ReportCreator(directory);
        report.create("замовлення");
        report.addTitle("Замовлення");
        report.addText("Застосовані параметри фільтрації:\n");
        report.addText("Пошук за ключовим словом: ");
        String searchText = searchField.getText().isEmpty() ? "—" :
                "'" + searchField.getText() + "'";
        report.addText(searchText + "\n");
        report.addText("Параметри сортування: ");
        String sortBy = sortList.getValue() == null ? "—" :
                sortList.getValue().getLabel();
        report.addText(sortBy + "\n");
        report.addText("Статус замовлення: ");
        String status = statusList.getValue() == null ? "Усі" :
                statusList.getValue().getLabel();
        report.addText(status + "\n");
        report.addTable(table);
        report.saveAndShow();
    }
}
