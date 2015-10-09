package inc.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {
    private Thread serverThread;
    private boolean isRunning;
    private ServerSocket serverSocket;
    private int port;

    public boolean isRunning() {
        return isRunning;
    }

    public void run() {
        ThreadGroup threadGroup = new ThreadGroup("requestHandlerGroup");
        while (isRunning) {
            try {
                Socket socket = serverSocket.accept();
                new Thread(threadGroup, new HttpRequestHandler(socket), "handler", 2048).start();
            } catch (IOException ignored) {
            }
        }
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
        this.port = port;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException ignored) {
        }

        serverThread = new Thread(this);
        serverThread.start();
    }

    public int getPort() {
        return port;
    }
}
