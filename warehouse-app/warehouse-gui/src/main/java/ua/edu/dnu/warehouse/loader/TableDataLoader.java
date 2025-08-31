package ua.edu.dnu.warehouse.loader;

import javafx.concurrent.Task;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableView;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import ua.edu.dnu.warehouse.service.SearchService;
import ua.edu.dnu.warehouse.filter.SpecificationBuilder;
import ua.edu.dnu.warehouse.util.AlertUtil;

import java.util.List;

public class TableDataLoader<T> {
    public static final int DEFAULT_PAGE_SIZE = 50;
    private final TableView<T> table;
    private final SearchService<T> searchService;
    private final SpecificationBuilder<T> specifications;
    private PageRequest pageRequest;
    private boolean isLoading;

    public TableDataLoader(SearchService<T> searchService, TableView<T> table,
                           SpecificationBuilder<T> specifications){
        this.searchService = searchService;
        this.table = table;
        this.specifications = specifications;
        setTable(table);
        pageRequest = PageRequest.ofSize(DEFAULT_PAGE_SIZE);
    }

    private void setTable(TableView<T> table){
        table.setOnScroll(event -> {
            ScrollBar scrollbar = getScrollBar(table);
            if (scrollbar.getValue() >= scrollbar.getMax() - 0.1) {
                loadMoreData();
            }
        });
    }

    public void update(){
        pageRequest = pageRequest.first();
        table.getItems().clear();
        loadMoreData();
    }

    private void loadMoreData() {
        if (isLoading) {
            return;
        }
        Specification<T> specifications = this.specifications.build();
        Pageable pageable = pageRequest;
        isLoading = true;
        Task<List<T>> task = new Task<>() {
            @Override
            protected List<T> call() {
                return searchService.search(specifications, pageable).getContent();
            }
        };
        task.setOnSucceeded(event -> {
            List<T> entities = task.getValue();
            table.getItems().addAll(entities);
            if (!entities.isEmpty()) {
                pageRequest = pageRequest.next();
            }
            isLoading = false;
        });
        task.setOnFailed(event -> {
            AlertUtil.alertError("Помилка при завантаженні даних");
            isLoading = false;
            throw new RuntimeException(event.getSource().getException());
        });
        new Thread(task).start();
    }

    private ScrollBar getScrollBar(TableView<T> table) {
        for (var node : table.lookupAll(".scroll-bar")) {
            if (node instanceof ScrollBar scrollbar &&
                    scrollbar.getOrientation() == javafx.geometry.Orientation.VERTICAL) {
                return scrollbar;
            }
        }
        return null;
    }
}
