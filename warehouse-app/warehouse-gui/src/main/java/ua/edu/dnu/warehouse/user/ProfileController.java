package ua.edu.dnu.warehouse.user;


import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.springframework.stereotype.Component;
import ua.edu.dnu.warehouse.controls.SceneController;
import ua.edu.dnu.warehouse.model.Employee;

@Component
public class ProfileController {
    public TextField surname;
    public TextField patronymic;
    public TextField name;
    public TextField position;
    public TextField email;
    private SessionStorage sessionStorage;
    private SceneController sceneController;

    public ProfileController(SessionStorage sessionStorage, SceneController sceneController) {
        this.sessionStorage = sessionStorage;
        this.sceneController = sceneController;
    }

    @FXML
    public void initialize() {
        Employee employee = sessionStorage.getUser();
        surname.setText(employee.getSurname());
        name.setText(employee.getName());
        patronymic.setText(employee.getPatronymic());
        email.setText(employee.getEmail());
        position.setText(employee.getPost().getLabel());
    }

    public void changePassword() {
        sceneController.setNextContent("change-password.fxml");
    }
}
