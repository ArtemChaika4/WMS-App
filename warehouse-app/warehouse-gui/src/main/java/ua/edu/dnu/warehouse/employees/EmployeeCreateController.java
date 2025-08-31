package ua.edu.dnu.warehouse.employees;

import it.negste.peekablepasswordfield.PeekablePasswordField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.springframework.stereotype.Component;
import ua.edu.dnu.warehouse.controls.SceneController;
import ua.edu.dnu.warehouse.model.Employee;
import ua.edu.dnu.warehouse.model.Post;
import ua.edu.dnu.warehouse.service.EmployeeService;
import ua.edu.dnu.warehouse.util.AlertUtil;
import ua.edu.dnu.warehouse.validator.Validator;

@Component
public class EmployeeCreateController {
    public TextField name;
    public TextField email;
    public TextField patronymic;
    public TextField surname;
    public PeekablePasswordField password;
    public ComboBox<Post> postList;
    private final EmployeeService employeeService;
    private final SceneController sceneController;

    public EmployeeCreateController(EmployeeService employeeService, SceneController sceneController) {
        this.employeeService = employeeService;
        this.sceneController = sceneController;
    }


    private boolean isEmployeeValid(){
        return Validator.validateTextField(surname, Validator.NSP_REGEX) &&
                Validator.validateTextField(name, Validator.NSP_REGEX) &&
                Validator.validateTextField(patronymic, Validator.NSP_REGEX) &&
                Validator.validateTextField(email, Validator.EMAIL_REGEX) &&
                Validator.validateTextField(password, Validator.PASSWORD_REGEX) &&
                postList.getValue() != null;
    }

    @FXML
    private void addEmployee() {
        if (isEmployeeValid()) {
            Employee employee = new Employee(name.getText(), surname.getText(), patronymic.getText(),
                    email.getText(), password.getText(), postList.getValue());
            if(employeeService.getEmployeeByEmail(email.getText()).isPresent()){
                AlertUtil.alertError("Вже існує співробітник з електронною адресою: " + email.getText());
                return;
            }
            employeeService.createEmployee(employee);
            sceneController.setPrevContent();
        }
    }

    @FXML
    private void initialize() {
        Validator.addTextFieldValidation(surname, Validator.NSP_REGEX);
        Validator.addTextFieldValidation(name, Validator.NSP_REGEX);
        Validator.addTextFieldValidation(patronymic, Validator.NSP_REGEX);
        Validator.addTextFieldValidation(email, Validator.EMAIL_REGEX);
        Validator.addTextFieldValidation(password, Validator.PASSWORD_REGEX);
        ObservableList<Post> positions = FXCollections.observableArrayList(Post.values());
        postList.setItems(positions);
    }
}
