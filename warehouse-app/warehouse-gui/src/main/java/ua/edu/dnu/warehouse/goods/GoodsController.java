package ua.edu.dnu.warehouse.goods;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import org.springframework.stereotype.Component;
import ua.edu.dnu.warehouse.SpringContext;
import ua.edu.dnu.warehouse.controls.SceneController;
import ua.edu.dnu.warehouse.field.GoodsField;
import ua.edu.dnu.warehouse.filter.GoodsSpecification;
import ua.edu.dnu.warehouse.loader.TableDataLoader;
import ua.edu.dnu.warehouse.model.Goods;
import ua.edu.dnu.warehouse.model.GoodsStatus;
import ua.edu.dnu.warehouse.model.GoodsType;
import ua.edu.dnu.warehouse.report.ReportCreator;
import ua.edu.dnu.warehouse.report.Reportable;
import ua.edu.dnu.warehouse.service.GoodsService;
import ua.edu.dnu.warehouse.service.GoodsTypeService;

import java.io.File;

@Component
public class GoodsController implements Reportable {
    public TableView<Goods> table;
    public TableColumn<Goods, String> nameColumn;
    public TableColumn<Goods, String> descriptionColumn;
    public TableColumn<Goods, String> typeColumn;
    public TableColumn<Goods, Integer> amountColumn;
    public TableColumn<Goods, Integer> priceColumn;
    public TextField searchField;
    public ComboBox<GoodsField> sortList;
    public ComboBox<GoodsType> typeList;
    public ComboBox<GoodsStatus> statusList;
    private GoodsService goodsService;
    private GoodsTypeService typeService;
    private GoodsSpecification specifications;
    private SceneController paneController;
    private TableDataLoader<Goods> tableDataLoader;


    public GoodsController(GoodsService goodsService, GoodsTypeService typeService,
                           SceneController paneController){
        this.goodsService = goodsService;
        this.typeService = typeService;
        this.paneController = paneController;
    }

    public void onCreate() {
       paneController.setNextContent("goods-create.fxml");
    }

    public void onDelete() {
        Goods goods = table.getSelectionModel().getSelectedItem();
        if(goods != null && goods.getStatus() != GoodsStatus.DELETED) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Попередження про видалення товару");
            alert.setContentText("Після виконання операції література зникне з переліку наявних товарів");
            alert.showAndWait().ifPresent(b -> {
                goods.setStatus(GoodsStatus.DELETED);
                goodsService.updateGoods(goods);
                table.getItems().remove(goods);
            });
        }
    }
    @FXML
    private void onEdit(){
        Goods goods = table.getSelectionModel().getSelectedItem();
        if (goods != null) {
            paneController.setNextContent("goods-edit.fxml");
            SpringContext.getBean(GoodsEditController.class).loadGoods(goods);
        }
    }

    public void setType() {
        specifications.withType(typeList.getValue());
        tableDataLoader.update();
    }

    public void setStatus(){
        specifications.withStatus(statusList.getValue());
        tableDataLoader.update();
    }

    public void sort(){
        specifications.sortedBy(sortList.getValue());
        tableDataLoader.update();
    }


    @FXML
    public void initialize() {
        specifications = new GoodsSpecification();
        tableDataLoader = new TableDataLoader<>(goodsService, table, specifications);
        ObservableList<GoodsField> sorts =
                FXCollections.observableArrayList(GoodsField.values());
        sortList.setItems(sorts);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>(GoodsField.NAME.getName()));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>(GoodsField.DESCRIPTION.getName()));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>(GoodsField.PRICE.getName()));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>(GoodsField.AMOUNT.getName()));
        typeColumn.setCellValueFactory(goods -> new SimpleStringProperty(
                goods.getValue().getType() == null ? "—" :
                        goods.getValue().getType().getName() + " (" + goods.getValue().getType().getDescription() + ")"));
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            specifications.containsValue(newValue);
            tableDataLoader.update();
        });

        ObservableList<GoodsType> types = FXCollections.observableArrayList(
                typeService.getAllGoodsTypes());
        typeList.setItems(types);
        typeList.setConverter(new StringConverter<>() {
            @Override
            public String toString(GoodsType type) {
                if(type == null){
                    return "";
                }
                return type.getName();
            }
            @Override
            public GoodsType fromString(String s) {
                return null;
            }
        });
        ObservableList<GoodsStatus> statuses = FXCollections.observableArrayList(GoodsStatus.values());
        statusList.setItems(statuses);
        tableDataLoader.update();
    }

    @Override
    public void createReport(File directory) throws Exception {
        ReportCreator report = new ReportCreator(directory);
        report.create("товари");
        report.addTitle("Товари");
        report.addText("Застосовані параметри фільтрації:\n");
        report.addText("Пошук за ключовим словом: ");
        String searchText = searchField.getText().isEmpty() ? "—" :
                "'" + searchField.getText() + "'";
        report.addText(searchText + "\n");
        report.addText("Параметри сортування: ");
        String sortBy = sortList.getValue() == null ? "—" :
                sortList.getValue().getLabel();
        report.addText(sortBy + "\n");
        report.addText("Вид товарів: ");
        String type = typeList.getValue() == null ? "Усі" :
                typeList.getValue().getName();
        report.addText(type + "\n");
        report.addText("Статус товару: ");
        String status = statusList.getValue() == null ? "Усі" :
                statusList.getValue().getLabel();
        report.addText(status + "\n");
        report.addTable(table);
        report.saveAndShow();
    }
}
