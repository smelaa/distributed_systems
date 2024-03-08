package task1_chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class MsgReceiver extends Thread {
    private BufferedReader in;
    private boolean running = true;

    public MsgReceiver(BufferedReader inBuffer) {
        in = inBuffer;
    }

    private void safePrint(String str) {
        System.out.println();
        System.out.println(str);
        System.out.print("> ");
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

class ClosingClientHandler extends Thread {
    private final Socket socket;
    private final MsgReceiver msgReceiver;
    private final PrintWriter out;

    public ClosingClientHandler(Socket socket, MsgReceiver msgReceiver, PrintWriter out) {
        this.socket = socket;
        this.msgReceiver = msgReceiver;
        this.out = out;
    }

    public void run() {
        System.out.print("Closing... ");
        if (msgReceiver != null) {
            msgReceiver.stopRunning();
            msgReceiver.interrupt();
        }
        out.println("closed");
        try {
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("closed.");

    }
}

public class Client {
    private static PrintWriter init(String name) throws IOException {
        System.out.println("Client started");
        String hostName = "localhost";
        int portNumber = 12345;
        Socket socket = null;
        MsgReceiver msgReceiver = null;

        try {
            socket = new Socket(hostName, portNumber);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            msgReceiver = new MsgReceiver(in);

            Runtime.getRuntime().addShutdownHook(new ClosingClientHandler(socket, msgReceiver, out));

            out.println(name);
            String receivedMsg = in.readLine();
            System.out.println(receivedMsg);
            if (!receivedMsg.equals("connected")) {
                System.exit(0);
            }

            System.out.println("Type 'close' to exit");
            System.out.print("> ");
            msgReceiver.start();
            return out;
        } catch (IOException e) {
            if (socket != null)
                socket.close();
            System.out.println("Init unsuccessfull");
            throw new IOException();
        }
    }

    public static void main(String[] args) throws IOException {
        try {
            PrintWriter out = init(args != null && args.length != 0 ? args[0] : "" + ProcessHandle.current().pid());
            String msg;
            BufferedReader keyRead = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                if ((msg = keyRead.readLine()) != null) {
                    if (msg.equals("close"))
                        System.exit(0);
                    if (!msg.equals(""))
                        out.println(msg);
                    System.out.print("> ");
                }
            }
        } catch (IOException e) {
            System.exit(0);
        }
    }

}
