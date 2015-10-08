package inc.server;

import inc.server.context.*;
import inc.util.Util;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class HttpRequestHandler implements Runnable {

    private Socket socket;
    private Map<String, String> request;

    private Map<String, ServerContext> allowedContexts;
    private Map<String, String> headers;
    private String context = "/";

    public HttpRequestHandler(Socket socket) {
        this.socket = socket;

        headers = new TreeMap<>();
        allowedContexts = new TreeMap<>();
        allowedContexts.put("/resource", new Resource());
        allowedContexts.put("/resourcereply", new Resourcereply());
        allowedContexts.put("/checkmd5", new Checkmd5());
        allowedContexts.put("/answermd5", new Answermd5());
        allowedContexts.put("/crack", new Crack());
    }

    @Override
    public void run() {
        try (
                InputStreamReader in = new InputStreamReader(socket.getInputStream());
                OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream())
        ) {
            StringBuilder stringBuilder = new StringBuilder();

            char temp;
            boolean startedReading = false;
            Thread.sleep(50L);// otherwise in.ready randomly says false and cannot read smth
            while (in.ready()) {
                if(!startedReading){
                    startedReading = true;
                }
                temp = (char) in.read();
                System.out.print(String.valueOf(temp));
                stringBuilder.append(temp);
            }

            if(!startedReading){
                System.out.println("There occures troubles with reading data");
                out.write(ServerContext.CANNOT_READ_FROM_SOCKET);
                out.flush();
                return;
            }

            parseRequestStream(stringBuilder.toString());

            out.write(processContext(context));
            out.flush();
            socket.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void parseRequestStream(String socketInputText){
        if(socketInputText.equals("")){
            return;
        }

        String[] lines = socketInputText.split(Util.CRLF);
        boolean isJson = false;

        for(int i = 0; i < lines.length; i++){
            String current = lines[i];

            if(current.contains("GET ")){
                request = Util.getRequestFromStringQuery(current);
            }

            if(current.contains("POST ") || current.contains("GET ")){
                context = Util.getRequestContext(current);
                continue;
            }

            if (current.equals("")){
                isJson = true;
                continue;
            }
            if(!isJson){
                String[] keyValue_pair = current.split(": ");
                String key = keyValue_pair[0];
                String value = keyValue_pair[1];
                headers.put(key, value);
            } else {
                request = Util.getRequestFromJson(current);
            }
        }
    }

    private String processContext(String context) {
        System.out.println(String.format("Processing request '%s': %s", context, request));
        if (context == null) {
            return String.valueOf(ServerContext.UNKNOWN_CONTEXT_CODE);
        }

        if (request == null) {
            return String.valueOf(ServerContext.WRONG_REQUEST_PARAMS_CODE);
        }

        ServerContext serverContext = allowedContexts.get(context);

        if (serverContext == null) {
            System.out.println(String.format("\nUnknown request context '%s'\n", context));
            return String.valueOf(ServerContext.UNKNOWN_CONTEXT_CODE);
        }

        return serverContext.executeCommand(request);
    }
}
