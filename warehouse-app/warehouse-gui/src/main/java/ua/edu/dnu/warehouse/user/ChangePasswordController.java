package ua.edu.dnu.warehouse.user;

import it.negste.peekablepasswordfield.PeekablePasswordField;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.springframework.stereotype.Component;
import ua.edu.dnu.warehouse.controls.SceneController;
import ua.edu.dnu.warehouse.model.Employee;
import ua.edu.dnu.warehouse.service.EmployeeService;
import ua.edu.dnu.warehouse.util.AlertUtil;
import ua.edu.dnu.warehouse.validator.Validator;

@Component
public class ChangePasswordController {
    public TextField emailField;
    public Label oldErrorLabel;
    public Label newErrorLabel;
    public Label confirmErrorLabel;
    public PeekablePasswordField oldPassword;
    public PeekablePasswordField newPassword;
    public PeekablePasswordField confirmPassword;
    private final EmployeeService employeeService;
    private final SessionStorage sessionStorage;
    private final SceneController sceneController;



    public ChangePasswordController(EmployeeService employeeService, SessionStorage sessionStorage,
                                    SceneController sceneController){
        this.employeeService = employeeService;
        this.sessionStorage = sessionStorage;
        this.sceneController = sceneController;
    }

    @FXML
    public void initialize() {
        Employee employee = sessionStorage.getUser();
        emailField.setText(employee.getEmail());
        Validator.addTextFieldValidation(oldPassword, Validator.PASSWORD_REGEX);
        Validator.addTextFieldValidation(newPassword, Validator.PASSWORD_REGEX);
    }

    private boolean validatePassword(TextField password, Label error){
        boolean isValid = Validator.validateTextField(password, Validator.PASSWORD_REGEX);
        error.setVisible(!isValid);
        return isValid;
    }

    private boolean checkOldPassword(){
        String email = emailField.getText();
        String password = oldPassword.getText();
        boolean isCorrect = employeeService.checkEmployeePassword(email, password);
        oldErrorLabel.setVisible(!isCorrect);
        return isCorrect;
    }

    private boolean checkConfirmPassword(){
        String newPass = newPassword.getText();
        String confirmPass = confirmPassword.getText();
        boolean isConfirmed = confirmPass.equals(newPass);
        confirmErrorLabel.setVisible(!isConfirmed);
        return isConfirmed;
    }

    private boolean validate(){
        oldErrorLabel.setVisible(false);
        newErrorLabel.setVisible(false);
        confirmErrorLabel.setVisible(false);
        return validatePassword(newPassword, newErrorLabel) &&
                checkConfirmPassword() && checkOldPassword();
    }

    public void changePassword() {
        if(validate()){
            String email = emailField.getText();
            String newPass = newPassword.getText();
            String oldPass = oldPassword.getText();
            AlertUtil.alertWarning(
                    "Попередження про зміну пароля",
                    "Після виконання операції пароль буде змінено")
                    .ifPresent(b -> {
                        employeeService.changePassword(email, oldPass, newPass);
                        sceneController.setPrevContent();
                    });
        }
    }
}
