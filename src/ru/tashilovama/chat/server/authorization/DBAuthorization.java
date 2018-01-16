package ru.tashilovama.chat.server.authorization;

import java.sql.*;

public class DBAuthorization implements AuthService{
    private Connection connection;
    private PreparedStatement ps;

    public boolean start() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection= DriverManager.getConnection("jdbc:sqlite:authBase.db");
            ps=connection.prepareStatement("SELECT nick FROM authTable WHERE login = ? AND pass = ?;");
        } catch (Exception e) {
        //    e.printStackTrace();
            System.out.println("Ошибка при подключении к БД");
            return false;
        }
        return true;
    }

    public String getNickByLoginPass(String login, String pass){
        try {
            ps.setString(1,login);
            ps.setString(2,pass);
            ResultSet rs=ps.executeQuery();
            if (rs.next()){
                System.out.println("Авторизация через БД "+rs.getString(1));
                return rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void stop(){
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
