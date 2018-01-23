package ru.tashilovama.chat.client.content;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String host = "localhost";
    private int port = 5555;
    private Callback controller;

    public interface Callback {
        void callMeBack(String message);
    }

    public void registerCallback(Callback controller) {
        this.controller = controller;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    private void startConnection() throws IOException {
        if (socket == null || socket.isClosed()) {
            try {
                socket = new Socket(host, port);
                in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());
                Thread thread = new Thread(() -> {
                    try {
                        while (true) {
                            String message;
                            message = in.readUTF();
                            controller.callMeBack(message);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("Клиент отключился");
                    } finally {
                        closeConnection();
                    }
                });
                thread.setDaemon(true);
                thread.start();
            } catch (IOException e) {
                e.printStackTrace();
                throw new IOException("Connection problem");
            }
        }
    }

    public void closeConnection() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setConnectionSettings(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void guestAuth() {
        try {
            startConnection();
            writeMsg("/guestauth");
        } catch (IOException e) {
            e.printStackTrace();
            controller.callMeBack("/system /alert Сервер не отвечает. Попробуйте подключиться позже.");
        }

    }

    public void authByLoginPass(String login, String pass) {
        try {
            startConnection();
            writeMsg("/auth " + login + " " + pass);
        } catch (IOException e) {
            e.printStackTrace();
            controller.callMeBack("/system /alert Сервер не отвечает. Попробуйте подключиться позже.");
        }

    }

    public void writeMsg(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
