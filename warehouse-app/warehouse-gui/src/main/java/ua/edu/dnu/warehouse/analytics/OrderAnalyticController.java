package ua.edu.dnu.warehouse.analytics;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.stereotype.Component;
import ua.edu.dnu.warehouse.field.OrderItemField;
import ua.edu.dnu.warehouse.model.OrderItem;
import ua.edu.dnu.warehouse.report.ReportCreator;
import ua.edu.dnu.warehouse.report.Reportable;
import ua.edu.dnu.warehouse.service.OrderService;
import ua.edu.dnu.warehouse.util.FormatUtil;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class OrderAnalyticController implements Reportable {
    private static final int TOP_GOODS_LIMIT = 100;
    public PieChart pieChart;
    public BarChart<String, Number> chart;
    public Label totalLabel;
    public Label averagePriceLabel;
    public Label completedCountLabel;
    public Label ordersCountLabel;
    public TableView<OrderItem> table;
    public TableColumn<OrderItem, String> nameColumn;
    public TableColumn<OrderItem, String> descriptionColumn;
    public TableColumn<OrderItem, String> typeColumn;
    public TableColumn<OrderItem, String> priceColumn;
    public TableColumn<OrderItem, Integer> numberColumn;
    private final OrderService orderService;
    public DatePicker startDate;
    public DatePicker endDate;


    public OrderAnalyticController(OrderService orderService) {
        this.orderService = orderService;
    }

    public void initialize() {
        nameColumn.setCellValueFactory(item -> new SimpleStringProperty(
                item.getValue().getGoods().getName()));
        descriptionColumn.setCellValueFactory(item -> new SimpleStringProperty(
                item.getValue().getGoods().getDescription()));
        typeColumn.setCellValueFactory(item -> new SimpleStringProperty(
                item.getValue().getGoods().getType() == null ? "—" :
                        item.getValue().getGoods().getType().getName()));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>(OrderItemField.PRICE.getName()));
        numberColumn.setCellValueFactory(new PropertyValueFactory<>(OrderItemField.NUMBER.getName()));
        List<OrderItem> topGoods = orderService.getTopSellingGoods(TOP_GOODS_LIMIT);
        table.getItems().addAll(topGoods);
        orderService.getOrdersCountByStatus().forEach((k, v) ->
                pieChart.getData().add(new PieChart.Data(k.getLabel() + " - " + v, v)));
        pieChart.setTitle("Діаграма розподілу замовлень за статусом");
        long totalProfit = orderService.getTotalProfit();
        double averagePrice = orderService.getAverageOrderPrice();
        long completedCount = orderService.getCompletedOrdersCount();
        long ordersCount = orderService.getOrdersCount();
        totalLabel.setText(String.valueOf(totalProfit));
        averagePriceLabel.setText(String.format("%.2f", averagePrice));
        completedCountLabel.setText(String.valueOf(completedCount));
        ordersCountLabel.setText(String.valueOf(ordersCount));
        startDate.setValue(LocalDate.now().minusMonths(1));
        endDate.setValue(LocalDate.now());
        updateChart();
    }

    @FXML
    private void updateChart(){
        chart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Кількість виконаних замовлень за період: " +
                startDate.getValue() + " - " + endDate.getValue());
        orderService.getCompletedOrdersByDay(
                startDate.getValue().atStartOfDay(),
                endDate.getValue().atStartOfDay()
        ).forEach((k, v) -> series.getData().add(new XYChart.Data<>(FormatUtil.formatDayMonth(k), v)));
        chart.getData().add(series);
    }

    @Override
    public void createReport(File directory) throws Exception {
        ReportCreator report = new ReportCreator(directory);
        report.create("розділ реалізації");
        report.addTitle("Статистична інформація за замовленнями");
        report.addText("");
        report.addText("Загальний прибуток від продажу товарів: ");
        report.addText(totalLabel.getText() + " грн\n");
        report.addText("Середня ціна замовлень: ");
        report.addText(averagePriceLabel.getText() + "грн\n");
        report.addText("Кількість виконаних замовлень: ");
        report.addText(completedCountLabel.getText() + "\n");
        report.addText("Загальна кількість замовлень: ");
        report.addText(ordersCountLabel.getText() + "\n");
        report.addChart(pieChart);
        report.addTitle("Топ товарів за продажами\n");
        report.addText("");
        report.addTable(table);
        report.addTitle("Аналітика за реалізацією товарів");
        report.addText("Кількість виконаних замовлень за період: " +
                startDate.getValue() + " - " + endDate.getValue());
        report.addChart(chart);
        report.saveAndShow();
    }
}
