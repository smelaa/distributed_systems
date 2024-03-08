package task1_chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.net.DatagramSocket;
import java.util.Arrays;
import java.net.DatagramPacket;

class TcpClientHandler extends Thread {
    private PrintWriter outClient;
    private BufferedReader inClient;
    private String name;
    private TcpClientRegister register;
    private boolean running = true;

    public TcpClientHandler(TcpClientRegister register, String name, PrintWriter outClient, BufferedReader inClient) {
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
                        System.out.println(name + " sent message via TCP.");
                        register.sendToAll(name, msgFromClient);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

class UdpClientsHandler extends Thread {
    private HashMap<SocketAddress, String> clients = new HashMap<>();
    private DatagramSocket socket;
    private boolean running = true;

    public UdpClientsHandler(DatagramSocket socket) {
        this.socket = socket;
    }

    private void sendToAll(SocketAddress senderAddress, String msg) throws IOException {
        String name = clients.get(senderAddress);
        System.out.println(name + " sent message via UDP.");
        String msgToSend = name + ": " + msg;
        byte[] sendBuffer = msgToSend.getBytes("UTF-8");
        for (SocketAddress address : clients.keySet()) {
            if (!address.equals(senderAddress)) {
                DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, address);
                socket.send(sendPacket);
            }
        }

    }

    public void stopRunning() {
        running = false;
    }

    public void run() {
        byte[] receiveBuffer = new byte[1024];
        String msg;
        DatagramPacket receivePacket;
        SocketAddress senderAddress;
        while (running) {
            try {
                Arrays.fill(receiveBuffer, (byte) 0);
                receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(receivePacket);
                msg = new String(receivePacket.getData(), 0, receivePacket.getLength(), "UTF-8");
                senderAddress = receivePacket.getSocketAddress();
                if (msg.equals("closed")) {
                    clients.remove(senderAddress);
                } else if (!clients.keySet().contains(senderAddress)) {
                    clients.put(senderAddress, msg);
                    System.out.println(msg + " connected via UDP.");
                } else
                    sendToAll(senderAddress, msg);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
    }

}

class TcpClientRegister {
    private HashMap<String, TcpClientHandler> clientHandlers = new HashMap<>();
    private boolean writing = false;
    private Integer readers = 0;

    private void reader(int amount) {
        synchronized (clientHandlers) {
            readers += amount;
        }
    }

    private boolean isReading() {
        synchronized (clientHandlers) {
            return readers != 0;
        }
    }

    public synchronized void sendToAll(String senderName, String msg) {
        while (writing) {
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
        while (writing) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        boolean result = true;
        reader(1);
        for (String unavailableName : clientHandlers.keySet()) {
            if (unavailableName.equals(name)) {
                result = false;
                break;
            }
        }
        reader(-1);
        notifyAll();
        return result;
    }

    public synchronized void addClient(String name, TcpClientHandler handler) {
        while (isReading() || writing) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        writing = true;
        clientHandlers.put(name, handler);
        writing = false;
        notifyAll();
    }

    public synchronized void removeClient(String name) {
        while (isReading() || writing) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        writing = true;
        clientHandlers.remove(name);
        writing = false;
        notifyAll();
    }

    public synchronized void stopAllThreads() {
        while (writing) {
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
    private final ServerSocket tcpServerSocket;
    private final TcpClientRegister clientRegister;
    private final DatagramSocket udpServerSocket;
    private final UdpClientsHandler udpHandler;

    public ClosingServerHandler(ServerSocket tcpSocket, TcpClientRegister register, DatagramSocket udpSocket,
            UdpClientsHandler udpHandler) {
        this.tcpServerSocket = tcpSocket;
        this.clientRegister = register;
        this.udpServerSocket = udpSocket;
        this.udpHandler = udpHandler;
    }

    public void run() {
        System.out.print("Server closing... ");
        clientRegister.stopAllThreads();
        if (udpHandler != null)
            udpHandler.stopRunning();
        try {
            if (tcpServerSocket != null)
                tcpServerSocket.close();
            if (udpServerSocket != null)
                udpServerSocket.close();
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
        TcpClientRegister register = new TcpClientRegister();
        UdpClientsHandler udpHandler = null;

        try {
            tcpServerSocket = new ServerSocket(portNumber);
            udpServerSocket = new DatagramSocket(portNumber);
            udpHandler = new UdpClientsHandler(udpServerSocket);
            Runtime.getRuntime()
                    .addShutdownHook(new ClosingServerHandler(tcpServerSocket, register, udpServerSocket, udpHandler));
            udpHandler.start();
            while (true) {
                Socket clientSocket = tcpServerSocket.accept();
                PrintWriter outClient = new PrintWriter(new OutputStreamWriter(
                        clientSocket.getOutputStream(), StandardCharsets.UTF_8), true);
                BufferedReader inClient = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
                String name = inClient.readLine();
                if (register.isNameAvailable(name)) {
                    System.out.println(name + " connected via TCP");
                    outClient.println("connected");
                    TcpClientHandler handler = new TcpClientHandler(register, name, outClient, inClient);
                    register.addClient(name, handler);
                    handler.start();
                } else {
                    System.out.println("unsuccessfull tcp connection try");
                    outClient.println("nickname unavailable");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            new ClosingServerHandler(tcpServerSocket, register, udpServerSocket, udpHandler).run();
        }
    }

}
