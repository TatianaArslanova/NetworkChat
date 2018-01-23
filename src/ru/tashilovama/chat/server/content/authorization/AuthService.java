package ru.tashilovama.chat.server.content.authorization;

public interface AuthService {
    String getNickByLoginPass(String login, String pass);
    boolean changeNick(String oldNick, String newNick);
    void start();
    void stop();
}
