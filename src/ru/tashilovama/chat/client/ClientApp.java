package ru.tashilovama.chat.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import ru.tashilovama.chat.client.content.Client;
import ru.tashilovama.chat.client.controllers.ControllerMainStage;

import java.awt.*;

public class ClientApp extends Application {
    private Client client;
    private ControllerMainStage mainController;
    private Callback controller;
    private static ClientApp instance;

    @Override
    public void init() throws Exception {
        super.init();
        client = new Client();
        instance = this;
    }

    public interface Callback {
        void callMeBack(Stage stage);
    }

    public void registerCallback(Callback controller) {
        this.controller = controller;
    }

    public static ClientApp getInstance() {
        return instance;
    }

    public Client getClient() {
        return client;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/tashilovama/chat/client/view/mainStage.fxml"));
        Parent root = loader.load();
        mainController = loader.getController();
        primaryStage.setTitle("JavaChat");
        primaryStage.getIcons().add(new Image("/ru/tashilovama/chat/client/view/resource/icon.png"));
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension dimension = kit.getScreenSize();
        primaryStage.setScene(new Scene(root, dimension.getWidth() / 2, dimension.getHeight() / 2));
        primaryStage.setMinWidth(350);
        primaryStage.setMinHeight(200);
        primaryStage.show();
        controller.callMeBack(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
