package ru.tashilovama.chat.client.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import ru.tashilovama.chat.client.content.Client;
import ru.tashilovama.chat.client.content.Main;

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
        users = FXCollections.observableArrayList();
        clientList.setItems(users);
        Main.getInstance().registerCallback(stage -> {
            mainStage = stage;
            mainTitle = stage.getTitle();
        });
        client = Main.getInstance().getClient();
        client.registerCallback(message -> Platform.runLater(() -> {
            if (!message.startsWith("/") || !executeIfIsCommand(message)) {
                chatTextArea.appendText(message + "\n");
            }
        }));
    }

    public void onSend() {
        if (messageField.getText().startsWith("/system")) chatTextArea.appendText("Недопустимая команда\n");
        else {
            client.writeMsg(messageField.getText());
            messageField.clear();
            messageField.requestFocus();
        }
    }

    public void guestAuthClick() {
        client.guestAuth();
    }

    public void userAuthClick() {
        client.authByLoginPass(loginField.getText(), passField.getText());
        loginField.clear();
        passField.clear();
    }

    private void updateClientList(String message) {
        String[] clients = message.split(" ");
        Platform.runLater(() -> users.setAll(clients));
    }

    public void listClick(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2 && clientList.getSelectionModel().getSelectedItem() != null) {
            messageField.setText(clientList.getSelectionModel().getSelectedItem() + ", ");
            clientList.getSelectionModel().clearSelection();
            messageField.requestFocus();
            messageField.end();
        }
    }

    private void setCompositeClientList() {
        clientList.setCellFactory(new Callback<>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!empty || item != null) {
                            Button letter = new Button("", new Rectangle(10, 10, Color.WHITE));
                            letter.setOnAction(Event -> {
                                messageField.setText("/wisp " + item + " ");
                                messageField.requestFocus();
                                messageField.end();
                            });
                            setGraphic(letter);
                            setText(item);
                        } else {
                            setGraphic(null);
                            setText("");
                        }
                    }
                };
            }
        });
    }

    private void showAlert(String alertMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(mainStage);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setTitle("Возникли проблемы");
        alert.setHeaderText("Возникли проблемы");
        alert.setContentText(alertMessage);
        alert.showAndWait();
    }

    private void setOriginalView(Boolean originalView) {
        authPane.setVisible(originalView);
        authPane.setManaged(originalView);
        messagePane.setVisible(!originalView);
        messagePane.setManaged(!originalView);
    }

    private boolean executeIfIsCommand(String message) {
        final int PARTS_LIMIT = 3;
        String[] parts = message.split(" ", PARTS_LIMIT);
        String command = parts[0];
        String specifiadMessage;
        String myNick;
        switch (command) {
            case "/system":
                String subcommand = parts[1];
                switch (subcommand) {
                    case "/clientlist":
                        specifiadMessage = parts[2];
                        updateClientList(specifiadMessage);
                        return true;
                    case "/alert":
                        specifiadMessage = parts[2];
                        showAlert(specifiadMessage);
                        return true;
                    default:
                        return true;
                }
            case "/guestauth":
                myNick = parts[1];
                setOriginalView(false);
                mainStage.setTitle(mainTitle + " - " + myNick);
                return true;
            case "/auth":
                myNick = parts[1];
                setOriginalView(false);
                setCompositeClientList();
                mainStage.setTitle(mainTitle + " - " + myNick);
                return true;
            case "/end":
                setOriginalView(true);
                clientList.setCellFactory(null);
                mainStage.setTitle(mainTitle);
                users.clear();
                client.closeConnection();
                return true;
            case "/changenick":
                myNick = parts[1];
                mainStage.setTitle(mainTitle + " - " + myNick);
                return true;
            default:
                return false;
        }
    }

}
