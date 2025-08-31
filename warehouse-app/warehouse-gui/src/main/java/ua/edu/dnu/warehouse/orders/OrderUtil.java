package ua.edu.dnu.warehouse.orders;

import com.itextpdf.text.DocumentException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import ua.edu.dnu.warehouse.field.OrderItemField;
import ua.edu.dnu.warehouse.model.Customer;
import ua.edu.dnu.warehouse.model.Order;
import ua.edu.dnu.warehouse.model.OrderItem;
import ua.edu.dnu.warehouse.report.ReportCreator;
import ua.edu.dnu.warehouse.util.FormatUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class OrderUtil {
    public static void setOrderData(Order order, TableView<OrderItem> table,
                                    TextField phone, TextField name, TextField address, TextField total,
                                    TextField number, TextField date, TextField status){
        table.setItems(FXCollections.observableArrayList(order.getItems()));
        total.setText(String.valueOf(order.getTotal()));
        phone.setText(order.getCustomer().getPhone());
        name.setText(order.getCustomer().getFullName());
        address.setText(order.getCustomer().getAddress());
        number.setText(String.valueOf(order.getId()));
        date.setText(FormatUtil.formatDateTime(order.getDate()));
        status.setText(order.getStatus().getLabel());
    }

    public static void setOrderItemColumns(TableColumn<OrderItem, String> nameColumn,
                                           TableColumn<OrderItem, String> descriptionColumn,
                                           TableColumn<OrderItem, String> priceColumn,
                                           TableColumn<OrderItem, String> amountColumn,
                                           TableColumn<OrderItem, String> typeColumn){
        nameColumn.setCellValueFactory(item -> new SimpleStringProperty(
                item.getValue().getGoods().getName()));
        descriptionColumn.setCellValueFactory(item -> new SimpleStringProperty(
                item.getValue().getGoods().getDescription()));
        priceColumn.setCellValueFactory(item -> new SimpleStringProperty(
                String.valueOf(item.getValue().getTotalPrice())));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>(OrderItemField.NUMBER.getName()));
        typeColumn.setCellValueFactory(goods -> new SimpleStringProperty(
                goods.getValue().getGoods().getType() == null ? "—" :
                        goods.getValue().getGoods().getType().getName()));
    }

    public static ReportCreator createOrderReport(File directory, Order order, TableView<OrderItem> table)
            throws DocumentException, FileNotFoundException {
        ReportCreator report = new ReportCreator(directory);
        report.create("замовлення №" + order.getId());
        report.addTitle("Інформація про замовника");
        Customer customer = order.getCustomer();
        report.addText("Номер телефону: ");
        report.addText(customer.getPhone() + "\n");
        report.addText("ПІБ: ");
        report.addText(customer.getFullName() + "\n");
        report.addText("Адреса: ");
        report.addText(customer.getAddress() + "\n");
        report.addTitle("Інформація про замовлення");
        report.addText("Номер замовлення: ");
        report.addText(order.getId() + "\n");
        report.addText("Дата та час створення: ");
        report.addText(FormatUtil.formatDateTime(order.getDate()) + "\n");
        report.addText("Статус замовлення: ");
        report.addText(order.getStatus().getLabel() + "\n");
        report.addTitle("Товари, що входять до замовлення");
        report.addTable(table);
        report.addText("Сума до сплати: ");
        report.addText(order.getTotal() + " грн\n");
        return report;
    }
}
