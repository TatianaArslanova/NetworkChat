package ru.tashilovama.chat.client.controllers;

import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import ru.tashilovama.chat.client.content.Main;
import ru.tashilovama.chat.client.content.Client;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerMainStage implements Initializable{

    public TextArea chatTextArea;
    public TextField messageField;
    public TextField loginField;
    public PasswordField passField;
    public HBox messagePane;
    public HBox authPane;

    private Client client;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        client=Main.getInstance().getClient();
        client.registerCallback(message ->
        {if (!message.startsWith("/") || !executeIfIsCommand(message)){
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
        client.writeMsg("/guestauth");
    }

    public void userAuthClick() {
        client.writeMsg("/auth " + loginField.getText() + " " + passField.getText());
        loginField.clear();
        passField.clear();
    }

    private boolean executeIfIsCommand(String message) {
        String[] parts = message.split(" ", 3);
        String command = parts[0];
        switch (command) {
            case "/guestauth":
                authPane.setVisible(false);
                messagePane.setVisible(true);
                return true;
            case "/auth":
                authPane.setVisible(false);
                messagePane.setVisible(true);
                return true;
            case "/end":
                authPane.setVisible(true);
                messagePane.setVisible(false);
                return true;
            default:
                return false;
        }
    }
}
