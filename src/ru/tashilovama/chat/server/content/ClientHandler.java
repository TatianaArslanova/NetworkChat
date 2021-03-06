package ru.tashilovama.chat.server.content;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ClientHandler {
    private MyServer myServer;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String nick;
    private Rights rights;

    public String getNick() {
        return nick;
    }

    public Rights getRights() {
        return rights;
    }

    public ClientHandler(MyServer myServer, Socket socket) {
        try {
            this.myServer = myServer;
            this.socket = socket;
            socket.setSoTimeout(120000);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            this.nick = "";
            rights = Rights.NOT_AUTHORIZED;
            new Thread(() -> {
                try {
                    while (true) {
                        try {
                            String message;
                            message = in.readUTF();
                            if (!message.startsWith("/") || !executeIfIsCommand(message))
                                myServer.broadcastMessage(nick + ": " + message);
                        } catch (SocketTimeoutException e) {
                            e.printStackTrace();
                            if (rights == Rights.NOT_AUTHORIZED) {
                                disconnect();
                            } else socket.setSoTimeout(0);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Клиент отключен");
                } finally {
                    myServer.unsubscribe(this);
                    disconnect();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void disconnect() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void changeNick(String newNick) {
        myServer.broadcastMessage(nick + " меняет ник на " + newNick);
        nick = newNick;
        myServer.broadcastClientList();
    }

    private boolean executeIfIsCommand(String message) {
        final int PARTS_LIMIT = 3;
        final int MIN_NICK_SIZE = 2;
        final int MAX_NICK_SIZE = 30;
        String[] parts = message.split(" ", PARTS_LIMIT);
        String command = parts[0].toLowerCase();
        String login;
        String pass;
        String specifiadNick;
        String specifiadMessage;
        if (rights.isCommand(command)) {
            if (rights.getAllow(command)) {
                switch (command) {
                    case "/guestauth":
                        nick = myServer.getGuestAuth().getGuestName();
                        if (nick == null) sendMsg("В чате заняты все места для гостей. Попробуйте позже.");
                        else {
                            sendMsg("Вы авторизованы как " + nick);
                            rights = Rights.GUEST;
                            sendMsg(command + " " + nick);
                            myServer.subscribe(this);
                            myServer.broadcastMessage(nick + " входит в чат");
                        }
                        return true;
                    case "/auth":
                        login = parts[1].toLowerCase();
                        pass = parts[2];
                        nick = myServer.getAuthService().getNickByLoginPass(login, pass);
                        if (nick == null) sendMsg("Неверный логин или пароль");
                        else if (!myServer.isNickBusy(nick)) {
                            sendMsg("Вы авторизованы как " + nick);
                            rights = Rights.AUTHORIZED;
                            sendMsg(command + " " + nick);
                            myServer.subscribe(this);
                            myServer.broadcastMessage(nick + " входит в чат");
                        } else sendMsg("Учетная запись уже используется");
                        return true;
                    case "/end":
                        sendMsg("Вы вышли из учетной записи");
                        sendMsg(command);
                        return true;
                    case "/wisp":
                        if (parts.length != PARTS_LIMIT) {
                            sendMsg("Команда сформулирована неверно: \"" + message.trim() + "\"");
                            return true;
                        }
                        specifiadNick = parts[1];
                        specifiadMessage = parts[2];
                        if (myServer.wispMsg(specifiadNick, "Личное сообщение от " + nick + ": " + specifiadMessage)) {
                            sendMsg("Личное сообщение для " + specifiadNick + ": " + specifiadMessage);
                        } else sendMsg("Адресат " + specifiadNick + " не найден для сообщения: " + specifiadMessage);
                        return true;
                    case "/changenick":
                        if (parts.length < 2) {
                            sendMsg("Команда сформулирована неверно: \"" + message.trim() + "\"");
                            return true;
                        }
                        specifiadNick = parts[1].trim();
                        if (rights.isCommand(specifiadNick)) sendMsg("Некорректный ник");
                        else if (specifiadNick.length() < MIN_NICK_SIZE || specifiadNick.length() > MAX_NICK_SIZE)
                            sendMsg("Ник должен содержать от " + MIN_NICK_SIZE + " до " + MAX_NICK_SIZE + " символов");
                        else if (myServer.getAuthService().changeNick(nick, specifiadNick)) {
                            changeNick(specifiadNick);
                            sendMsg(command + " " + specifiadNick);
                        } else sendMsg("Указанный ник занят");
                        return true;
                }
            } else {
                sendMsg("Недостаточно прав для использования команды");
                return true;
            }
        }
        return false;
    }

}