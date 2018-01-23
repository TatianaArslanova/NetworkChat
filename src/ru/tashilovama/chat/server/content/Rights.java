package ru.tashilovama.chat.server.content;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public enum Rights {
    NOT_AUTHORIZED  (true,true,false,false,false,false),
    BANNED          (false,false,true,false,false,false),
    GUEST           (false,false,true,false,false,false),
    AUTHORIZED      (false,false,true,true,true,false),
    ADMIN           (false,false,true,true,true,false);

    private final HashMap<String, Boolean> ALLOWS;
    private final List<String> COMMANDS;

    Rights(Boolean... isAllow) {
        ALLOWS = new HashMap<>();
        COMMANDS=new ArrayList<>(Arrays.asList("/auth", "/guestauth", "/end", "/wisp", "/changenick", "/system"));
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
