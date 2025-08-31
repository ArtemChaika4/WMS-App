package ua.edu.dnu.warehouse.orders;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.stereotype.Component;
import ua.edu.dnu.warehouse.controls.SceneController;
import ua.edu.dnu.warehouse.field.GoodsField;
import ua.edu.dnu.warehouse.goods.GoodsController;
import ua.edu.dnu.warehouse.model.Goods;
import ua.edu.dnu.warehouse.model.GoodsStatus;
import ua.edu.dnu.warehouse.model.GoodsType;
import ua.edu.dnu.warehouse.model.OrderItem;
import ua.edu.dnu.warehouse.util.AlertUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OrderFormController {
    public TableView<Goods> table;
    public TableColumn<Goods, String> nameColumn;
    public TableColumn<Goods, String> descriptionColumn;
    public TableColumn<Goods, String> typeColumn;
    public TableColumn<Goods, Integer> amountColumn;
    public TableColumn<Goods, Integer> priceColumn;
    public TextField searchField;
    public ComboBox<GoodsField> sortList;
    public ComboBox<GoodsType> typeList;
    public TableView<OrderItem> goodsTable;
    public TableColumn<OrderItem, String> goodsNameColumn;
    public TableColumn<OrderItem, String> goodsDescriptionColumn;
    public TableColumn<OrderItem, String> goodsTypeColumn;
    public TableColumn<OrderItem, String> goodsAmountColumn;
    public TableColumn<OrderItem, String> goodsPriceColumn;
    public TextField goodsName;
    public TextField goodsDescription;
    public TextField goodsType;
    public TextField goodsPrice;
    public TextField goodsAmount;
    public Spinner<Integer> goodsNumber;
    public TextField goodsTotal;
    public TextField total;
    private final GoodsController goodsController;
    private Goods selectedGoods;
    private Map<Long, OrderItem> goodsItemMap;
    private final SceneController sceneController;

    public OrderFormController(GoodsController goodsController, SceneController sceneController) {
        this.goodsController = goodsController;
        this.sceneController = sceneController;
    }

    @FXML
    private void initialize() {
        goodsItemMap = new HashMap<>();
        goodsController.table = table;
        goodsController.nameColumn = nameColumn;
        goodsController.descriptionColumn = descriptionColumn;
        goodsController.typeColumn = typeColumn;
        goodsController.amountColumn = amountColumn;
        goodsController.priceColumn = priceColumn;
        goodsController.searchField = searchField;
        goodsController.typeList = typeList;
        goodsController.sortList = sortList;
        goodsController.statusList = new ComboBox<>();
        goodsController.initialize();
        goodsController.statusList.setValue(GoodsStatus.AVAILABLE);
        goodsController.setStatus();
        OrderUtil.setOrderItemColumns(goodsNameColumn, goodsDescriptionColumn, goodsPriceColumn, goodsAmountColumn, goodsTypeColumn);
        table.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldValue, newValue) -> setGoods(newValue));
        goodsTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldValue, newValue) -> setGoods(newValue));
        goodsNumber.valueProperty().addListener(
                (obs, oldValue, newValue) -> setGoodsTotal());
    }

    private void setGoodsTotal() {
        long total = goodsNumber.getValue() * selectedGoods.getPrice();
        goodsTotal.setText(String.valueOf(total));
    }

    private void setGoods(OrderItem item){
        if(item == null){
            return;
        }
        setGoods(item.getGoods());
        table.getSelectionModel().clearSelection();
    }

    private void setGoods(Goods goods){
        if(goods == null){
            return;
        }
        selectedGoods = goods;
        goodsName.setText(goods.getName());
        goodsDescription.setText(goods.getDescription());
        goodsType.setText(goods.getType() == null ? "—" : goods.getType().getName());
        goodsPrice.setText(String.valueOf(goods.getPrice()));

        OrderItem item = goodsItemMap.get(goods.getId());
        int number = item == null ? 1 : item.getNumber();
        goodsAmount.setText(String.valueOf(goods.getAmount()));
        goodsNumber.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, goods.getAmount()));
        goodsNumber.getValueFactory().setValue(number);
    }

    public void onRemove() {
        if(selectedGoods == null){
            return;
        }
        Long goodsId = selectedGoods.getId();
        if(goodsItemMap.containsKey(goodsId)) {
            goodsItemMap.remove(goodsId);
            update();
            clear();
        }
    }

    private void clear() {
        selectedGoods = null;
        goodsName.setText(null);
        goodsDescription.setText(null);
        goodsType.setText(null);
        goodsPrice.setText(null);
        goodsAmount.setText(null);
        goodsNumber.getEditor().clear();
        goodsNumber.setValueFactory(null);
        goodsTotal.setText(null);
    }

    public void onUpdate() {
        if(selectedGoods == null){
            return;
        }
        Long goodsId = selectedGoods.getId();
        if(goodsItemMap.containsKey(goodsId)){
            int number = goodsNumber.getValue();
            OrderItem item = goodsItemMap.get(goodsId);
            item.setNumber(number);
            update();
        }
    }

    public void onAdd() {
        if(selectedGoods == null){
            return;
        }
        Long goodsId = selectedGoods.getId();
        long price = selectedGoods.getPrice();
        int number = goodsNumber.getValue();

        OrderItem item = goodsItemMap.get(goodsId);
        if(item == null){
            item = new OrderItem(selectedGoods, number, price);
            goodsItemMap.put(goodsId, item);
        }else {
            int amount = selectedGoods.getAmount() - item.getNumber();
            int maxNumber = Math.min(number, amount);
            item.addNumber(maxNumber);
            goodsNumber.getValueFactory().setValue(item.getNumber());
        }
        update();
    }

    private void update(){
        goodsTable.getItems().clear();
        goodsTable.setItems(FXCollections.observableArrayList(
                goodsItemMap.values()));
        total.setText(String.valueOf(getTotal()));
    }

    private long getTotal(){
        return goodsItemMap.values().stream()
                .mapToLong(OrderItem::getTotalPrice)
                .sum();
    }

    public void onConfirm() {
        if(goodsItemMap.isEmpty()){
            AlertUtil.alertInfo("Список товарів порожній. Додайте товари до замовлення");
            return;
        }
        List<OrderItem> items = new ArrayList<>(goodsItemMap.values());
        sceneController.setNextContent("order-creation.fxml", OrderCreateController.class)
                .setItems(items);
    }

    public void setType() {
        goodsController.setType();
    }

    public void sort() {
        goodsController.sort();
    }
}
