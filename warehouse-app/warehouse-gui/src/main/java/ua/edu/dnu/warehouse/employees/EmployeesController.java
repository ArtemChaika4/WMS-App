package ua.edu.dnu.warehouse.employees;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.stereotype.Component;
import ua.edu.dnu.warehouse.controls.SceneController;
import ua.edu.dnu.warehouse.field.EmployeeField;
import ua.edu.dnu.warehouse.filter.EmployeeSpecification;
import ua.edu.dnu.warehouse.loader.TableDataLoader;
import ua.edu.dnu.warehouse.model.Employee;
import ua.edu.dnu.warehouse.model.Post;
import ua.edu.dnu.warehouse.report.ReportCreator;
import ua.edu.dnu.warehouse.report.Reportable;
import ua.edu.dnu.warehouse.service.EmployeeService;
import ua.edu.dnu.warehouse.user.SessionStorage;
import ua.edu.dnu.warehouse.util.AlertUtil;

import java.io.File;
import java.util.Objects;

@Component
public class EmployeesController implements Reportable {
    @FXML
    private TableColumn<Employee, String> nameColumn;
    @FXML
    private TableColumn<Employee, String> surnameColumn;
    @FXML
    private TableColumn<Employee, String> patronymicColumn;
    @FXML
    private TableColumn<Employee, String> emailColumn;
    @FXML
    private TableColumn<Employee, String> postColumn;
    @FXML
    private TableView<Employee> table;
    @FXML
    private ComboBox<EmployeeField> sortList;
    @FXML
    private ComboBox<Post> postList;
    @FXML
    private TextField searchField;
    private EmployeeSpecification specifications;
    private EmployeeService employeeService;
    private SceneController sceneController;
    private SessionStorage sessionStorage;
    private TableDataLoader<Employee> tableDataLoader;


    public EmployeesController(EmployeeService employeeService, SceneController sceneController,
                               SessionStorage sessionStorage) {
        this.employeeService = employeeService;
        this.sceneController = sceneController;
        this.sessionStorage = sessionStorage;
    }

    @FXML
    public void sort(){
        specifications.sortedBy(sortList.getValue());
        tableDataLoader.update();
    }

    public void setPost(){
        specifications.withPost(postList.getValue());
        tableDataLoader.update();
    }

    @FXML
    public void onCreate() {
        sceneController.setNextContent("employee-create.fxml");
    }

    private boolean isNotActiveUser(Employee employee){
        return !Objects.equals(employee.getId(), sessionStorage.getUser().getId());
    }

    @FXML
    private void onEdit(){
        Employee employee = table.getSelectionModel().getSelectedItem();
        if (employee != null && isNotActiveUser(employee)) {
            sceneController.setNextContent("employee-edit.fxml", EmployeeEditController.class)
                    .loadEmployee(employee);
        }
    }

    @FXML
    private void onDelete()  {
        Employee employee = table.getSelectionModel().getSelectedItem();
        if (employee != null && isNotActiveUser(employee)) {
            AlertUtil.alertWarning(
                    "Попередження про видалення працівника",
                    "Після виконання операції працівника буде видалено без можливості відновлення")
                    .ifPresent(b -> {
                        employeeService.deleteEmployee(employee.getId());
                        table.getItems().remove(employee);
                    });
        }
    }

    @FXML
    private void initialize() {
        specifications = new EmployeeSpecification();
        tableDataLoader = new TableDataLoader<>(employeeService, table, specifications);
        ObservableList<EmployeeField> sorts = FXCollections.observableArrayList(
                EmployeeField.NAME, EmployeeField.SURNAME, EmployeeField.PATRONYMIC);
        sortList.setItems(sorts);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>(EmployeeField.NAME.getName()));
        surnameColumn.setCellValueFactory(new PropertyValueFactory<>(EmployeeField.SURNAME.getName()));
        patronymicColumn.setCellValueFactory(new PropertyValueFactory<>(EmployeeField.PATRONYMIC.getName()));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>(EmployeeField.EMAIL.getName()));
        postColumn.setCellValueFactory(employee -> new SimpleStringProperty(
                employee.getValue().getPost() == null ? "—" :
                        employee.getValue().getPost().getLabel()));
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            specifications.containsValue(newValue);
            tableDataLoader.update();
        });
        ObservableList<Post> positions = FXCollections.observableArrayList(Post.values());
        postList.setItems(positions);
        tableDataLoader.update();
    }

    @Override
    public void createReport(File directory) throws Exception {
        ReportCreator report = new ReportCreator(directory);
        report.create("працівники");
        report.addTitle("Працівники");
        report.addText("Застосовані параметри фільтрації:\n");
        report.addText("Пошук за ключовим словом: ");
        String searchText = searchField.getText().isEmpty() ? "—" :
                "'" + searchField.getText() + "'";
        report.addText(searchText + "\n");
        report.addText("Параметри сортування: ");
        String sortBy = sortList.getValue() == null ? "—" :
                sortList.getValue().getLabel();
        report.addText(sortBy + "\n");
        report.addText("Посада працівника: ");
        String position = postList.getValue() == null ? "Усі" :
                postList.getValue().getLabel();
        report.addText(position + "\n");
        report.addTable(table);
        report.saveAndShow();
    }
}
