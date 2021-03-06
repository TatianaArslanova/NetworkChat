package ru.tashilovama.chat.server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.tashilovama.chat.server.content.MyServer;

public class ServerApp extends Application{
    private static ServerApp instance;
    private MyServer server;

    @Override
    public void init() throws Exception {
        instance=this;
        server=new MyServer();
    }

    public static ServerApp getInstance(){
        return instance;
    }

    public MyServer getServer() {
        return server;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root=FXMLLoader.load(getClass().getResource("/ru/tashilovama/chat/server/fxml/server.fxml"));
        primaryStage.setTitle("Server");
        primaryStage.setScene(new Scene(root,400,400));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
