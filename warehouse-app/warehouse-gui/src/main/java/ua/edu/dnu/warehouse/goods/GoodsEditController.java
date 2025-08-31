package ua.edu.dnu.warehouse.goods;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import org.springframework.stereotype.Component;
import ua.edu.dnu.warehouse.controls.SceneController;
import ua.edu.dnu.warehouse.model.Goods;
import ua.edu.dnu.warehouse.model.GoodsStatus;
import ua.edu.dnu.warehouse.model.GoodsType;
import ua.edu.dnu.warehouse.service.GoodsService;
import ua.edu.dnu.warehouse.service.GoodsTypeService;
import ua.edu.dnu.warehouse.validator.Validator;

@Component
public class GoodsEditController {
    public TextField name;
    public TextArea description;
    public TextField amount;
    public TextField price;
    public ComboBox<GoodsType> typeList;
    public ComboBox<GoodsStatus> statusList;
    private Goods goods;
    private final GoodsService goodsService;
    private final GoodsTypeService typeService;
    private final SceneController paneController;

    public GoodsEditController(GoodsService goodsService, GoodsTypeService typeService,
                               SceneController paneController) {
        this.goodsService = goodsService;
        this.typeService = typeService;
        this.paneController = paneController;
    }

    public void loadGoods(Goods goods){
        this.goods = goods;
        reset();
    }

    public void reset(){
        name.setText(goods.getName());
        description.setText(goods.getDescription());
        amount.setText(String.valueOf(goods.getAmount()));
        price.setText(String.valueOf(goods.getPrice()));
        typeList.setValue(goods.getType());
        statusList.setValue(goods.getStatus());
    }

    private boolean isGoodsValid(){
        return Validator.validateTextField(name, Validator.TITLE_REGEX) &&
                Validator.validateTextField(amount, Validator.AMOUNT_REGEX) &&
                Validator.validateTextField(price, Validator.PRICE_REGEX) &&
                typeList.getValue() != null && statusList.getValue() != null;
    }

    @FXML
    private void saveGoods() {
        if (isGoodsValid()) {
            goods.setName(name.getText());
            goods.setDescription(description.getText());
            goods.setAmount(Integer.parseInt(amount.getText()));
            goods.setPrice(Integer.parseInt(price.getText()));
            goods.setStatus(statusList.getValue());
            goods.setType(typeList.getValue());
            goodsService.updateGoods(goods);
            paneController.setPrevContent();
        }
    }

    @FXML
    private void initialize() {
        Validator.addTextFieldValidation(name, Validator.TITLE_REGEX);
        Validator.addTextFieldValidation(amount, Validator.AMOUNT_REGEX);
        Validator.addTextFieldValidation(price, Validator.PRICE_REGEX);
        ObservableList<GoodsStatus> statusObservableList =
                FXCollections.observableArrayList(GoodsStatus.values());
        statusList.setItems(statusObservableList);
        ObservableList<GoodsType> typeObservableList =
                FXCollections.observableArrayList(typeService.getAllGoodsTypes());
        typeList.setItems(typeObservableList);
        typeList.setConverter(new StringConverter<>() {
            @Override
            public String toString(GoodsType type) {
                if(type == null){
                    return "";
                }
                return type.getName() + " (" + type.getDescription() + ")";
            }
            @Override
            public GoodsType fromString(String s) {
                return null;
            }
        });
    }
}
