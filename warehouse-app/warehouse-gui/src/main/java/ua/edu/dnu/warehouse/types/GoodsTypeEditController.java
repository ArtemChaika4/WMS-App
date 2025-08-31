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
public class GoodsTypeEditController {
    public TextField name;
    public TextArea description;
    private GoodsType type;
    private final GoodsTypeService typeService;
    private final SceneController paneController;

    public GoodsTypeEditController(GoodsTypeService typeService, SceneController paneController){
        this.typeService = typeService;
        this.paneController = paneController;
    }

    public void loadType(GoodsType type){
        this.type = type;
        reset();
    }

    public void reset() {
        name.setText(type.getName());
        description.setText(type.getDescription());
    }

    private boolean isTypeValid(){
        return Validator.validateTextField(name, Validator.TITLE_REGEX);
    }

    public void saveType() {
        if(isTypeValid()){
            type.setName(name.getText());
            type.setDescription(description.getText());
            typeService.updateGoodsType(type);
            paneController.setPrevContent();
        }
    }

    @FXML
    private void initialize() {
        Validator.addTextFieldValidation(name, Validator.TITLE_REGEX);
    }
}
