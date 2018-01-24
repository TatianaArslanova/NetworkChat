package ru.tashilovama.chat.client.controllers;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ControllerSettings {
    public TextField hostField;
    public TextField portField;
    public Label tryAgainMessage;

    private final int MAX_PORT = 65535;
    private Callback connectionSettings;

    public interface Callback {
        void callMeBack(String host, int port);
    }

    public void registerCallback(Callback connectionSettings) {
        this.connectionSettings = connectionSettings;
    }

    public void setDefaultText(String host, int port) {
        hostField.setText(host);
        portField.setText(String.valueOf(port));
    }

    public void updateConnectionSettings() {
        try {
            int port = Integer.parseInt(portField.getText());
            String host = hostField.getText().trim();
            if (port < 1 || port > MAX_PORT) throw new RuntimeException("Invalid port");
            Platform.runLater(() -> {
                connectionSettings.callMeBack(host, port);
                Stage stage = (Stage) portField.getScene().getWindow();
                stage.close();
            });
        } catch (Exception e) {
            e.printStackTrace();
            Platform.runLater(() -> {
                tryAgainMessage.setManaged(true);
                tryAgainMessage.setVisible(true);
            });

        }
    }

    public void cancelClick() {
        Stage stage = (Stage) portField.getScene().getWindow();
        stage.close();
    }

    public void defaultClick() {
        hostField.setText("localhost");
        portField.setText("5555");
    }

}
