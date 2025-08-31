package ua.edu.dnu.warehouse.util;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class AlertUtil {
    public static Optional<ButtonType> alertWarning(String header, String context){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Увага!");
        alert.setHeaderText(header);
        alert.setContentText(context);
        return alert.showAndWait();
    }

    public static void alertInfo(String context){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Повідомлення!");
        alert.setHeaderText("Інформація про виконання операції");
        alert.setContentText(context);
        alert.showAndWait();
    }

    public static void alertError(String context){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Помилка!");
        alert.setHeaderText("Виникла помилка");
        alert.setContentText(context);
        alert.showAndWait();
    }
}
