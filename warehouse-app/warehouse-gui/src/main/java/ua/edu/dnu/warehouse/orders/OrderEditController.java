package ua.edu.dnu.warehouse.orders;

import com.jfoenix.controls.JFXRadioButton;
import javafx.scene.control.*;
import org.springframework.stereotype.Component;
import ua.edu.dnu.warehouse.controls.SceneController;
import ua.edu.dnu.warehouse.model.Order;
import ua.edu.dnu.warehouse.model.OrderItem;
import ua.edu.dnu.warehouse.model.OrderStatus;
import ua.edu.dnu.warehouse.report.ReportCreator;
import ua.edu.dnu.warehouse.report.Reportable;
import ua.edu.dnu.warehouse.service.OrderService;
import ua.edu.dnu.warehouse.util.AlertUtil;

import java.io.File;

@Component
public class OrderEditController implements Reportable {
    public TableView<OrderItem> table;
    public TableColumn<OrderItem, String> nameColumn;
    public TableColumn<OrderItem, String> descriptionColumn;
    public TableColumn<OrderItem, String> typeColumn;
    public TableColumn<OrderItem, String> amountColumn;
    public TableColumn<OrderItem, String> priceColumn;
    public ToggleGroup statusGroup;
    public JFXRadioButton createdToggle;
    public JFXRadioButton processedToggle;
    public JFXRadioButton readyToggle;
    public Button completeButton;
    public Button returnButton;
    public TextField phone;
    public TextField name;
    public TextField address;
    public TextField total;
    public TextField number;
    public TextField date;
    public TextField status;
    private Order order;
    private final OrderService orderService;
    private final SceneController sceneController;

    public OrderEditController(OrderService orderService, SceneController sceneController){
        this.orderService = orderService;
        this.sceneController = sceneController;
        statusGroup = new ToggleGroup();
        completeButton = new Button();
        returnButton = new Button();
    }

    public void loadOrder(Order order){
        this.order = order;
        order.setItems(orderService.getOrderItems(order));
        OrderUtil.setOrderData(order, table, phone, name, address, total, number, date, status);
        OrderStatus orderStatus = order.getStatus();
        statusGroup.selectToggle(getStatusToggle(orderStatus));
        if(orderStatus == OrderStatus.READY){
            completeButton.setDisable(false);
        }
        if(!orderStatus.isCanceled()){
            returnButton.setDisable(false);
        }
    }

    public void initialize() {
        OrderUtil.setOrderItemColumns(nameColumn, descriptionColumn, priceColumn, amountColumn, typeColumn);
    }

    private Toggle getStatusToggle(OrderStatus status){
        return switch (status) {
            case CREATED -> createdToggle;
            case PROCESSED -> processedToggle;
            case READY -> readyToggle;
            default -> null;
        };
    }

    private OrderStatus getSelectedStatus(){
        JFXRadioButton button = (JFXRadioButton) statusGroup.getSelectedToggle();
        if(button == null) {
            return null;
        }
        return switch (button.getId()){
            case "createdToggle" -> OrderStatus.CREATED;
            case "processedToggle" -> OrderStatus.PROCESSED;
            case "readyToggle" -> OrderStatus.READY;
            default -> null;
        };
    }

    public void onEditOrder() {
        OrderStatus status = getSelectedStatus();
        if(status == null){
            return;
        }
        if(status == order.getStatus()){
            sceneController.setPrevContent();
        }else {
            changeOrderStatus(status);
        }
    }

    private void changeOrderStatus(OrderStatus status){
        AlertUtil.alertWarning(
                "Попередження про оновлення замовлення",
                "Після виконання операції статус замовлення буде змінено на: " + status.getLabel()
                ).ifPresent(b -> {
                    orderService.changeOrderStatus(order.getId(), status);
                    sceneController.setPrevContent();
                });
    }

    public void onCompleteOrder() {
        changeOrderStatus(OrderStatus.COMPLETED);
    }

    public void onReturnOrder() {
        changeOrderStatus(OrderStatus.RETURNED);
    }

    @Override
    public void createReport(File directory) throws Exception {
        ReportCreator report = OrderUtil.createOrderReport(directory, order, table);
        report.saveAndShow();
    }
}
