package ru.tashilovama.chat.client.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import ru.tashilovama.chat.client.content.Main;
import ru.tashilovama.chat.client.content.Client;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerMainStage implements Initializable {

    public TextArea chatTextArea;
    public ListView<String> clientList;
    public TextField messageField;
    public TextField loginField;
    public PasswordField passField;
    public HBox messagePane;
    public HBox authPane;

    private Client client;
    private Stage mainStage;
    private String mainTitle;

    private ObservableList<String> users;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        users=FXCollections.observableArrayList();

        Main.getInstance().registerCallback(stage -> {
            mainStage = stage;
            mainTitle = stage.getTitle();
        });
        client = Main.getInstance().getClient();
        client.registerCallback(message ->
        {
            if (!message.startsWith("/") || !executeIfIsCommand(message)) {
                chatTextArea.appendText(message + "\n");
            }
        });
    }

    public void onSend() {
        client.writeMsg(messageField.getText());
        messageField.clear();
        messageField.requestFocus();
    }

    public void guestAuthClick() {
        client.startConnection();
        client.writeMsg("/guestauth");
    }

    public void userAuthClick() {
        client.startConnection();
        client.writeMsg("/auth " + loginField.getText() + " " + passField.getText());
        loginField.clear();
        passField.clear();
    }

    private void updateClientList(String message) {
        String[] clients=message.split(" ");
        Platform.runLater(() -> {
            users.clear();
            users.addAll(clients);
            clientList.setItems(users);
        });
    }

    private boolean executeIfIsCommand(String message) {
        final int PARTS_LIMIT = 2;
        String[] parts = message.split(" ", PARTS_LIMIT);
        String command = parts[0];
        String specifiadMessage;
        String myNick;
        switch (command) {
            case "/clientlist":
                specifiadMessage = parts[1];
                updateClientList(specifiadMessage);
                return true;
            case "/guestauth":
                myNick = parts[1];
                authPane.setVisible(false);
                authPane.setManaged(false);
                messagePane.setVisible(true);
                messagePane.setManaged(true);
                Platform.runLater(() -> mainStage.setTitle(mainTitle + " - " + myNick));
                return true;
            case "/auth":
                myNick = parts[1];
                authPane.setVisible(false);
                authPane.setManaged(false);
                messagePane.setVisible(true);
                messagePane.setManaged(true);
                Platform.runLater(() -> mainStage.setTitle(mainTitle + " - " + myNick));
                return true;
            case "/end":
                authPane.setVisible(true);
                authPane.setManaged(true);
                messagePane.setVisible(false);
                messagePane.setManaged(false);
                Platform.runLater(() -> {
                    mainStage.setTitle(mainTitle);
                    users.clear();
                });
                client.closeConnection();
                return true;
            case "/changenick":
                myNick = parts[1];
                Platform.runLater(() -> mainStage.setTitle(mainTitle + " - " + myNick));
                return true;
            default:
                return false;
        }
    }

}
