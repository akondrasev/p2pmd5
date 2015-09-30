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

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void stop() {
        isTerminated = true;

        if (thisThread != null) {
            thisThread.interrupt();
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("Server has been stopped");
    }

    public void start() {
        isTerminated = false;

        thisThread = new Thread(this, "Server thread");
        thisThread.start();
        System.out.println(String.format("Server started at port '%d'", port));
    }

    private void runServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
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


    @Override
    public void run() {
        runServer(port);
    }

    public void setPort(int port) {
        this.port = port;
    }
}
