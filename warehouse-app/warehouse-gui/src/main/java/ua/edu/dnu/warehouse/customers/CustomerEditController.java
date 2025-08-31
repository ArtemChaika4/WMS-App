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
public class CustomerEditController {
    public TextField name;
    public TextField phone;
    public TextField patronymic;
    public TextField address;
    public TextField surname;
    private final CustomerService customerService;
    private final SceneController paneController;

    public CustomerEditController(CustomerService customerService, SceneController paneController){
        this.customerService = customerService;
        this.paneController = paneController;
    }

    private Customer customer;
    public void loadCustomer(Customer customer){
        this.customer = customer;
        reset();
    }

    public void reset() {
        name.setText(customer.getName());
        surname.setText(customer.getSurname());
        patronymic.setText(customer.getPatronymic());
        address.setText(customer.getAddress());
        phone.setText(customer.getPhone().substring(3));
    }

    private boolean isCustomerValid(){
        return Validator.validateTextField(surname, Validator.NSP_REGEX) &&
                Validator.validateTextField(name, Validator.NSP_REGEX) &&
                Validator.validateTextField(patronymic, Validator.NSP_REGEX) &&
                Validator.validateTextField(phone, Validator.PHONE_REGEX);
    }

    public void saveCustomer() {
        if(isCustomerValid()){
            String newPhone = "+38" + phone.getText();
            if(!newPhone.equals(customer.getPhone()) &&
                    customerService.getCustomerByPhone(newPhone).isPresent()){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Вже існує замовник з номером телефону: " + newPhone);
                alert.showAndWait();
                return;
            }
            customer.setName(name.getText());
            customer.setSurname(surname.getText());
            customer.setPatronymic(patronymic.getText());
            customer.setAddress(address.getText());
            customer.setPhone(newPhone);
            customerService.updateCustomer(customer);
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
