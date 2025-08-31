package ua.edu.dnu.warehouse.controls;


import javafx.event.ActionEvent;
import org.springframework.stereotype.Component;

@Component
public class MenuController {
    private final SceneController controller;

    public MenuController(SceneController controller) {
        this.controller = controller;
    }

    public void openGoods() {
        controller.setRootContent("goods.fxml");
    }

    public void openOrdersOperator() {
        controller.setRootContent("orders-operator.fxml");
    }

    public void openOrdersAdmin() {
        controller.setRootContent("orders-admin.fxml");
    }

    public void openOrdersWarehouse(){
        controller.setRootContent("orders-warehouse.fxml");
    }
    public void openAnalytic() {
        controller.setRootContent("analytic.fxml");
    }

    public void openOrderAnalytic() {
        controller.setRootContent("order-analytic.fxml");
    }

    public void openCustomers() {
        controller.setRootContent("customers.fxml");
    }

    public void openEmployees() {
        controller.setRootContent("employees.fxml");
    }

    public void openProfile() {
        controller.setRootContent("profile.fxml");
    }

    public void openLogs() {
        controller.setRootContent("logs.fxml");
    }

    public void openGoodsTypes() {
        controller.setRootContent("types.fxml");
    }

    public void openOrderSearch() {
        controller.setRootContent("order-search.fxml");
    }

    public void openChatAI() {
        controller.setRootContent("chat-ai.fxml");
    }
}
