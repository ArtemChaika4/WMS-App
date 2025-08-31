package ua.edu.dnu.warehouse.customers;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ua.edu.dnu.warehouse.controls.SceneController;
import ua.edu.dnu.warehouse.field.CustomerField;
import ua.edu.dnu.warehouse.filter.SpecificationBuilder;
import ua.edu.dnu.warehouse.loader.TableDataLoader;
import ua.edu.dnu.warehouse.model.Customer;

import org.springframework.stereotype.Component;
import ua.edu.dnu.warehouse.report.ReportCreator;
import ua.edu.dnu.warehouse.report.Reportable;
import ua.edu.dnu.warehouse.service.CustomerService;
import ua.edu.dnu.warehouse.filter.CustomerSpecification;
import ua.edu.dnu.warehouse.service.SearchService;
import ua.edu.dnu.warehouse.util.AlertUtil;

import java.io.File;


@Component
public class CustomersController implements Reportable {
    public TableView<Customer> table;
    public TableColumn<Customer, String> nameColumn;
    public TableColumn<Customer, String> surnameColumn;
    public TableColumn<Customer, String> patronymicColumn;
    public TableColumn<Customer, String> addressColumn;
    public TableColumn<Customer, String> phoneColumn;
    public ComboBox<CustomerField> sortList;
    public TextField searchField;
    private final CustomerService customerService;
    private final SceneController sceneController;
    private CustomerSpecification customerSpecification;
    private TableDataLoader<Customer> tableDataLoader;


    public CustomersController(CustomerService customerService, SceneController sceneController){
        this.customerService = customerService;
        this.sceneController = sceneController;
    }

    public void onCreate() {
        sceneController.setNextContent("customer-create.fxml");
    }

    public void onDelete() {
        Customer customer = table.getSelectionModel().getSelectedItem();
        if(customer != null) {
            AlertUtil.alertWarning(
                    "Попередження про видалення замовника",
                    "При видаленні замовника видаляються всі його замовлення")
                    .ifPresent(b -> {
                        customerService.deleteCustomer(customer.getId());
                        table.getItems().remove(customer);
                    });
        }
    }

    @FXML
    private void onEdit(){
        Customer customer = table.getSelectionModel().getSelectedItem();
        if (customer != null) {
            sceneController.setNextContent("customer-edit.fxml", CustomerEditController.class)
                    .loadCustomer(customer);
        }
    }

    public void initialize() {
        customerSpecification = new CustomerSpecification();
        tableDataLoader = new TableDataLoader<>(customerService, table, customerSpecification);
        ObservableList<CustomerField> sorts = FXCollections.observableArrayList(CustomerField.values());
        sortList.setItems(sorts);
        surnameColumn.setCellValueFactory(new PropertyValueFactory<>(CustomerField.SURNAME.getName()));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>(CustomerField.NAME.getName()));
        patronymicColumn.setCellValueFactory(new PropertyValueFactory<>(CustomerField.PATRONYMIC.getName()));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>(CustomerField.ADDRESS.getName()));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>(CustomerField.PHONE.getName()));
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            customerSpecification.containsValue(newValue);
            tableDataLoader.update();
        });
        tableDataLoader.update();
    }

    public void sort() {
        customerSpecification.sortedBy(sortList.getValue());
        tableDataLoader.update();
    }

    @Override
    public void createReport(File directory) throws Exception {
        ReportCreator report = new ReportCreator(directory);
        report.create("замовники");
        report.addTitle("Замовники");
        report.addText("Застосовані параметри фільтрації:\n");
        report.addText("Пошук за ключовим словом: ");
        String searchText = searchField.getText().isEmpty() ? "—" :
                "'" + searchField.getText() + "'";
        report.addText(searchText + "\n");
        report.addText("Параметри сортування: ");
        String sortBy = sortList.getValue() == null ? "—" :
                sortList.getValue().getLabel();
        report.addText(sortBy + "\n");
        report.addTable(table);
        report.saveAndShow();
    }
}
