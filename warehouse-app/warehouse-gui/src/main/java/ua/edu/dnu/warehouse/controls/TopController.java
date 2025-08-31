package ua.edu.dnu.warehouse.controls;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;

import org.springframework.stereotype.Component;
import ua.edu.dnu.warehouse.model.Employee;
import ua.edu.dnu.warehouse.report.Reportable;
import ua.edu.dnu.warehouse.user.SessionStorage;
import ua.edu.dnu.warehouse.util.SceneUtil;

import java.io.File;

@Component
public class TopController {
    public Label fullNameLabel;
    public Label emailLabel;
    private final SceneController sceneController;
    private final SessionStorage sessionStorage;


    public TopController(SceneController sceneController, SessionStorage sessionStorage) {
        this.sceneController = sceneController;
        this.sessionStorage = sessionStorage;
    }

    @FXML
    private void initialize() {
        Employee employee = sessionStorage.getUser();
        fullNameLabel.setText(employee.getFullName());
        emailLabel.setText(employee.getEmail());
    }

    public void saveToPDF() {
        Object controller = sceneController.getContentNode().controller();
        if(controller instanceof Reportable reportable){
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File directory = directoryChooser.showDialog(null);
            if(directory != null){
                try {
                    reportable.createReport(directory);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void logOut(ActionEvent event) {
        sessionStorage.clear();
        SceneUtil.changeScene(event, "login.fxml");
    }

    public void goBack() {
       sceneController.setPrevContent();
    }

    public void onProfileClicked() {
        sceneController.setNextContent("profile.fxml");
    }

    public void openChatAI() {
        sceneController.setNextContent("chat-ai.fxml");
    }
}
