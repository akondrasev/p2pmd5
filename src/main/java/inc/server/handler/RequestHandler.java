package inc.server.handler;

import inc.util.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

public class RequestHandler implements Runnable {
    private static final int OK_CODE = 0;

    private Socket socket;
    private Map<String, String> request;
    private Util utilInstance;

    public RequestHandler(Socket socket) {
        this.socket = socket;
        utilInstance = new Util();
    }

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
                    context = new Util().getContextFromHeaderLine(line);
                }

                if (line.contains("GET ")) {
                    request = utilInstance.getRequestFromUrlQuery(line);
                    break;
                } else if (line.contains("Content-length: ")) {
                    contentLength = Integer.parseInt(line.split("Content-length: ")[1]);
                    break;
                }
            }

            if (contentLength != -1) {

                StringBuilder stringBuilder = new StringBuilder();
                int c;
                for (int i = 0; i < contentLength; i++) {
                    c = in.read();
                    stringBuilder.append((char) c);
                }

                postData = stringBuilder.toString();
            }

            if (postData != null) {
                request = utilInstance.getRequestFromJson(postData);
            }

            processContext(context);

            out.print(OK_CODE);
            out.flush();
        } catch (IOException ignored) {
        }
    }

    private void processContext(String context) {
        System.out.println(String.format("Processing context '%s'", context));
    }
}
