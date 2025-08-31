package ua.edu.dnu.warehouse;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import ua.edu.dnu.warehouse.util.AlertUtil;
import ua.edu.dnu.warehouse.util.SceneUtil;

import java.io.IOException;

public class FXApplication extends Application {
    private ConfigurableApplicationContext context;

    @Override
    public void init() {
        ApplicationContextInitializer<GenericApplicationContext> initializer = ac -> {
            ac.registerBean(Application.class, () -> FXApplication.this);
            ac.registerBean(Parameters.class, this::getParameters);
            ac.registerBean(HostServices.class, this::getHostServices);
        };
        context = new SpringApplicationBuilder()
                .sources(SpringApplication.class)
                .initializers(initializer)
                .run(getParameters().getRaw().toArray(new String[0]));
    }

    @Override
    public void stop() {
        context.close();
        Platform.exit();
    }

    @Override
    public void start(Stage stage) throws Exception {
        Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> {
            throwable.printStackTrace();
            AlertUtil.alertError(throwable.getMessage());
        });
        loadScene(stage);
    }

    private void loadScene(Stage stage) throws IOException {
        FXMLLoader loader = SceneUtil.getFXMLLoader("login.fxml");
        loader.setControllerFactory(context::getBean);
        Parent root = loader.load();
        Scene scene = new Scene(root, root.prefWidth(-1), root.prefHeight(-1));
        stage.setTitle("WarehouseApp");
        stage.setScene(scene);
        stage.show();
    }
}
