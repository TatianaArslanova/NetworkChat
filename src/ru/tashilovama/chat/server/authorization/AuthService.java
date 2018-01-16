package ru.tashilovama.chat.server.authorization;

public interface AuthService {
    String getNickByLoginPass(String login, String pass);
    boolean start();
    void stop();
}
