package inc.util;

import inc.dto.Answer;
import inc.server.Server;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Commands {

    private static final Server server;
    public static String[] computers;
    private static int reqCounter = 0;
    private static boolean working = false;
    private static int ttl = 0;
    private static int timeout = 20;
    private static final ConcurrentHashMap<String, String> results = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Boolean> resultsDoneFlags = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, String> md5Tasks = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, List<Answer>> answersMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, String> lastTasksMap = new ConcurrentHashMap<>();

    static {
        server = new Server();
    }

    public Commands() {
    }

    /*
    synced methods
     */
    public ConcurrentHashMap<String, String> getLastTasksMap(){
        synchronized (lastTasksMap){
            return lastTasksMap;
        }
    }
    public ConcurrentHashMap<String, List<Answer>> getAnswersMap(){
        synchronized (answersMap){
            return answersMap;
        }
    }
    public ConcurrentHashMap<String, String> getMd5Tasks() {
        synchronized (md5Tasks) {
            return md5Tasks;
        }
    }

    public ConcurrentHashMap<String, Boolean> getResultsDoneFlags() {
        synchronized (resultsDoneFlags) {
            return resultsDoneFlags;
        }
    }

    public ConcurrentHashMap<String, String> getResultsMap() {
        synchronized (results) {
            return results;
        }
    }

    public int incrementReqCount() {
        synchronized (Commands.class) {
            return reqCounter++;
        }
    }

    public int getTimeout() {
        synchronized (Commands.class) {
            return timeout;
        }
    }

    public void setTimeout(int timeout) {
        synchronized (Commands.class) {
            Commands.timeout = timeout;
        }
    }

    public int getTtl() {
        synchronized (Commands.class) {
            return ttl;
        }
    }

    public void setTtl(int ttl) {
        synchronized (Commands.class) {
            Commands.ttl = ttl;
        }
    }

    public boolean isWorking() {
        synchronized (Commands.class) {
            return working;
        }
    }

    public void setWorking(boolean isWorking) {
        synchronized (Commands.class) {
            working = isWorking;
        }
    }

    public Server getServer() {
        synchronized (server) {
            return server;
        }
    }

    public String stopServer() {
        synchronized (server) {
            if (server.isRunning()) {
                server.stop();
                return "Server stopped successfully";
            }

            return "Server is not running yet";
        }
    }
    //end of synced methods

    public String startServer(int port) {
        synchronized (server) {
            if (server.isRunning()) {
                return "Server is already started";
            }
            server.start(port);
            return "Server started listening port " + port;
        }
    }

    public String readConfigFromFile(String fileName) {
        String machinesJson = Util.readFile(fileName);
        if (machinesJson == null) {
            return "file not found";
        }
        computers = Util.getKnownComputersFromJson(machinesJson);
        return "Loaded IPs are: " + Arrays.toString(computers);
    }

    public String sendRequest(String requestMethod, String url, String... params) {
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
            System.out.println(String.format("*** Cannot connect to host %s:%s", host, port));
            return String.format("Cannot connect to '%s:%d'", host, port);
        } catch (UnknownHostException e) {
            System.out.println("*** Unknown host, check spelling");
            return String.format("Unknown host '%s', check if host is connected to the network", host);
        } catch (IOException e) {
            System.out.println("*** Cannot open socket");
            return "Cannot open socket connection";
        }

        try (
                InputStreamReader in = new InputStreamReader(socket.getInputStream());
                OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream())
        ) {

            switch (requestMethod.toUpperCase()) {
                case Util.HTTP_METHOD_GET:
                    return sendGet(url, in, out, params);
                case Util.HTTP_METHOD_POST:
                    return sendPost(url, in, out, params);
                default:
                    return String.format("No such HTTP method '%s', cannot send request", requestMethod);
            }

        } catch (IOException e) {
            return "connection troubles. cannot connect to url " + url;
        }
    }

    private String sendPost(String url, InputStreamReader in, OutputStreamWriter out, String... params) throws IOException {
        System.out.println(String.format("---> Sending POST request to %s", url));
        String context = Util.getRequestContext(url);
        JSONObject postData = Util.generateJson(params);
        System.out.println(String.format("---> post data:\n\t %s\n", postData));

        out.write("POST " + context + " HTTP/1.1" + Util.CRLF);
        out.write("Host: " + Util.getHostFromUrl(url) + Util.CRLF);
        out.write("Content-Length: " + postData.toString().getBytes().length + Util.CRLF);
        out.write(Util.CRLF);
        out.write(postData.toString() + Util.CRLF);

        out.flush();
        out.close();

        char response = (char) in.read();
        System.out.println(response);

        if (response == '0') {
            System.out.println("<--- OK");
        } else {
            System.out.println("<--- ne OK");
        }
        return String.valueOf(response);
    }

    private String sendGet(String url, InputStreamReader in, OutputStreamWriter out, String... params) throws IOException {
        System.out.println(String.format("===> Sending GET request to %s", url));
        String context = Util.getRequestContext(url);
        String queryString = Util.generateStringQuery(params);
        System.out.println(String.format("===> query string:\n\t %s\n", queryString));

        if (queryString != null) {
            context = context + "?" + queryString;
        }

        out.write("GET " + context + " HTTP/1.1" + Util.CRLF);
        out.write("Host: " + url + Util.CRLF);
        out.write(Util.CRLF);
        out.flush();
        out.close();

        char response = (char) in.read();
        System.out.println(response);

        if (response == '0') {
            System.out.println("<=== OK");
        } else {
            System.out.println("<=== ne OK");
        }

        return String.valueOf(response);
    }
}
