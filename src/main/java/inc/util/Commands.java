package inc.util;

import inc.server.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Commands {

    private static final Server server;
    static {
        server = new Server();
    }

    public Commands() {
    }

    public Server getServer() {
        synchronized (server){
            return server;
        }
    }

    public String stopServer() {
        synchronized (server){
            if(server.isRunning()){
                server.stop();
                return "Server stopped successfully";
            }

            return "Server is not running yet";
        }
    }

    public String startServer(int port) {
        synchronized (server){
            if(server.isRunning()){
                return "Server is already started";
            }
            server.start(port);
            return "Server started listening port " + port;
        }
    }


    public String sendRequest(String requestMethod, String url, String... params) {
        System.out.println(String.format("Sending request to %s", url));

        switch (requestMethod.toUpperCase()) {
            case Util.HTTP_METHOD_GET:
                return sendGet(url, params);
            case Util.HTTP_METHOD_POST:
                return sendPost(url, params);
            default:
                return String.format("No such HTTP method '%s', cannot send request", requestMethod);
        }
    }

    private String sendPost(String url, String... params) {
        int port = 80;
        String host = Util.getHostFromUrl(url);
        String[] address = host.split(":");

        if (address.length > 1) {
            port = Integer.parseInt(address[1]);
            host = address[0];
        }

        Socket socket;
        try {
            socket = new Socket(InetAddress.getByName(host), port);
        } catch (ConnectException e) {
            System.out.println("Cannot connect to host " +  e.getMessage());
            return String.format("Cannot connect to '%s:%d'", host, port);
        } catch (UnknownHostException e) {
            System.out.println("Unknown host, check spelling");
            return String.format("Unknown host '%s', check if host is connected to the network", host);
        } catch (IOException e) {
            System.out.println("Cannot open socket");
            return "Cannot open socket connection";
        }

        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream())
        ) {
            String context = Util.getRequestContext(url);
            String postData = Util.parseStringArrayToJson(params);
            System.out.println(String.format("post data: %s", postData));

            out.write("POST " + context + " HTTP/1.1" + Util.CRLF);
            out.write("Host: www." + url + Util.CRLF);
            out.write("Content-length: " + postData.length() + Util.CRLF);
            out.write(Util.CRLF);
            out.write(postData);
            out.write(Util.CRLF);

            out.flush();

            char response = (char) in.read();
            System.out.println(response);

            if (response == '0') {
                System.out.println("OK");
            } else {
                System.out.println("ne OK");
            }
            return String.format("Response from '%s:%d': %s", host, port, response);
        } catch (IOException e) {
            return String.format("Something went wrong with reading/writing to socket '%s'", socket.getLocalAddress());
        }
    }

    private String sendGet(String url, String... params) {

        int port = 80;
        String host = Util.getHostFromUrl(url);
        String[] address = host.split(":");

        if (address.length > 1) {
            port = Integer.parseInt(address[1]);
            host = address[0];
        }

        Socket socket;
        try {
            socket = new Socket(InetAddress.getByName(host), port);
        } catch (ConnectException e) {
            System.out.println("Cannot connect to host " +  e.getMessage());
            return String.format("Cannot connect to '%s:%d'", host, port);
        } catch (UnknownHostException e) {
            System.out.println("Unknown host, check spelling");
            return String.format("Unknown host '%s', check if host is connected to the network", host);
        } catch (IOException e) {
            System.out.println("Cannot open socket");
            return "Cannot open socket connection";
        }

        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream())
        ) {
            String context = Util.getRequestContext(url);
            String queryString = Util.parseArrayToGetParams(params);
            System.out.println(String.format("query string: %s", queryString));

            if(queryString != null){
                context = context + "?" + queryString;
            }

            out.write("GET " + context + " HTTP/1.1" + Util.CRLF);
            out.write("Host: www." + url + Util.CRLF);
            out.write(Util.CRLF);
            out.flush();

            char response = (char) in.read();
            System.out.println(response);

            if (response == '0') {
                System.out.println("OK");
            } else {
                System.out.println("ne OK");
            }

            return String.format("Response from '%s:%d': %s", host, port, response);
        } catch (IOException e) {
            return String.format("Something went wrong with reading/writing to socket '%s'", socket.getLocalAddress());
        }
    }
}
