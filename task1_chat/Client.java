package task1_chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.net.Socket;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

class TcpMsgReceiver extends Thread {
    private BufferedReader in;
    private boolean running = true;

    public TcpMsgReceiver(BufferedReader inBuffer) {
        in = inBuffer;
    }

    private void safePrint(String str) {
        System.out.println();
        System.out.println(str);
        System.out.print("T> ");
        System.out.flush();
    }

    public void stopRunning() {
        running = false;
    }

    public void run() {
        String receivedMsg;
        try {
            while (running) {
                if ((receivedMsg = in.readLine()) != null) {
                    safePrint(receivedMsg);
                }
            }
        } catch (IOException e) {
            System.out.println("Server closed.");
            System.exit(0);
        }
    }
}

class UdpMsgReceiver extends Thread {
    private boolean running = true;
    private DatagramSocket udpSocket;

    public UdpMsgReceiver(DatagramSocket udpSocket) {
        this.udpSocket = udpSocket;
    }

    private void safePrint(String str) {
        System.out.println();
        System.out.println(str);
        System.out.print("T> ");
        System.out.flush();
    }

    public void stopRunning() {
        running = false;
    }

    public void run() {
        byte[] receiveBuffer;
        DatagramPacket receivePacket;
        String receivedData;
        while (running) {
            try {
                receiveBuffer = new byte[1024];
                receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                udpSocket.receive(receivePacket);
                receivedData = new String(receivePacket.getData(), 0, receivePacket.getLength(), "UTF-8");
                safePrint(receivedData);
            } catch (IOException e) {
                System.out.println("Server closed.");
                System.exit(0);
            }
        }
    }
}

class ClosingClientHandler extends Thread {
    private final Socket tcpSocket;
    private final DatagramSocket udpSocket;
    private final MulticastSocket udpMulticastSocket;
    private final TcpMsgReceiver tcpMsgReceiver;
    private final UdpMsgReceiver udpMsgReceiver;
    private final UdpMsgReceiver udpMulticastMsgReceiver;
    private final PrintWriter out;
    private final UdpMsgSenderData udpData;

    public ClosingClientHandler(Socket tcpSocket, DatagramSocket udpSocket, MulticastSocket udpMulticastSocket,
            TcpMsgReceiver tcpMsgReceiver,
            UdpMsgReceiver udpMsgReceiver, UdpMsgReceiver udpMulticastMsgReceiver, PrintWriter out,
            UdpMsgSenderData udpData) {
        this.tcpSocket = tcpSocket;
        this.udpSocket = udpSocket;
        this.udpMulticastSocket = udpMulticastSocket;
        this.tcpMsgReceiver = tcpMsgReceiver;
        this.udpMsgReceiver = udpMsgReceiver;
        this.udpMulticastMsgReceiver = udpMulticastMsgReceiver;
        this.out = out;
        this.udpData = udpData;
    }

    public void run() {
        System.out.print("Closing... ");
        if (tcpMsgReceiver != null) {
            tcpMsgReceiver.stopRunning();
        }
        if (udpMsgReceiver != null) {
            udpMsgReceiver.stopRunning();
        }
        if (udpMulticastMsgReceiver != null) {
            udpMulticastMsgReceiver.stopRunning();
        }
        out.println("closed");
        try {
            Client.sendUdpMsg("closed", udpData);
            if (tcpSocket != null)
                tcpSocket.close();
            if (udpSocket != null)
                udpSocket.close();
            if (udpMulticastSocket != null)
                udpMulticastSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("closed.");

    }
}

record UdpMsgSenderData(InetAddress address, int portNumber, DatagramSocket udpSocket) {
};

record MsgSenderData(UdpMsgSenderData udpData, UdpMsgSenderData udpMulticastData, PrintWriter tcpData) {
};

public class Client {
    public static void sendUdpMsg(String msg, UdpMsgSenderData udpData) throws IOException {
        byte[] sendBuffer = msg.getBytes("UTF-8");
        DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, udpData.address(),
                udpData.portNumber());
        udpData.udpSocket().send(sendPacket);
    }

