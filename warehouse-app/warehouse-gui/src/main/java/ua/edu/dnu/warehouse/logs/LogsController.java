package ua.edu.dnu.warehouse.logs;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.stereotype.Component;
import ua.edu.dnu.warehouse.field.EventLogField;
import ua.edu.dnu.warehouse.filter.EventLogSpecification;
import ua.edu.dnu.warehouse.loader.TableDataLoader;
import ua.edu.dnu.warehouse.model.Action;
import ua.edu.dnu.warehouse.model.Category;
import ua.edu.dnu.warehouse.model.EventLog;
import ua.edu.dnu.warehouse.model.Post;
import ua.edu.dnu.warehouse.report.ReportCreator;
import ua.edu.dnu.warehouse.report.Reportable;
import ua.edu.dnu.warehouse.service.EventLogService;
import ua.edu.dnu.warehouse.util.FormatUtil;

import java.io.File;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class LogsController implements Reportable {
    public TextField searchField;
    public TableView<EventLog> table;
    public TableColumn<EventLog, String> actionColumn;
    public TableColumn<EventLog, String> timestampColumn;
    public TableColumn<EventLog, String> emailColumn;
    public TableColumn<EventLog, String> categoryColumn;
    public TableColumn<EventLog, String> positionColumn;
    public TableColumn<EventLog, String> resultColumn;
    public DatePicker afterDate;
    public DatePicker beforeDate;
    public ComboBox<Category> categoryList;
    public ComboBox<Action> actionList;
    public ComboBox<Post> postList;
    private final EventLogService logService;
    private EventLogSpecification specifications;
    private TableDataLoader<EventLog> tableDataLoader;

    public LogsController(EventLogService logService) {
        this.logService = logService;
    }

    public void initialize() {
        specifications = new EventLogSpecification();
        tableDataLoader = new TableDataLoader<>(logService, table, specifications);
        actionColumn.setCellValueFactory(new PropertyValueFactory<>(EventLogField.ACTION.getName()));
        resultColumn.setCellValueFactory(new PropertyValueFactory<>(EventLogField.RESULT.getName()));
        categoryColumn.setCellValueFactory(log -> new SimpleStringProperty(
                log.getValue().getCategory() == null ? "—" :
                        log.getValue().getCategory().getLabel()));
        timestampColumn.setCellValueFactory(log -> new SimpleStringProperty(
                FormatUtil.formatDateTime(log.getValue().getTimestamp())));
        emailColumn.setCellValueFactory(log -> new SimpleStringProperty(
                log.getValue().getEmployee() == null ? "—" :
                        log.getValue().getEmployee().getEmail()));
        positionColumn.setCellValueFactory(log -> new SimpleStringProperty(
                log.getValue().getEmployee() == null ||
                        log.getValue().getEmployee().getPost() == null ? "—" :
                        log.getValue().getEmployee().getPost().getLabel()));
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            specifications.containsValue(newValue);
            tableDataLoader.update();
        });
        afterDate.valueProperty().addListener((observable, oldValue, newValue) -> {
            LocalDateTime timestamp = newValue == null ? null : newValue.atStartOfDay();
            specifications.withAfterDate(timestamp);
            tableDataLoader.update();
        });
        beforeDate.valueProperty().addListener((observable, oldValue, newValue) -> {
            LocalDateTime timestamp = newValue == null ? null : newValue.atStartOfDay();
            specifications.withBeforeDate(timestamp);
            tableDataLoader.update();
        });
        ObservableList<Category> categories = FXCollections.observableArrayList(Category.values());
        categoryList.setItems(categories);
        ObservableList<Action> actions = FXCollections.observableArrayList(Action.values());
        actionList.setItems(actions);
        ObservableList<Post> posts = FXCollections.observableArrayList(Post.values());
        postList.setItems(posts);
        afterDate.setValue(LocalDate.now());
    }

    public void reset() {
        afterDate.setValue(null);
        beforeDate.setValue(null);
        searchField.setText(null);
        categoryList.setValue(null);
        actionList.setValue(null);
        postList.setValue(null);
        tableDataLoader.update();
    }

    public void setTable() {
        specifications.withCategory(categoryList.getValue());
        tableDataLoader.update();
    }

    public void setAction() {
        specifications.withAction(actionList.getValue());
        tableDataLoader.update();
    }

    public void setPost() {
        specifications.withPost(postList.getValue());
        tableDataLoader.update();
    }

    @Override
    public void createReport(File directory) throws Exception {
        ReportCreator report = new ReportCreator(directory);
        report.create("журнал подій");
        report.addTitle("Журнал подій");
        report.addText("Застосовані параметри фільтрації:\n");
        report.addText("Пошук за ключовим словом: ");
        String searchText = searchField.getText().isEmpty() ? "—" :
                "'" + searchField.getText() + "'";
        report.addText(searchText + "\n");
        report.addText("Фільтрація за датою:\n");
        report.addText("   від: ");
        report.addText(getDateValue(afterDate.getValue()) + "\n");
        report.addText("   до: ");
        report.addText(getDateValue(beforeDate.getValue()) + "\n");
        report.addText("Категорія: ");
        String section = categoryList.getValue() == null ? "Усі" :
                categoryList.getValue().getLabel();
        report.addText(section + "\n");
        report.addTable(table);
        report.saveAndShow();
    }

    private String getDateValue(LocalDate date){
        return date == null ? "—" : date.toString();
    }
}
