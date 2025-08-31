package ua.edu.dnu.warehouse.employees;

import com.jfoenix.controls.JFXCheckBox;
import it.negste.peekablepasswordfield.PeekablePasswordField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import org.springframework.stereotype.Component;
import ua.edu.dnu.warehouse.controls.SceneController;
import ua.edu.dnu.warehouse.model.Employee;
import ua.edu.dnu.warehouse.model.Post;
import ua.edu.dnu.warehouse.service.EmployeeService;
import ua.edu.dnu.warehouse.util.AlertUtil;
import ua.edu.dnu.warehouse.validator.Validator;

@Component
public class EmployeeEditController {
    public TextField name;
    public TextField email;
    public TextField patronymic;
    public TextField surname;
    public PeekablePasswordField password;
    public ComboBox<Post> postList;
    public JFXCheckBox checkPasswordChange;
    private Employee employee;
    private final EmployeeService employeeService;
    private final SceneController sceneController;

    public EmployeeEditController(EmployeeService employeeService, SceneController sceneController) {
        this.employeeService = employeeService;
        this.sceneController = sceneController;
    }

    public void loadEmployee(Employee employee) {
        this.employee = employee;
        reset();
    }

    private boolean isEmployeeValid(){
        return Validator.validateTextField(surname, Validator.NSP_REGEX) &&
                Validator.validateTextField(name, Validator.NSP_REGEX) &&
                Validator.validateTextField(patronymic, Validator.NSP_REGEX) &&
                Validator.validateTextField(email, Validator.EMAIL_REGEX) &&
                (!checkPasswordChange.isSelected() || Validator.validateTextField(password, Validator.PASSWORD_REGEX)) &&
                postList.getValue() != null;
    }

    public void saveEmployee() {
        if(isEmployeeValid()){
            String newEmail = email.getText();
            if(!newEmail.equals(employee.getEmail()) && employeeService.getEmployeeByEmail(newEmail).isPresent()){
                AlertUtil.alertError("Вже існує співробітник з електронною адресою: " + newEmail);
                return;
            }
            employee.setName(name.getText());
            employee.setSurname(surname.getText());
            employee.setPatronymic(patronymic.getText());
            employee.setEmail(newEmail);
            employee.setPassword(password.getText());
            employee.setPost(postList.getValue());
            employeeService.updateEmployee(employee);
            if(checkPasswordChange.isSelected()){
                employeeService.adminChangePassword(email.getText(), password.getText());
            }
            sceneController.setPrevContent();
        }
    }

    @FXML
    private void initialize() {
        Validator.addTextFieldValidation(surname, Validator.NSP_REGEX);
        Validator.addTextFieldValidation(name, Validator.NSP_REGEX);
        Validator.addTextFieldValidation(patronymic, Validator.NSP_REGEX);
        Validator.addTextFieldValidation(password, Validator.PASSWORD_REGEX);
        Validator.addTextFieldValidation(email, Validator.EMAIL_REGEX);
        ObservableList<Post> positions = FXCollections.observableArrayList(Post.values());
        postList.setItems(positions);
        checkPasswordChange.selectedProperty().addListener((observable, oldValue, newValue) -> {
            password.clear();
            password.setDisable(!newValue);
        });
    }

    public void reset() {
        name.setText(employee.getName());
        surname.setText(employee.getSurname());
        patronymic.setText(employee.getPatronymic());
        email.setText(employee.getEmail());
        postList.setValue(employee.getPost());
        checkPasswordChange.selectedProperty().set(false);
    }

}
