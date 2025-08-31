package ua.edu.dnu.warehouse.types;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.springframework.stereotype.Component;
import ua.edu.dnu.warehouse.controls.SceneController;
import ua.edu.dnu.warehouse.model.GoodsType;
import ua.edu.dnu.warehouse.service.GoodsTypeService;
import ua.edu.dnu.warehouse.validator.Validator;

@Component
public class GoodsTypeCreateController {
    public TextField name;
    public TextArea description;
    private final GoodsTypeService typeService;
    private final SceneController paneController;

    public GoodsTypeCreateController(GoodsTypeService typeService, SceneController paneController){
        this.typeService = typeService;
        this.paneController = paneController;
    }

    private boolean isTypeValid(){
        return Validator.validateTextField(name, Validator.TITLE_REGEX);
    }

    public void addType() {
        if(isTypeValid()){
            GoodsType type = new GoodsType(name.getText(), description.getText());
            typeService.createGoodsType(type);
            paneController.setPrevContent();
        }
    }

    @FXML
    private void initialize() {
        Validator.addTextFieldValidation(name, Validator.TITLE_REGEX);
    }
}