    private static MsgSenderData init(String name) throws IOException {
        String hostName = "localhost";
        int portNumber = 12345;
        int multicastPortNumber = 12346;
        InetAddress group = InetAddress.getByName("230.0.0.1");
        System.out.println("Client started");
        Socket tcpSocket = null;
        DatagramSocket udpSocket = null;
        MulticastSocket udpMulticastSocket = null;
        TcpMsgReceiver msgReceiver = null;
        UdpMsgReceiver udpMsgReceiver = null;
        UdpMsgReceiver udpMulticastMsgReceiver = null;

        try {
            tcpSocket = new Socket(hostName, portNumber);
            udpSocket = new DatagramSocket();
            udpMulticastSocket = new MulticastSocket(multicastPortNumber);
            PrintWriter out = new PrintWriter(new OutputStreamWriter(
                    tcpSocket.getOutputStream(), StandardCharsets.UTF_8), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(tcpSocket.getInputStream(), StandardCharsets.UTF_8));
            UdpMsgSenderData udpData = new UdpMsgSenderData(InetAddress.getByName(hostName), portNumber, udpSocket);
            UdpMsgSenderData udpMulticastData = new UdpMsgSenderData(group, multicastPortNumber, udpMulticastSocket);
            udpMulticastSocket.joinGroup(group);

            msgReceiver = new TcpMsgReceiver(in);
            udpMsgReceiver = new UdpMsgReceiver(udpSocket);
            udpMulticastMsgReceiver = new UdpMsgReceiver(udpMulticastSocket);

            Runtime.getRuntime()
                    .addShutdownHook(
                            new ClosingClientHandler(tcpSocket, udpSocket, udpMulticastSocket, msgReceiver,
                                    udpMsgReceiver, udpMulticastMsgReceiver, out, udpData));

            out.println(name);
            String receivedMsg = in.readLine();
            System.out.println(receivedMsg);
            if (!receivedMsg.equals("connected")) {
                System.exit(0);
            }

            Client.sendUdpMsg(name, udpData);

            System.out.println("Type 'Q/' to exit");
            System.out.println("Type 'U/' to use UDP");
            System.out.println("Type 'M/' to use multicast UDP");
            System.out.print("T> ");
            msgReceiver.start();
            udpMsgReceiver.start();
            udpMulticastMsgReceiver.start();
            return new MsgSenderData(udpData, udpMulticastData, out);
        } catch (IOException e) {
            if (tcpSocket != null)
                tcpSocket.close();
            if (udpSocket != null)
                udpSocket.close();
            if (udpMulticastSocket != null)
                udpMulticastSocket.close();
            System.out.println("Init unsuccessfull");
            throw new IOException();
        }
    }

    public static void main(String[] args) throws IOException {
        try {
            MsgSenderData senderData = init(
                    args != null && args.length != 0 ? args[0] : "" + ProcessHandle.current().pid());
            PrintWriter out = senderData.tcpData();
            UdpMsgSenderData udpData = senderData.udpData();
            UdpMsgSenderData udpMulticastData = senderData.udpMulticastData();
            String msg;
            BufferedReader keyRead = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
            boolean udp = false;
            boolean multicast = false;
            while (true) {
                if ((msg = keyRead.readLine()) != null) {
                    // System.out.println(msg);
                    if (msg.equals("Q/"))
                        System.exit(0);
                    else if (msg.equals("U/")) {
                        System.out.print("U> ");
                        udp = true;
                    } else if (msg.equals("M/")) {
                        System.out.print("M> ");
                        multicast = true;
                    } else {
                        if (!msg.equals("")) {
                            if (udp) {
                                sendUdpMsg(msg, udpData);
                                udp = false;
                            } else if (multicast) {
                                sendUdpMsg(msg, udpMulticastData);
                                multicast = false;
                            } else {
                                out.println(msg);
                                // out.println("zażółć żółtą gęś");}
                            }
                        } else {
                            udp = false;
                            multicast = false;
                        }
                        System.out.print("T> ");
                    }
                }
            }
        } catch (IOException e) {
            System.exit(0);
        }
    }

}
