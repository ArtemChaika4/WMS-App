package ua.edu.dnu.warehouse.orders;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.springframework.stereotype.Component;
import ua.edu.dnu.warehouse.controls.SceneController;
import ua.edu.dnu.warehouse.model.Order;
import ua.edu.dnu.warehouse.model.Post;
import ua.edu.dnu.warehouse.service.OrderService;
import ua.edu.dnu.warehouse.user.SessionStorage;

import java.util.Optional;

@Component
public class OrderSearchController {
    public TextField number;
    public Label errorLabel;
    private final OrderService orderService;
    private final SceneController sceneController;
    private final SessionStorage sessionStorage;

    public OrderSearchController(OrderService orderService, SessionStorage sessionStorage,
                                 SceneController sceneController) {
        this.orderService = orderService;
        this.sceneController = sceneController;
        this.sessionStorage = sessionStorage;
    }

    public void searchOrder() {

        try {
            Long orderId = Long.parseLong(number.getText());
            Optional<Order> order = orderService.getOrderById(orderId);
            if(order.isEmpty()){
                errorLabel.setText("За вказаним номером не знайдено замовлення");
            }else {
                setNextScene(order.get());
                errorLabel.setText("");
                number.setText("");
            }
        }catch (Exception e){
            errorLabel.setText("Виникла помилка при пошуку замовлення");
        }
    }

    private void setNextScene(Order order){
        Post post = sessionStorage.getUser().getPost();
        if(post == Post.WAREHOUSEMAN){
            sceneController.setNextContent("order-close.fxml", OrderEditController.class)
                    .loadOrder(order);
        }else {
            sceneController.setNextContent("order-view.fxml", OrderViewController.class)
                    .loadOrder(order);
        }
    }
}
