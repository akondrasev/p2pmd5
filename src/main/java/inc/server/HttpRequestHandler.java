package inc.server;

import inc.server.context.*;
import inc.util.Util;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.TreeMap;

public class HttpRequestHandler implements Runnable {

    private static final String OK_CODE = "0";
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

            System.out.println(String.format("Processing request '%s': %s", context, request));
            if (context.equals("/crack")) {
                out.print(new Crack().executeCommand(request));
            } else {
                processContext(context);
                out.print(OK_CODE);
            }
            out.flush();
        } catch (IOException ignored) {
        }
    }

    private void processContext(String context) {
        if (context == null) {
            return;
        }

        if (request == null) {
            return;
        }

        ServerContext serverContext = allowedContexts.get(context);


        if (serverContext == null) {
            System.out.println(String.format("\nUnknown request context '%s'\n", context));
            return;
        }

        serverContext.executeCommand(request);
    }
}
