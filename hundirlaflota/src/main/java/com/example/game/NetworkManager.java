package com.example.game;

import java.io.*;
import java.net.*;

public class NetworkManager {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private boolean isServer;

    public NetworkManager(boolean isServer, String ip, int port) throws IOException {
        this.isServer = isServer;
        if (isServer) {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                socket = serverSocket.accept();
            }
        } else {
            socket = new Socket(ip, port);
        }

        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    public void send(String msg) {
        out.println(msg);
    }

    public String receive() throws IOException {
        return in.readLine();
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException ignored) {
            // is empty
        }
    }

    public boolean isServer() {
        return isServer;
    }
}
