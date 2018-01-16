package ru.tashilovama.chat.server.authorization;

import java.util.ArrayList;

public class BaseAuthorization implements AuthService {
    private class User{
        private String login;
        private String pass;
        private String nick;

        public User(String login, String pass, String nick){
            this.login=login;
            this.pass=pass;
            this.nick=nick;
        }
    }

    private ArrayList<User> users;

    public BaseAuthorization (){
        users=new ArrayList<>();
        users.add(new User("admin", "admin", "admin"));
        users.add(new User("login1", "pass1", "nick1"));
        users.add(new User("login2", "pass2", "nick2"));
    }

    @Override
    public boolean start() {
        return true;
    }

    @Override
    public void stop() {}

    @Override
    public String getNickByLoginPass(String login, String pass) {
        for (User o: users){
            if (o.login.equals(login)&&o.pass.equals(pass)){
                System.out.println("Базовая авторизация "+o.nick);
                return o.nick;
            }
        }
        return null;
    }
}
