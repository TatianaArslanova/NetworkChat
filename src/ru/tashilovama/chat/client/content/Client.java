package ru.tashilovama.chat.client.content;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private final String host = "localhost";
    private final int PORT = 5555;
    private Callback controller;

    public interface Callback{
        void callMeBack(String message);
    }

    public void registerCallback(Callback controller){
        this.controller=controller;
    }

    public void startConnection(){
        if (socket==null || socket.isClosed()) {
            try {
                socket = new Socket(host, PORT);
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
                        //  e.printStackTrace();
                        System.out.println("Клиент отключился");
                    } finally {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.setDaemon(true);
                thread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void closeConnection(){
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
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
