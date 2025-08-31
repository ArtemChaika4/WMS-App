package ua.edu.dnu.warehouse.util;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ua.edu.dnu.warehouse.SpringContext;

import java.io.IOException;

public class SceneUtil {
    private static final String FXML_PATH = "fxml/";

    public static FXMLLoader getFXMLLoader(String fxml){
        String fxmlPath = FXML_PATH + fxml;
        return new FXMLLoader(SceneUtil.class.getClassLoader().getResource(fxmlPath));
    }

    public static void changeScene(ActionEvent event, String fxml){
        FXMLLoader fxmlLoader = getFXMLLoader(fxml);
        fxmlLoader.setControllerFactory(SpringContext::getBean);
        try {
            Parent root = fxmlLoader.load();
            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            double width = root.prefWidth(-1);
            double height = root.prefHeight(-1);
            Scene scene = new Scene(root, width, height);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.alertError("Не вдалося завантажити сцену: " + fxml);
        }
    }

}
