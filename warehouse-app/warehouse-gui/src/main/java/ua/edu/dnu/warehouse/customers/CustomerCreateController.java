package ua.edu.dnu.warehouse.customers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import org.springframework.stereotype.Component;
import ua.edu.dnu.warehouse.controls.SceneController;
import ua.edu.dnu.warehouse.model.Customer;
import ua.edu.dnu.warehouse.service.CustomerService;
import ua.edu.dnu.warehouse.validator.Validator;

@Component
public class CustomerCreateController {
    public TextField name;
    public TextField phone;
    public TextField patronymic;
    public TextField address;
    public TextField surname;
    private final CustomerService customerService;
    private final SceneController paneController;

    public CustomerCreateController(CustomerService customerService, SceneController paneController){
        this.customerService = customerService;
        this.paneController = paneController;
    }

    private boolean isCustomerValid(){
        return Validator.validateTextField(surname, Validator.NSP_REGEX) &&
                Validator.validateTextField(name, Validator.NSP_REGEX) &&
                Validator.validateTextField(patronymic, Validator.NSP_REGEX) &&
                Validator.validateTextField(phone, Validator.PHONE_REGEX);
    }

    @FXML
    private void addCustomer() {
        if (isCustomerValid()) {
            Customer customer = new Customer(name.getText(), surname.getText(),
                    patronymic.getText(), address.getText(), "+38" + phone.getText());
            if(customerService.getCustomerByPhone(customer.getPhone()).isPresent()){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Вже існує замовник з номером телефону: " + customer.getPhone());
                alert.showAndWait();
                return;
            }
            customerService.createCustomer(customer);
            paneController.setPrevContent();
        }
    }

    @FXML
    private void initialize() {
        Validator.addTextFieldValidation(surname, Validator.NSP_REGEX);
        Validator.addTextFieldValidation(name, Validator.NSP_REGEX);
        Validator.addTextFieldValidation(patronymic, Validator.NSP_REGEX);
        Validator.addTextFieldValidation(phone, Validator.PHONE_REGEX);
    }

}
