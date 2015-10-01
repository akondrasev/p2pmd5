package inc.server;

import inc.server.context.*;
import inc.util.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Map;
import java.util.TreeMap;

public class HttpRequestHandler implements Runnable {

    private static final String ZERO = "0";
    private Socket socket;
    private String command;
    private Map<String, String> requestParams;

    private Map<String, ServerCommand> allowedContexts;

    public HttpRequestHandler(Socket socket) {
        this.socket = socket;

        allowedContexts = new TreeMap<>();
        allowedContexts.put("/resource", new Resource());
        allowedContexts.put("/resourcereply", new Resourcereply());
        allowedContexts.put("/checkmd5", new Checkmd5());
        allowedContexts.put("/answermd5", new Answermd5());
    }

    private String processRequest(BufferedReader in) {
        StringBuilder headers = new StringBuilder();
        String headerLine;
        int contentLength = -1;
        try {
            while (true) {
                headerLine = in.readLine();
                if (headerLine == null){
                    break;
                }

                headers.append(headerLine)
                        .append(Util.CRLF);

                if(headerLine.contains(Util.HTTP_METHOD_GET)){
                    String stringQuery = headerLine.split("GET ")[1].split(" ")[0];
                    command = Util.getHostContext(stringQuery);
                    requestParams = Util.parseGetRequest(stringQuery);
                } else if (headerLine.contains(Util.HTTP_METHOD_POST)){
                    String stringQuery = headerLine.split("POST ")[1].split(" ")[0];
                    command = Util.getHostContext(stringQuery);
                }

                if (headerLine.contains("Content-length: ")) {
                    contentLength = Integer.parseInt(headerLine.split("Content-length: ")[1]);
                }

                if (headerLine.equals("")) {
                    break;
                }
            }

            if (contentLength != -1) {

                StringBuilder postData = new StringBuilder();
                int c;
                for (int i = 0; i < contentLength; i++) {
                    c = in.read();
                    headers.append((char) c);
                    postData.append((char) c);
                }
                requestParams = Util.getRequestParamsFromJson(postData.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return headers.toString();
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream())
        ) {
            String request = processRequest(in);
            System.out.println(String.format("\n\tIncome Request:\n%s\n", request));

            String cracked = null;
            if(command.equals("/crack")){
                cracked = new Crack().executeCommand(requestParams);
            }

            if (cracked != null){
                out.write(cracked);
            } else {
                out.write(ZERO);
            }


            out.flush();
            processCommand(command);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void processCommand(String command) {
        if(command == null){
            return;
        }

        if(requestParams == null){
            return;
        }

        ServerCommand serverCommand = allowedContexts.get(command);


        if(serverCommand == null){
            System.out.println(String.format("\nUnknown request context '%s'\n", command));
            return;
        }

        serverCommand.executeCommand(requestParams);
    }
}
