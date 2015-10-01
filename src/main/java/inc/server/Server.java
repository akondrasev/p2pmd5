package inc.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {

    private int port;
    private boolean isTerminated = true;
    private Thread thisThread;
    private ServerSocket serverSocket;

    public Server() {
    }

    public boolean isTerminated() {
        return isTerminated;
    }

    public int getPort() {
        return port;
    }

    public void stop() {
        isTerminated = true;

        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException ignored) {
            }
        }
        System.out.println("Server has been stopped");
    }

    public void start(int port) {
        isTerminated = false;
        this.port = port;

        thisThread = new Thread(this, "Server thread");
        thisThread.start();
        System.out.println(String.format("Server started at port '%d'", this.port));
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("Cannot start server on port " + port);
            return;
        }

        while (!isTerminated) {

            Socket socket;

            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                System.out.println("Cannot accept socket.");
                continue;
            }

            HttpRequestHandler httpRequestHandler = new HttpRequestHandler(socket);
            Thread thread = new Thread(httpRequestHandler);
            thread.start();
        }

        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
