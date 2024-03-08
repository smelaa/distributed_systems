package task1_chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.nio.charset.StandardCharsets;
import java.net.DatagramSocket;

class TcpClientHandler extends Thread {
    private PrintWriter outClient;
    private BufferedReader inClient;
    private String name;
    private ClientRegister register;
    private boolean running = true;

    public TcpClientHandler(ClientRegister register, String name, PrintWriter outClient, BufferedReader inClient) {
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

class UdpClientHandler extends Thread{

}

class ClientRegister {
    private HashMap<String, TcpClientHandler> clientHandlers = new HashMap<>();
    private boolean writing = false;
    private Integer readers =0;

    private void reader(int amount){
        synchronized(clientHandlers){
            readers+=amount;
        }
    }

    private boolean isReading(){
        synchronized(clientHandlers){
            return readers!=0;
        }
    }

    public synchronized void sendToAll(String senderName, String msg) {
        while(writing){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        reader(1);
        String msgToSend = senderName + ": " + msg;
        for (String clientName : clientHandlers.keySet()) {
            if (clientName != senderName) {
                clientHandlers.get(clientName).send(msgToSend);
            }
        }
        reader(-1);
        notifyAll();
    }

    public synchronized boolean isNameAvailable(String name) {
        while(writing){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        boolean result=true;
        reader(1);
        for (String unavailableName : clientHandlers.keySet()) {
            if (unavailableName.equals(name)) {
                result=false;
                break;
            }
        }
        reader(-1);
        notifyAll();
        return result;
    }

    public synchronized void addClient(String name, TcpClientHandler handler) {
        while(isReading() || writing){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        writing=true;
        clientHandlers.put(name, handler);
        writing=false;
        notifyAll();
    }

    public synchronized void removeClient(String name) {
        while(isReading() || writing){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        writing=true;
        clientHandlers.remove(name);
        writing=false;
        notifyAll();
    }

    public synchronized void stopAllThreads() {
        while(writing){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        reader(1);
        for (TcpClientHandler thread : clientHandlers.values()) {
            thread.stopRunning();
        }
        reader(-1);
        notifyAll();
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
        ServerSocket tcpServerSocket = null;
        DatagramSocket udpServerSocket = null;
        ClientRegister register = new ClientRegister();

        try {
            tcpServerSocket = new ServerSocket(portNumber);
            udpServerSocket = new DatagramSocket(portNumber);
            Runtime.getRuntime().addShutdownHook(new ClosingServerHandler(tcpServerSocket, register));
            while (true) {
                Socket clientSocket = tcpServerSocket.accept();
                PrintWriter outClient = new PrintWriter(new OutputStreamWriter(
                        clientSocket.getOutputStream(), StandardCharsets.UTF_8), true);
                BufferedReader inClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
                String name = inClient.readLine();
                if (register.isNameAvailable(name)) {
                    System.out.println(name + " connected");
                    outClient.println("connected");
                    TcpClientHandler handler = new TcpClientHandler(register, name, outClient, inClient);
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
            new ClosingServerHandler(tcpServerSocket, register).run();
        }
    }

}
