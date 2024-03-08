package task1_chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

class ClientHandler extends Thread {
    private PrintWriter outClient;
    private BufferedReader inClient;
    private String name;
    private ClientRegister register;
    private boolean running = true;

    public ClientHandler(ClientRegister register, String name, PrintWriter outClient, BufferedReader inClient) {
        this.register = register;
        this.name = name;
        this.outClient = outClient;
        this.inClient = inClient;
    }

    public void send(String msg) {
        outClient.println(msg);
    }

    public void stopRunning() {
        running = false;
    }

    public void run() {
        String msgFromClient;
        while (running) {
            try {
                if ((msgFromClient = inClient.readLine()) != null) {
                    if (msgFromClient.equals("closed")) {
                        System.out.println(name + " left.");
                        stopRunning();
                        register.removeClient(name);
                    } else {
                        System.out.println(name + " sent message.");
                        register.sendToAll(name, msgFromClient);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

class ClientRegister {
    private HashMap<String, ClientHandler> clientHandlers = new HashMap<>();

    public void sendToAll(String senderName, String msg) {
        String msgToSend = senderName + ": " + msg;
        for (String clientName : clientHandlers.keySet()) {
            if (clientName != senderName) {
                clientHandlers.get(clientName).send(msgToSend);
            }
        }
    }

    public boolean isNameAvailable(String name) {
        for (String unavailableName : clientHandlers.keySet()) {
            if (unavailableName.equals(name)) {
                return false;
            }
        }
        return true;
    }

    public void addClient(String name, ClientHandler handler) {
        clientHandlers.put(name, handler);
    }

    public void removeClient(String name) {
        clientHandlers.remove(name);
    }

    public void stopAllThreads() {
        for (ClientHandler thread : clientHandlers.values()) {
            thread.stopRunning();
            thread.interrupt();
        }
    }
}

class ClosingServerHandler extends Thread {
    private final ServerSocket serverSocket;
    private final ClientRegister clientRegister;

    public ClosingServerHandler(ServerSocket socket, ClientRegister register) {
        serverSocket = socket;
        clientRegister = register;
    }

    public void run() {
        System.out.print("Server closing... ");
        clientRegister.stopAllThreads();
        try {
            if (serverSocket != null)
                serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("closed.");
    }
}

public class Server {
    public static void main(String[] args) {

        System.out.println("Server started");

        int portNumber = 12345;
        ServerSocket serverSocket = null;
        ClientRegister register = new ClientRegister();

        try {
            serverSocket = new ServerSocket(portNumber);
            Runtime.getRuntime().addShutdownHook(new ClosingServerHandler(serverSocket, register));
            while (true) {
                Socket clientSocket = serverSocket.accept();
                PrintWriter outClient = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader inClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String name = inClient.readLine();
                if (register.isNameAvailable(name)) {
                    System.out.println(name + " connected");
                    outClient.println("connected");
                    ClientHandler handler = new ClientHandler(register, name, outClient, inClient);
                    register.addClient(name, handler);
                    handler.start();
                } else {
                    System.out.println("unsuccessfull connection try");
                    outClient.println("nickname unavailable");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            new ClosingServerHandler(serverSocket, register).run();
        }
    }

}
