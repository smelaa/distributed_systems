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
    private final TcpMsgReceiver tcpMsgReceiver;
    private final UdpMsgReceiver udpMsgReceiver;
    private final PrintWriter out;
    private final UdpMsgSenderData udpData;

    public ClosingClientHandler(Socket tcpSocket, DatagramSocket udpSocket, TcpMsgReceiver tcpMsgReceiver,
            UdpMsgReceiver udpMsgReceiver, PrintWriter out, UdpMsgSenderData udpData) {
        this.tcpSocket = tcpSocket;
        this.udpSocket = udpSocket;
        this.tcpMsgReceiver = tcpMsgReceiver;
        this.udpMsgReceiver = udpMsgReceiver;
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
        out.println("closed");
        try {
            Client.sendUdpMsg("closed", udpData);
            if (tcpSocket != null)
                tcpSocket.close();
            if (udpSocket != null)
                udpSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("closed.");

    }
}

record UdpMsgSenderData(InetAddress address, int portNumber, DatagramSocket udpSocket) {
};

record MsgSenderData(UdpMsgSenderData udpData, PrintWriter tcpData) {
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
        System.out.println("Client started");
        Socket tcpSocket = null;
        DatagramSocket udpSocket = null;
        TcpMsgReceiver msgReceiver = null;
        UdpMsgReceiver udpMsgReceiver = null;

        try {
            tcpSocket = new Socket(hostName, portNumber);
            udpSocket = new DatagramSocket();
            PrintWriter out = new PrintWriter(new OutputStreamWriter(
                    tcpSocket.getOutputStream(), StandardCharsets.UTF_8), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(tcpSocket.getInputStream(), StandardCharsets.UTF_8));
            UdpMsgSenderData udpData = new UdpMsgSenderData(InetAddress.getByName(hostName), portNumber, udpSocket);

            msgReceiver = new TcpMsgReceiver(in);
            udpMsgReceiver = new UdpMsgReceiver(udpSocket);

            Runtime.getRuntime()
                    .addShutdownHook(
                            new ClosingClientHandler(tcpSocket, udpSocket, msgReceiver, udpMsgReceiver, out, udpData));

            out.println(name);
            String receivedMsg = in.readLine();
            System.out.println(receivedMsg);
            if (!receivedMsg.equals("connected")) {
                System.exit(0);
            }

            Client.sendUdpMsg(name, udpData);

            System.out.println("Type 'Q/' to exit");
            System.out.println("Type 'U/' to use UDP");
            System.out.print("T> ");
            msgReceiver.start();
            udpMsgReceiver.start();
            return new MsgSenderData(udpData, out);
        } catch (IOException e) {
            if (tcpSocket != null)
                tcpSocket.close();
            if (udpSocket != null)
                udpSocket.close();
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
            String msg;
            BufferedReader keyRead = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
            boolean udp = false;
            while (true) {
                if ((msg = keyRead.readLine()) != null) {
                    // System.out.println(msg);
                    if (msg.equals("Q/"))
                        System.exit(0);
                    else if (msg.equals("U/")) {
                        System.out.print("U> ");
                        udp = true;
                    } else {
                        if (!msg.equals("")) {
                            if (udp) {
                                sendUdpMsg(msg, udpData);
                                udp = false;
                            } else {
                                out.println(msg);
                                // out.println("zażółć żółtą gęś");}
                            }
                        } else {
                            udp = false;
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
