package ua.edu.dnu.warehouse.chatai;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;
import ua.edu.dnu.warehouse.ai.response.AiResponse;
import ua.edu.dnu.warehouse.ai.response.ResponseData;
import ua.edu.dnu.warehouse.ai.chat.ChatService;
import ua.edu.dnu.warehouse.report.ReportCreator;
import ua.edu.dnu.warehouse.report.Reportable;


import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class ChatAIController implements Reportable {
    public VBox messageContainer;
    public TextField messageInput;
    private final ChatService chatService;

    public ChatAIController(ChatService chatService) {
        this.chatService = chatService;

    }

    public void initialize() {
        // runRequestsInBackground();
    }

    public List<String> test() {
        List<String> prompts = List.of(
                // Загальні запити до товарів (GOODS)
                "Покажи всі товари, що є в наявності.",
                "Знайди товари, ціна яких більше 10000 гривень.",
                "Виведи всі товари з типом \"Книги\".",
                "Покажи товари, де назва містить слово \"телефон\".",
                "Які товари очікуються на складі?",
                "Скільки товарів типу \"Одяг\" зараз доступні?",
                "Яка середня ціна товарів у категорії \"Смартфони\"?",
                "Виведи п’ять найдорожчих товарів.",
                "Знайди товари, кількість яких менше 5 одиниць.",
                "Покажи всі видалені товари.",

                // 📦 Аналіз запасів (GOODS + AGGREGATION)
                "Скільки товарів кожного типу знаходиться в наявності?",
                "Підрахуй загальну кількість усіх товарів.",
                "Скільки усього товарів очікується на складі?",
                "Виведи середню ціну товарів по типах.",
                "Який тип товару має найбільшу кількість одиниць?",
                "Покажи типи товарів із вартістю товарів понад 15000 грн.",
                "Виведи мінімальну та максимальну ціну товарів.",
                "Скільки усього різних типів товарів на складі?",
                "Які типи товарів замовляють найчастіше?",
                "Зроби рейтинг товарів за кількістю виконаних замовлень.",

                // 🛒 Запити по замовленнях (ORDER)
                "Покажи всі замовлення, створені після 24 травня 2025 року.",
                "Знайди всі скасовані замовлення.",
                "Скільки замовлень виконано за останній місяць?",
                "Яка середня ціна замовлень у квітні 2025?",
                "Покажи замовлення клієнта з номером телефону +380999999999.",
                "Виведи замовлення з сумою понад 20000 грн.",
                "Які замовлення перебувають у статусі \"Готове до видачі\"?",
                "Скільки замовлень оброблено оператором ivanov@whs.ua?",
                "Покажи всі замовлення від клієнтів з прізвищем \"Шевченко\".",
                "Знайди замовлення, в яких більше 2 позицій товарів.",

                // 🧾 Агрегати по замовленнях
                "Який клієнт зробив найбільше замовлень?",
                "Скільки замовлень було створено кожного дня в травні?",
                "Виведи сумарну вартість замовлень по статусах.",
                "Яка найбільша ціна замовлення в системі?",
                "Підрахуй кількість замовлень для кожного клієнта та побудуй діаграму за отриманими даними.",
                "В який день тижня найчастіше створюють замовлення?",
                "Покажи 10 найактивніших клієнтів за кількістю замовлень.",
                "Виведи середню кількість замовлень на клієнта.",
                "Скільки замовлень було повернуто в останній місяць?",
                "Яка кількість замовлень у статусі \"Опрацьовано\"?",

                // 👤 Клієнти (CUSTOMER)
                "Покажи всіх клієнтів із міста Київ.",
                "Знайди клієнтів, які робили замовлення після 2025-05-01.",
                "Виведи клієнтів, у яких кількість замовлень більше 3.",
                "Хто з клієнтів витратив найбільше грошей?",
                "Які клієнти не робили жодного замовлення?",
                "Покажи клієнтів з номером телефону, що починається на \"+38011\".",
                "Скільки нових клієнтів з’явилося у травні?",
                "Виведи адреси всіх клієнтів із міста Дніпро.",
                "Відсортуй клієнтів за ПІБ.",
                "Зроби таблицю клієнтів із загальною сумою їх замовлень.",

                // 🧍‍♂️ Працівники (EMPLOYEE)
                "Покажи всіх працівників з посадою \"Оператор\".",
                "Скільки працівників з прізвищем \"Іванов\"?",
                "Які працівники мають посаду \"Адміністратор\"?",
                "Знайди працівника за електронною адресою olezko@whs.ua",
                "Скільки разів працівник Олежко входив в систему за останній тиждень?",
                "Який працівник виконав найбільше дій в системі?",
                "Хто з операторів створив найбільше замовлень?.",
                "Коли olezko@whs.ua востаннє входив у систему?",
                "Покажи всіх працівників, які видаляли записи на цьому тижні.",
                "Скільки різних працівників оновлювали замовлення за останні 3 дні?",

                // 📝 Журнал подій (EVENTLOG)
                "Покажи всі записи дій зі створенням товарів.",
                "Скільки разів оновлювали товари у травні?",
                "Хто видалив товари 2025-05-15?",
                "Яка найчастіша дія в системі?",
                "Виведи всі події категорії \"Замовники\".",
                "Які дії були виконані адміністратором?",
                "Скільки разів вхід у систему був здійснений за сьогдні?",
                "Покажи всі дії за останні 24 години.",
                "Хто найчастіше оновлює записи?",
                "Скільки входів в систему виконано працівниками за кожний день цього місяця?",

                // 🔄 Журнал замовлень (ORDERLOG)
                "Покажи всі оновлення замовлення з номером 24.",
                "Скільки разів змінювали статус замовлення?",
                "Хто змінював замовлення клієнта з номером телефону +380999999999?",
                "Покажи всі записи зі змінами повернутих замовлень.",
                "Коли було опрацьовано замовлення з номером 30?",
                "Виведи останні дії над замовленнями за сьогодні.",
                "Скільки різних працівників працювало над замовленням №21?",
                "Які замовлення були виконані працівником складу з прізвищем Олежко?",
                "Відсортуй логи змін статусів замовлень за датою.",
                "Покажи логи скасованих за останній місяць замовлень.",

                // 📊 Складна аналітика з групуванням і фільтрами
                "Які типи товарів приносять найбільший дохід?",
                "Скільки замовлень виконано щомісяця?",
                "Скільки замовлень надійшло від кожного клієнта у травні?",
                "Яка середня вартість замовлень по днях?",
                "Покажи ТОП-5 клієнтів за загальною сумою покупок.",
                "Виведи товари, в яких кількість < 10 і ціна > 5000.",
                "Які працівники оновлювали замовлення більше 10 разів?",
                "Скільки логів створення для кожної категорії?",
                "Яка кількість нових замовлень щодня протягом тижня?",
                "Виведи середню суму замовлень по клієнтах, у яких більше 2 виконаних замовлень.",

                // 🧪 Тестові, змішані й специфічні запити
                "Покажи всі замовлення, в яких товари були видалені.",
                "Скільки логів було з помилками? (припущення про поле result)",
                "Виведи замовлення клієнтів, які мешкають у Львові.",
                "Знайди товари, які з’явились після 2025-05-01.",
                "Скільки клієнтів замовили товар типу \"Смартфони\"?",
                "Який працівник відповідає за найменше дій?",
                "Сортуй замовлення за спаданням суми.",
                "Які товари мають однакову назву, але різні описи?",
                "Скільки замовлень було у кожному статусі за травень?",
                "Виведи всі товари, які мають у назві слово \"ноутбук\" і коштують понад 15000 грн."
        );
        return prompts;
    }


    private void runRequestsInBackground() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                List<String> prompts = test();
                for (int i = 0; i < prompts.size(); i++) {
                    String prompt = prompts.get(i);
                    try {
                        System.out.println();
                        System.out.println("Запит №" + (i + 1));
                        AiResponse response = chatService.ask(prompt);
                        sendRequest(prompt, response);
                    } catch (Throwable ex) {
                        System.out.println(ex.getMessage());
                    }
                    if (i < prompts.size() - 1) {
                        try {
                            Thread.sleep(45_000); // 45 сек
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    private void sendRequest(String message, AiResponse response){
        javafx.application.Platform.runLater(() -> {
            addMessage(message, Pos.BASELINE_RIGHT, "lightblue");
            addMessage(response.message(), Pos.BASELINE_LEFT, "lightgray");
            switch (response.type()){
                case TABLE -> addTable(response.data());
                case CHART -> addChart(response.data());
            }
            messageInput.clear();
        });
    }

    @FXML
    private void sendRequest() {
        String message = messageInput.getText().trim();
        if (!message.isEmpty()) {
            addMessage(message, Pos.BASELINE_RIGHT, "lightblue");
            getResponse(message);
            messageInput.clear();
        }
    }

    private void getResponse(String message) {
        AiResponse response = chatService.ask(message);
        addMessage(response.message(), Pos.BASELINE_LEFT, "lightgray");
        switch (response.type()){
            case TABLE -> addTable(response.data());
            case CHART -> addChart(response.data());
        }
    }

    private void addMessage(String text, Pos alignment, String color) {
        HBox messageBox = new HBox();
        messageBox.setAlignment(alignment);
        Label messageLabel = new Label(text);
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(400);
        messageLabel.setStyle("-fx-background-color: " + color + "; -fx-padding: 10; -fx-background-radius: 10;");
        messageBox.getChildren().add(messageLabel);
        messageContainer.getChildren().add(messageBox);
    }

    private void addChart(ResponseData data){
        String category = data.columns().get(0);
        String value = data.columns().get(1);
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel(category);
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(value);
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        data.rows().forEach(
                row -> series.getData().add(new XYChart.Data<>(row.get(category).toString(), Double.valueOf(row.get(value).toString())))
        );
        barChart.getData().add(series);
        messageContainer.getChildren().add(barChart);
    }

    private void addTable(ResponseData data){
        if(data == null || data.columns().isEmpty()){
            return;
        }
        TableView<Map<String, Object>> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        for (String name : data.columns()) {
            TableColumn<Map<String, Object>, String> column = new TableColumn<>(name);
            column.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().get(name))));
            table.getColumns().add(column);
        }
        table.setItems(FXCollections.observableArrayList(data.rows()));
        messageContainer.getChildren().add(table);
    }

    @Override
    public void createReport(File directory) throws Exception {
        ReportCreator report = new ReportCreator(directory);
        report.create("ШІ-асистент");
        report.addTitle("Чат з ШІ-асистентом");
        for (Node message : messageContainer.getChildren()) {
            if(message instanceof HBox hBox){
                Optional<Label> label = hBox.getChildren().stream()
                        .map(node -> (Label) node)
                        .findFirst();
                if(label.isPresent()){
                    String text = label.get().getText();
                    report.addText(text + "\n");
                }
            }
            if(message instanceof TableView<?> table){
                report.addTable(table);
            }
            if(message instanceof Chart chart){
                report.addChart(chart);
            }
        }
        report.saveAndShow();
    }
}
