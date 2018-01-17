package ru.tashilovama.chat.client.content;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.tashilovama.chat.client.controllers.ControllerMainStage;

import java.awt.*;

public class Main extends Application {
    private Client client;
    private ControllerMainStage mainController;
    private Callback controller;
    private static Main instance;

    @Override
    public void init() throws Exception {
        super.init();
        client=new Client();
        instance=this;
    }

    public interface Callback{
        void callMeBack(Stage stage);
    }

    public void registerCallback(Callback controller){
        this.controller=controller;
    }

    public static Main getInstance(){
        return instance;
    }

    public Client getClient() {
        return client;
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader=new FXMLLoader(getClass().getResource("/ru/tashilovama/chat/client/fxml/mainStage.fxml"));
        Parent root=loader.load();
        mainController=loader.getController();
        primaryStage.setTitle("JavaChat");
        Toolkit kit=Toolkit.getDefaultToolkit();
        Dimension dimension=kit.getScreenSize();
        primaryStage.setScene(new Scene(root, dimension.getWidth()/2, dimension.getHeight()/2));
        primaryStage.setMinWidth(350);
        primaryStage.setMinHeight(200);
        primaryStage.show();
        controller.callMeBack(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
