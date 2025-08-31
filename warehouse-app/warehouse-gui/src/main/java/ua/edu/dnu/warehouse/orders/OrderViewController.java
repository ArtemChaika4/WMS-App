package ua.edu.dnu.warehouse.orders;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.stereotype.Component;
import ua.edu.dnu.warehouse.field.OrderLogField;
import ua.edu.dnu.warehouse.model.Order;
import ua.edu.dnu.warehouse.model.OrderItem;
import ua.edu.dnu.warehouse.model.OrderLog;
import ua.edu.dnu.warehouse.report.ReportCreator;
import ua.edu.dnu.warehouse.report.Reportable;
import ua.edu.dnu.warehouse.service.OrderService;
import ua.edu.dnu.warehouse.util.FormatUtil;

import java.io.File;
import java.util.List;

@Component
public class OrderViewController implements Reportable {
    public TableView<OrderItem> table;
    public TableColumn<OrderItem, String> nameColumn;
    public TableColumn<OrderItem, String> descriptionColumn;
    public TableColumn<OrderItem, String> typeColumn;
    public TableColumn<OrderItem, String> amountColumn;
    public TableColumn<OrderItem, String> priceColumn;
    public TableView<OrderLog> logTable;
    public TableColumn<OrderLog, String> statusColumn;
    public TableColumn<OrderLog, String> timestampColumn;
    public TableColumn<OrderLog, String> emailColumn;
    public TableColumn<OrderLog, String> positionColumn;
    public TextField phone;
    public TextField name;
    public TextField address;
    public TextField total;
    public TextField number;
    public TextField date;
    public TextField status;
    private Order order;
    private final OrderService orderService;

    public OrderViewController(OrderService orderService) {
        this.orderService = orderService;
    }

    public void loadOrder(Order order){
        this.order = order;
        order.setItems(orderService.getOrderItems(order));
        OrderUtil.setOrderData(order, table, phone, name, address, total, number, date, status);
        List<OrderLog> logs = orderService.getOrderLogs(order);
        logTable.setItems(FXCollections.observableArrayList(logs));
    }


    public void initialize(){
        OrderUtil.setOrderItemColumns(nameColumn, descriptionColumn, priceColumn, amountColumn, typeColumn);
        statusColumn.setCellValueFactory(log -> new SimpleStringProperty(
                log.getValue().getStatus() == null ? "—" :
                        log.getValue().getStatus().getLabel()));
        timestampColumn.setCellValueFactory(log -> new SimpleStringProperty(
                FormatUtil.formatDateTime(log.getValue().getTimestamp())));
        emailColumn.setCellValueFactory(log -> new SimpleStringProperty(
                log.getValue().getEmployee() == null ? "—" :
                        log.getValue().getEmployee().getEmail()));
        positionColumn.setCellValueFactory(log -> new SimpleStringProperty(
                log.getValue().getEmployee() == null ? "—" :
                        log.getValue().getEmployee().getPost().getLabel()));
    }

    @Override
    public void createReport(File directory) throws Exception {
        ReportCreator report = OrderUtil.createOrderReport(directory, order, table);
        report.addTitle("Історія замовлення");
        report.addTable(logTable);
        report.saveAndShow();
    }
}
