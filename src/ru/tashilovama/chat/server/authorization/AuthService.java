package ru.tashilovama.chat.server.authorization;

import ru.tashilovama.chat.server.ClientHandler;

public interface AuthService {
    String getNickByLoginPass(String login, String pass);
    boolean changeNick(String oldNick, String newNick);
    boolean start();
    void stop();
}
