package ru.tashilovama.chat.server;

import ru.tashilovama.chat.server.authorization.AuthService;
import ru.tashilovama.chat.server.authorization.DBAuthorization;
import ru.tashilovama.chat.server.authorization.GuestAuthorization;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
import java.util.Vector;

public class MyServer {
    private ServerSocket server;
    private Vector<ClientHandler> clients;
    private GuestAuthorization guestAuth;
    private AuthService authService;
    private Callback controller;

    public interface Callback {
        void callMeBack(String message);
    }

    public void registerCallback(Callback controller) {
        this.controller = controller;
    }

    public void startServer(int port) {
        try {
            server = new ServerSocket(port);
            clients = new Vector<>();
            guestAuth = new GuestAuthorization();
            authService = new DBAuthorization();
            printWithDate("Сервер запущен. Порт: " + port);
            authService.start();
            Thread thread = new Thread(() -> {
                try {
                    while (true) {
                        printWithDate("Сервер ожидает подключения");
                        Socket socket = server.accept();
                        printWithDate("Клиент подключился");
                        createClient(socket);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    stopServer();
                }
            });
            thread.setDaemon(true);
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopServer() {
        try {
            if (server != null && !server.isClosed()) {
                broadcastMessage("/system /alert Неполадки на сервере. Попробуйте подключиться позже.");
                broadcastMessage("/end");
                authService.stop();
                server.close();
                printWithDate("Сервер остановлен");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printWithDate(String message){
        controller.callMeBack(Calendar.getInstance().getTime().toString()+": "+message);
    }

    private void createClient(Socket socket) {
        new ClientHandler(this, socket);
    }

    public synchronized void broadcastMessage(String message) {
        for (ClientHandler o : clients) {
            o.sendMsg(message);
        }
    }

    public synchronized void broadcastClientList() {
        StringBuilder clientList = new StringBuilder("/system /clientlist ");
        for (ClientHandler o : clients) {
            clientList.append(o.getNick() + " ");
        }
        broadcastMessage(clientList.toString());
    }

    public synchronized void subscribe(ClientHandler client) {
        clients.add(client);
        printWithDate("Клиент прошел авторизацию: " + client.getNick() + "\nАвторизованных пользователей: " + clients.size());
        broadcastClientList();
    }

    public synchronized void unsubscribe(ClientHandler client) {
        if (client.getRights() == Rights.GUEST) {
            guestAuth.removeGuest(client.getNick());
        }
        if (clients.contains(client)) {
            clients.remove(client);
            broadcastClientList();
            broadcastMessage(client.getNick() + " покидает чат");
        }
        String currentNick = client.getNick() == null ? "(не авторизован)" : client.getNick();
        printWithDate("Клиент отключен: " + currentNick + "\nАвторизованных пользователей: " + clients.size());
    }

    public synchronized boolean wispMsg(String toNick, String message) {
        for (ClientHandler o : clients) {
            if (o.getNick().equals(toNick)) {
                o.sendMsg(message);
                return true;
            }
        }
        return false;
    }

    public synchronized boolean isNickBusy(String nick) {
        for (ClientHandler o : clients) {
            if (o.getNick().equals(nick)) return true;
        }
        return false;
    }

    public GuestAuthorization getGuestAuth() {
        return guestAuth;
    }

    public AuthService getAuthService() {
        return authService;
    }
}
