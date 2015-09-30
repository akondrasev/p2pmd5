package inc.util;

import inc.server.Server;

import java.io.*;
import java.net.*;

public class Commands {

    private static Server server;
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

    public void stopServer() {
        synchronized (server){
            if(!server.isTerminated()){
                server.stop();
            }
        }
    }

    public void startServer(int port) {
        synchronized (server){
            if(!server.isTerminated()){
                return;
            }
            server.setPort(port);
            server.start();
        }
    }


    public void sendRequest(String requestMethod, String url, String... params) {
        if (url == null || url.equals("")) {
            System.out.println("No url specified");
            return;
        }


        System.out.println(String.format("Sending request to %s", url));

        switch (requestMethod.toUpperCase()) {
            case Util.HTTP_METHOD_GET:
                sendGet(url, params);
                break;
            case Util.HTTP_METHOD_POST:
                sendPost(url, params);
                break;
            default:
                System.out.println(String.format("Unknown request method '%s'", requestMethod));
        }
    }

    private void sendPost(String url, String... params) {
        int port = 80;
        String host = Util.getHostInUrl(url);
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
            return;
        } catch (UnknownHostException e) {
            System.out.println("Unknown host, check spelling");
            return;
        } catch (IOException e) {
            System.out.println("Cannot open socket");
            return;
        }

        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream())
        ) {
            String context = Util.getHostContext(url);
            String postData = Util.parseStringArrayToJson(params);
            out.write("POST " + context + " HTTP/1.1" + Util.CRLF);
            out.write("Host: www." + url + Util.CRLF);
            out.write("Content-length: " + postData.length() + Util.CRLF);
            out.write(Util.CRLF);
            out.write(postData);
            out.write(Util.CRLF);

            out.flush();

            char responseCode = (char) in.read();
            System.out.println(responseCode);

            if(responseCode == '0'){
                System.out.println("OK");
            } else {
                System.out.println("ne OK");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendGet(String url, String... params) {

        int port = 80;
        String host = Util.getHostInUrl(url);
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
            return;
        } catch (UnknownHostException e) {
            System.out.println("Unknown host, check spelling");
            return;
        } catch (IOException e) {
            System.out.println("Cannot open socket");
            return;
        }

        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream())
        ) {
            String context = Util.getHostContext(url);
            String queryString = Util.parseArrayToGetParams(params);

            if(queryString != null){
                context = context + "?" + queryString;
            }

            out.write("GET " + context + " HTTP/1.1" + Util.CRLF);
            out.write("Host: www." + url + Util.CRLF);
            out.write(Util.CRLF);
            out.flush();

            char responseCode = (char) in.read();
            System.out.println(responseCode);

            if(responseCode == '0'){
                System.out.println("OK");
            } else {
                System.out.println("ne OK");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
