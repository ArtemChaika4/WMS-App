package ua.edu.dnu.warehouse;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringApplication {
    public static void main(String[] args) {
        Application.launch(FXApplication.class, args);
    }
}
