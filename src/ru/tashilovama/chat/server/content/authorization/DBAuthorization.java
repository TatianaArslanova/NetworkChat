package ru.tashilovama.chat.server.content.authorization;

import java.sql.*;

public class DBAuthorization implements AuthService {
    private Connection connection;
    private PreparedStatement findNick;
    private PreparedStatement checkNick;
    private PreparedStatement setNick;

    public void start() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection= DriverManager.getConnection("jdbc:sqlite:authBase.db");
            findNick=connection.prepareStatement("SELECT nick FROM authTable WHERE login = ? AND pass = ?;");
            checkNick=connection.prepareStatement("SELECT COUNT(*) FROM authTable WHERE nick = ?;");
            setNick=connection.prepareStatement("UPDATE authTable SET nick = ? WHERE nick = ?;");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getNickByLoginPass(String login, String pass){
        try {
            findNick.setString(1,login);
            findNick.setString(2,pass);
            ResultSet rs=findNick.executeQuery();
            if (rs.next()){
                return rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean changeNick(String oldNick, String newNick) {
        try{
            checkNick.setString(1,newNick);
            ResultSet rs=checkNick.executeQuery();
            rs.next();
            if (rs.getInt(1)==0){
                setNick.setString(1,newNick);
                setNick.setString(2,oldNick);
                setNick.executeUpdate();
                return true;
            }
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public void stop(){
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
