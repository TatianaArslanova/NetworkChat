package ru.tashilovama.chat.server;

import ru.tashilovama.chat.server.authorization.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class MyServer {
    private ServerSocket server;
    private Vector<ClientHandler> clients;
    private final int PORT=5555;
    private GuestAuthorization guestAuth;
    private AuthService authService;

    public MyServer(){
        try {
            server=new ServerSocket(PORT);
            clients=new Vector<>();
            guestAuth=new GuestAuthorization();
            authService=new DBAuthorization();
            System.out.println("Сервер запущен");
            if (!authService.start()) {
                authService = new BaseAuthorization();
                authService.start();
            }
            new Thread(() -> {
                try{
                    while (true) {
                        System.out.println("Сервер ожидает подключения");
                        Socket socket = server.accept();
                        System.out.println("Клиент подключился");
                        createClient(socket);
                    }
                }catch (IOException e){
                    e.printStackTrace();
                } finally {
                    authService.stop();
                }
            }).start();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void createClient(Socket socket){
        new ClientHandler(this,socket);
    }

    public synchronized void broadcastMessage(String message){
        for (ClientHandler o: clients){
                o.sendMsg(message);
        }
    }

    private synchronized void broadcastClientList(){
        StringBuilder clientList=new StringBuilder("/clientlist ");
        for (ClientHandler o: clients){
            clientList.append(o.getNick()+"\n");
        }
        broadcastMessage(clientList.toString());
    }

    public synchronized void subscribe(ClientHandler client){
        clients.add(client);
        broadcastClientList();
    }

    public synchronized void unsubscribe(ClientHandler client){
        if (client.getRights()== Rights.GUEST){
            guestAuth.removeGuest(client.getNick());
        }
        clients.remove(client);
        broadcastClientList();
        broadcastMessage(client.getNick() + " покидает чат");
    }

    public synchronized boolean wispMsg (String toNick, String message){
        for (ClientHandler o: clients){
            if (o.getNick().equals(toNick)){
                o.sendMsg(message);
                return true;
            }
        }
        return false;
    }

    public synchronized boolean isNickBusy(String nick){
        for (ClientHandler o: clients){
            if (o.getNick().equals(nick)) return true;
        }
        return false;
    }

    public GuestAuthorization getGuestAuth() {
        return guestAuth;
    }

    public AuthService getAuthService() {
        return authService;
    }
}
