package ua.edu.dnu.warehouse.controls;


import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import ua.edu.dnu.warehouse.util.AlertUtil;
import ua.edu.dnu.warehouse.util.SceneUtil;

@Service
public class SceneController {
    private final ApplicationContext context;
    private BorderPane pane;
    private PaneNode top;
    private PaneNode menu;
    private PaneContent content;

    static class PaneContent {
        PaneContent prev;
        PaneNode node;
        public PaneContent(PaneNode node) {
            this.node = node;
        }
    }

    private SceneController(ApplicationContext applicationContext){
        this.context = applicationContext;
    }

    public void setPane(BorderPane pane){
        this.pane = pane;
    }

    public void setMenu(String fxmlFileName){
        menu = loadNode(fxmlFileName);
        pane.setLeft(menu.root());
    }

    public void setTop(String fxmlFileName){
        top = loadNode(fxmlFileName);
        pane.setTop(top.root());
    }

    public <C> C setNextContent(String fxmlFileName, Class<C> cClass){
        setNextContent(fxmlFileName);
        return getController(cClass);
    }

    public void setNextContent(String fxmlFileName){
        PaneContent newContent = new PaneContent(loadNode(fxmlFileName));
        newContent.prev = content;
        content = newContent;
        pane.setCenter(content.node.root());
    }

    public void setRootContent(String fxmlFileName){
        content = new PaneContent(loadNode(fxmlFileName));
        pane.setCenter(content.node.root());
    }

    public void reset(){
        content = null;
        menu = null;
        top = null;
        pane = null;
    }

    public void setRootContent(){
        if(content == null){
            return;
        }
        PaneContent current = content;
        while (current.prev != null){
            current = current.prev;
        }
        content = current;
        update();
    }

    public void setPrevContent(){
        if(content == null || content.prev == null){
            return;
        }
        content = content.prev;
        if(content.prev == null){
            updateContent(content);
        }
        pane.setCenter(content.node.root());
    }

    public void update(){
        updateContent(content);
        pane.setCenter(content.node.root());
    }

    private void updateContent(PaneContent content){
        String fxmlFileName = content.node.fileName();
        content.node = loadNode(fxmlFileName);
    }

    private PaneNode loadNode(String fxml){
        FXMLLoader fxmlLoader = SceneUtil.getFXMLLoader(fxml);
        fxmlLoader.setControllerFactory(context::getBean);
        PaneNode node = null;
        try {
            Parent root = fxmlLoader.load();
            Object controller = fxmlLoader.getController();
            root.setFocusTraversable(true);
            node = new PaneNode(fxml, root, controller);
        }catch (Exception ex){
            ex.printStackTrace();
            AlertUtil.alertError("Не вдалося завантажити контент: " + fxml);
        }
        return node;
    }

    public <C> C getController(Class<C> cClass){
        return context.getBean(cClass);
    }

    public PaneNode getContentNode(){
        return content.node;
    }

    public PaneNode getTopNode(){
        return top;
    }

    public PaneNode getMenuNode(){
        return menu;
    }

}
