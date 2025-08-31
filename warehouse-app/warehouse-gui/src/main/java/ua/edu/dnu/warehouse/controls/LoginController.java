package ua.edu.dnu.warehouse.controls;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.springframework.stereotype.Component;
import ua.edu.dnu.warehouse.model.Employee;
import ua.edu.dnu.warehouse.service.EmployeeService;
import ua.edu.dnu.warehouse.user.SessionStorage;
import ua.edu.dnu.warehouse.util.AlertUtil;
import ua.edu.dnu.warehouse.util.SceneUtil;
import ua.edu.dnu.warehouse.validator.Validator;

import java.util.Optional;

@Component
public class LoginController {
    public TextField emailField;
    public TextField passwordField;
    public Label errorLabel;
    private final EmployeeService employeeService;
    private final SessionStorage sessionStorage;
    private final SceneController sceneController;

    public LoginController(EmployeeService employeeService, SessionStorage sessionStorage,
                           SceneController sceneController) {
        this.employeeService = employeeService;
        this.sessionStorage = sessionStorage;
        this.sceneController = sceneController;
    }

    @FXML
    private void initialize() {
        Validator.addTextFieldValidation(emailField, Validator.EMAIL_REGEX);
        errorLabel.setVisible(false);
    }

    public void logIn(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();
        if(email.isBlank() || password.isBlank()){
            errorLabel.setVisible(true);
            return;
        }
        Employee employee = employeeService.loginEmployee(email, password);
        if(employee == null){
            errorLabel.setVisible(true);
            return;
        }
        if(employee.getPost() == null){
            AlertUtil.alertError("Не вдалося визначити посаду працівника");
            return;
        }
        sessionStorage.setUser(employee);
        SceneUtil.changeScene(event, "main.fxml");
        setEmployeeScene(employee);
    }

    private void setEmployeeScene(Employee employee){
        String menu = switch (employee.getPost()) {
            case OPERATOR -> "operator-menu.fxml";
            case WAREHOUSEMAN -> "warehouseman-menu.fxml";
            case ADMINISTRATOR -> "admin-menu.fxml";
        };
        sceneController.setMenu(menu);
        sceneController.setTop("top.fxml");
        sceneController.setRootContent("profile.fxml");
    }
}
