package inc.server;

import inc.server.context.*;
import inc.util.Util;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.TreeMap;

public class HttpRequestHandler implements Runnable {

    private Socket socket;
    private Map<String, String> request;

    private Map<String, ServerContext> allowedContexts;

    public HttpRequestHandler(Socket socket) {
        this.socket = socket;

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
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream())
        ) {
            String line;
            String postData = null;
            int contentLength = -1;
            String context = "/";

            while (true) {//GET, content-length check and break
                line = in.readLine();

                if (line.contains("POST ") || line.contains("GET ")) {
                    context = Util.getRequestContext(line);
                }

                if (line.contains("GET ")) {
                    request = Util.getRequestFromStringQuery(line);
                    break;
                } else if (line.contains("Content-length: ")) {
                    contentLength = Integer.parseInt(line.split("Content-length: ")[1]);
                    break;
                }
            }

            if (contentLength != -1) {

                StringBuilder stringBuilder = new StringBuilder();
                int c;
                for (int i = 0; i < contentLength + 2; i++) {
                    c = in.read();
                    stringBuilder.append((char) c);
                }

                postData = stringBuilder.toString();
            }

            if (postData != null) {
                request = Util.getRequestFromJson(postData);
            }

            out.print(processContext(context));
            out.flush();
        } catch (IOException ignored) {
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
