package inc.server;

import inc.server.handler.RequestHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable, ServerInterface {
    private Thread serverThread;
    private boolean isRunning;
    private ServerSocket serverSocket;

    public void run() {
        while (isRunning) {
            try {
                Socket socket = serverSocket.accept();
                new Thread(new RequestHandler(socket)).start();
            } catch (IOException ignored) {
            }
        }

        System.out.println("Server has stopped working");
    }

    public void stop() {
        if (!isRunning) {
            return;
        }

        isRunning = false;
        try {
            serverSocket.close();
        } catch (IOException ignored) {
        }
    }

    public void start(int port) {
        if (isRunning) {
            return;
        }
        isRunning = true;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException ignored) {
        }
        serverThread = new Thread(this);
        serverThread.start();
    }
}
