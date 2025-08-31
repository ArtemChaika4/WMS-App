package ua.edu.dnu.warehouse.analytics;

import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import org.springframework.stereotype.Component;
import ua.edu.dnu.warehouse.report.ReportCreator;
import ua.edu.dnu.warehouse.report.Reportable;
import ua.edu.dnu.warehouse.service.CustomerService;
import ua.edu.dnu.warehouse.service.EmployeeService;
import ua.edu.dnu.warehouse.service.GoodsService;
import ua.edu.dnu.warehouse.service.GoodsTypeService;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

@Component
public class AnalyticController implements Reportable {
    public Label averagePriceLabel;
    public Label goodsCountLabel;
    public Label typeCountLabel;
    public Label customerCountLabel;
    public Label employeeCountLabel;
    public PieChart typeChart;
    public PieChart statusChart;
    public PieChart positionChart;
    private final GoodsService goodsService;
    private final GoodsTypeService typeService;
    private final EmployeeService employeeService;
    private final CustomerService customerService;

    public AnalyticController(GoodsService goodsService, GoodsTypeService typeService,
                              EmployeeService employeeService, CustomerService customerService) {
        this.goodsService = goodsService;
        this.typeService = typeService;
        this.employeeService = employeeService;
        this.customerService = customerService;
    }

    public void initialize() {
        double averagePrice = goodsService.getGoodsAveragePrice();
        long goodsCount = goodsService.getGoodsCount();
        long typeCount = typeService.getGoodsTypeCount();
        long customerCount = customerService.getCustomersCount();
        long employeeCount = employeeService.getEmployeesCount();

        averagePriceLabel.setText(String.format("%.2f", averagePrice));
        goodsCountLabel.setText(String.valueOf(goodsCount));
        typeCountLabel.setText(String.valueOf(typeCount));
        customerCountLabel.setText(String.valueOf(customerCount));
        employeeCountLabel.setText(String.valueOf(employeeCount));

        goodsService.getGoodsCountGroupedByType().forEach((k, v) ->
                typeChart.getData().add(new PieChart.Data(k.getName() + " - " + v,  v)));
        goodsService.getGoodsCountByStatus().forEach((k, v) ->
                statusChart.getData().add(new PieChart.Data(k.getLabel() + " - " + v, v)));
        employeeService.getEmployeesCountByPosition().forEach((k, v) ->
                positionChart.getData().add(new PieChart.Data(k.getLabel() + " - " + v, v)));
        typeChart.setTitle("Діаграма розподілу товарів за типом");
        statusChart.setTitle("Діаграма розподілу товарів за статусом");
        positionChart.setTitle("Діаграма розподілу працівників за посадою");
    }

    @Override
    public void createReport(File directory) throws Exception {
        ReportCreator report = new ReportCreator(directory);
        report.create("розділ аналітики");
        report.addTitle("Управління підприємством");
        report.addText("Середня ціна товарів: ");
        report.addText(averagePriceLabel.getText() + " грн\n");
        report.addText("Загальна кількість товарів: ");
        report.addText(goodsCountLabel.getText() + "\n");
        report.addText("Загальна кількість видів товарів: ");
        report.addText(typeCountLabel.getText() + "\n");
        report.addText("Загальна кількість замовників: ");
        report.addText(customerCountLabel.getText() + "\n");
        report.addText("Загальна кількість працівників: ");
        report.addText(employeeCountLabel.getText() + "\n");
        report.addTitle("Аналітика розділу товарів");
        report.addChart(typeChart);
        report.addChart(statusChart);
        report.addTitle("Аналітика розділу працівників");
        report.addChart(positionChart);
        report.saveAndShow();
    }
}
