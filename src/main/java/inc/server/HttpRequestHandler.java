package inc.server;

import inc.server.context.*;
import inc.util.Util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Map;
import java.util.TreeMap;

public class HttpRequestHandler implements Runnable {

    private Socket socket;
    private Map<String, String> request;

    private Map<String, ServerContext> allowedContexts;
    private Map<String, String> headers;
    private String context = "/";
    private boolean correctReadingStarted;

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

    private String readSocketData(InputStreamReader in) {
        StringBuilder stringBuilder = new StringBuilder();
        char temp;
        try {
            while (in.ready()) {
                if (!correctReadingStarted) {
                    correctReadingStarted = true;
                }
                temp = (char) in.read();
//                System.out.print(String.valueOf(temp));
                stringBuilder.append(temp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!correctReadingStarted) {
//            System.out.println("\n\t***There occurs troubles with reading data");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return readSocketData(in);
        }
        return stringBuilder.toString();
    }

    @Override
    public void run() {
        try (
                InputStreamReader in = new InputStreamReader(socket.getInputStream());
                OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream())
        ) {
            String inputData = readSocketData(in);

            parseRequestStream(inputData);

            out.write(processContext(context));
            out.flush();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseRequestStream(String socketInputText) {
        if (socketInputText.equals("")) {
            return;
        }

        String[] lines = socketInputText.split(Util.CRLF);
        boolean isJson = false;

        for (String current : lines) {
            if (current.contains("GET ")) {
                request = Util.getRequestFromStringQuery(current);
            }

            if (current.contains("POST ") || current.contains("GET ")) {
                context = Util.getRequestContext(current);
                continue;
            }

            if (current.equals("")) {
                isJson = true;
                continue;
            }
            if (!isJson) {
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
        System.out.println(String.format("<--- Processing request '%s':\n %s\n", context, request));
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
