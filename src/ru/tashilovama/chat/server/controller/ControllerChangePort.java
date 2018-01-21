package ru.tashilovama.chat.server.controller;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ControllerChangePort {
    public TextField portField;
    public Label tryAgainMessage;

    private final int MAX_PORT=65535;
    private Callback setPort;

    public interface Callback{
        void callMeBack(int port);
    }

    public void registerCallback(Callback setPort){
        this.setPort=setPort;
    }

    public void updatePort(){
        try {
            int port = Integer.parseInt(portField.getText());
            if (port<1||port>MAX_PORT) throw new RuntimeException("Invalid port");
            Platform.runLater(() -> {
                setPort.callMeBack(port);
                Stage stage = (Stage) portField.getScene().getWindow();
                stage.close();
            });
        }catch (Exception e){
            e.printStackTrace();
            Platform.runLater(() -> {
                tryAgainMessage.setManaged(true);
                tryAgainMessage.setVisible(true);
            });

        }
    }

    public void cancelClick(){
        Stage stage=(Stage)portField.getScene().getWindow();
        stage.close();
    }

}
