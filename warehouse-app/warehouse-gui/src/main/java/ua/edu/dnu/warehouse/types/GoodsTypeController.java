package ua.edu.dnu.warehouse.types;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.stereotype.Component;
import ua.edu.dnu.warehouse.SpringContext;
import ua.edu.dnu.warehouse.controls.SceneController;
import ua.edu.dnu.warehouse.field.GoodsTypeField;
import ua.edu.dnu.warehouse.filter.GoodsTypeSpecification;
import ua.edu.dnu.warehouse.loader.TableDataLoader;
import ua.edu.dnu.warehouse.model.GoodsType;
import ua.edu.dnu.warehouse.report.ReportCreator;
import ua.edu.dnu.warehouse.report.Reportable;
import ua.edu.dnu.warehouse.service.GoodsTypeService;
import ua.edu.dnu.warehouse.util.AlertUtil;

import java.io.File;

@Component
public class GoodsTypeController implements Reportable {
    public TextField searchField;
    public TableView<GoodsType> table;
    public TableColumn<GoodsType, String> nameColumn;
    public TableColumn<GoodsType, String> descriptionColumn;
    public ComboBox<GoodsTypeField> sortList;
    private GoodsTypeService types;
    private GoodsTypeSpecification specifications;
    private SceneController paneController;
    private TableDataLoader<GoodsType> tableDataLoader;

    public GoodsTypeController(GoodsTypeService types, SceneController paneController){
        this.types = types;
        this.paneController = paneController;
    }

    public void onCreate() {
        paneController.setNextContent("type-create.fxml");
    }

    public void sort() {
        specifications.sortedBy(sortList.getValue());
        tableDataLoader.update();
    }

    public void onEdit() {
        GoodsType type = table.getSelectionModel().getSelectedItem();
        if(type != null) {
            paneController.setNextContent("type-edit.fxml");
            SpringContext.getBean(GoodsTypeEditController.class).loadType(type);
        }
    }

    public void onDelete() {
        GoodsType type = table.getSelectionModel().getSelectedItem();
        if(type != null){
            AlertUtil.alertWarning(
                    "Попередження про видалення типу літератури",
                    "Після виконання операції тип літератури буде видалено без можливості відновлення")
                    .ifPresent(b -> {
                        types.deleteGoodsType(type.getId());
                        table.getItems().remove(type);
                    });
        }
    }

    public void initialize() {
        specifications = new GoodsTypeSpecification();
        tableDataLoader = new TableDataLoader<>(types, table, specifications);
        ObservableList<GoodsTypeField> sorts = FXCollections.observableArrayList(GoodsTypeField.values());
        sortList.setItems(sorts);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>(GoodsTypeField.NAME.getName()));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>(GoodsTypeField.DESCRIPTION.getName()));
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            specifications.containsValue(newValue);
            tableDataLoader.update();
        });
        tableDataLoader.update();
    }

    @Override
    public void createReport(File directory) throws Exception {
        ReportCreator report = new ReportCreator(directory);
        report.create("види товарів");
        report.addTitle("Види товарів");
        report.addText("Застосовані параметри фільтрації:\n");
        report.addText("Пошук за ключовим словом: ");
        String searchText = searchField.getText().isEmpty() ? "—" :
                "'" + searchField.getText() + "'";
        report.addText(searchText + "\n");
        report.addText("Параметри сортування: ");
        String sortBy = sortList.getValue() == null ? "—" :
                sortList.getValue().getLabel();
        report.addText(sortBy + "\n");
        report.addTable(table);
        report.saveAndShow();
    }
}
