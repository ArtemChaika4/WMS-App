package ua.edu.dnu.warehouse.controls;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import org.springframework.stereotype.Component;

@Component
public class MainController {
    @FXML
    public BorderPane mainPane;
    private final SceneController controller;


    public MainController(SceneController controller) {
        this.controller = controller;
    }

    public void initialize() {
        controller.setPane(mainPane);
    }
}
