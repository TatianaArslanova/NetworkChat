package ru.tashilovama.chat.server;

public enum Rights {
    NOT_AUTHORIZED(0), BANNED(1), GUEST(2), AUTHORIZED(3), ADMIN(4);

    private final int accessLevel;

    Rights(int accessLevel){
        this.accessLevel=accessLevel;
    }
}
