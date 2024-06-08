package org.hw;

public class Main {
    public static void main(String[] args) {
        ZooApp app = new ZooApp("127.0.0.1:2182", 3000, "mspaint");
        app.run();
    }
}