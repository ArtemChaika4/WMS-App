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
public class GoodsCreateController {
    public TextField name;
    public TextArea description;
    public TextField amount;
    public TextField price;
    public ComboBox<GoodsType> typeList;
    private final GoodsService goodsService;
    private final GoodsTypeService typeService;
    private final SceneController paneController;

    public GoodsCreateController(GoodsService goodsService, GoodsTypeService typeService,
                               SceneController paneController) {
        this.goodsService = goodsService;
        this.typeService = typeService;
        this.paneController = paneController;
    }

    private boolean isGoodsValid(){
        return Validator.validateTextField(name, Validator.TITLE_REGEX) &&
                Validator.validateTextField(amount, Validator.AMOUNT_REGEX) &&
                Validator.validateTextField(price, Validator.PRICE_REGEX) &&
                typeList.getValue() != null;
    }

    @FXML
    private void addGoods() {
        if (isGoodsValid()) {
            Goods goods = new Goods(name.getText(), description.getText(),
                    Integer.parseInt(amount.getText()), Integer.parseInt(price.getText()),
                    GoodsStatus.AVAILABLE, typeList.getValue());
            goodsService.createGoods(goods);
            paneController.setPrevContent();
        }
    }

    @FXML
    private void initialize() {
        Validator.addTextFieldValidation(name, Validator.TITLE_REGEX);
        Validator.addTextFieldValidation(amount, Validator.AMOUNT_REGEX);
        Validator.addTextFieldValidation(price, Validator.PRICE_REGEX);
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
