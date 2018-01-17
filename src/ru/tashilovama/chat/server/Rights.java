package ru.tashilovama.chat.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public enum Rights {
    NOT_AUTHORIZED  (true,true,false,false,false),
    BANNED          (false,false,true,false,false),
    GUEST           (false,false,true,false,false),
    AUTHORIZED      (false,false,true,true,true),
    ADMIN           (false,false,true,true,true);

    private final HashMap<String, Boolean> ALLOWS;
    private final List<String> COMMANDS;

    Rights(Boolean... isAllow) {
        ALLOWS = new HashMap<>();
        COMMANDS=new ArrayList<>(Arrays.asList("/auth", "/guestauth", "/end", "/wisp", "/changenick"));
        for (int i = 0; i < isAllow.length; i++) {
            ALLOWS.put(COMMANDS.get(i), isAllow[i]);
        }
    }

    public boolean getAllow(String command){
            return ALLOWS.get(command);
    }

    public boolean isCommand(String str){
        return COMMANDS.contains(str);
    }
}
