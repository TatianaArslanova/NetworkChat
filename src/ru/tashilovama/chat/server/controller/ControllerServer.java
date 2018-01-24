package ru.tashilovama.chat.server.controller;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.tashilovama.chat.server.ServerApp;
import ru.tashilovama.chat.server.content.MyServer;

import java.net.BindException;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerServer implements Initializable {
    public Label currentPort;
    public HBox startPane;
    public HBox stopPane;
    public TextArea log;
    private MyServer server;
    private int port;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        server= ServerApp.getInstance().getServer();
        port=5555;
        server.registerCallback(message -> Platform.runLater(() -> log.appendText(message+"\n")));
    }

    public void startClick(){
        try {
            server.startServer(port);
            Platform.runLater(() -> {
                startPane.setVisible(false);
                startPane.setManaged(false);
                stopPane.setManaged(true);
                stopPane.setVisible(true);
            });
        }catch (BindException e){
            e.printStackTrace();
            log.appendText("Указанный порт занят\n");
            server.stopServer();
        }
    }

    public void stopClick(){
        server.stopServer();
        Platform.runLater(() -> {
            stopPane.setManaged(false);
            stopPane.setVisible(false);
            startPane.setVisible(true);
            startPane.setManaged(true);
        });

    }

    public void changePort() throws Exception{
        Stage portChangeWindow=new Stage();
        FXMLLoader loader=new FXMLLoader(getClass().getResource("/ru/tashilovama/chat/server/fxml/changePort.fxml"));
        Parent portChange=loader.load();
        ControllerChangePort controller=loader.getController();
        controller.registerCallback(port -> {
            this.port=port;
            currentPort.setText("Текущий порт: "+port);
        });
        portChangeWindow.setScene(new Scene(portChange,300,160));
        portChangeWindow.setAlwaysOnTop(true);
        portChangeWindow.setTitle("Изменение порта");
        portChangeWindow.initModality(Modality.APPLICATION_MODAL);
        portChangeWindow.setResizable(false);
        portChangeWindow.show();
    }
}
