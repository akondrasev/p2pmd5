package inc.server.context;

import inc.util.Commands;
import inc.util.Util;

import java.util.Map;

public class Resourcereply implements ServerContext {

    private final Commands commander = new Commands();

    private String getNextTask(String requestId) {
        String last = commander.getLastTasksMap().get(requestId);
        String currentTask = " ";
        if (last != null) {
            if (last.length() == 1) {
                currentTask = "  ";
            } else if (last.length() == 2) {
                currentTask = "   ";
            } else if (last.length() == 3) {
                currentTask = String.valueOf(new char[]{(char) 33, ' ', ' ', ' '});
            } else if (last.length() == 4) {
                char firstChar = last.charAt(0);
                int asciiNumber = (int) firstChar;
                char currentChar = (char) ++asciiNumber;
                currentTask = String.valueOf(new char[]{currentChar, ' ', ' ', ' '});
            }
        }

        commander.getLastTasksMap().put(requestId, currentTask);
        return "[\"" + currentTask + "\"]";
    }

    @Override
    public String executeCommand(final Map<String, String> request) {

        final String toPort = request.get("port");
        final String toIp = request.get("ip");
        final String requestId = request.get("id");
        final int resource = Integer.parseInt(request.get("resource"));

        final String sendip = Util.getCurrentIp();
        final int port = new Commands().getServer().getPort();
        final String currentTask = commander.getMd5Tasks().get(requestId);
        boolean isDoneCurrentTask = commander.getResultsDoneFlags().get(requestId);


        if (!isDoneCurrentTask && resource > 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    commander.sendRequest("POST", String.format("%s:%s/checkmd5", toIp, toPort),
                            String.format("ip=%s", sendip),
                            String.format("port=%s", port),
                            String.format("id=%s", requestId),
                            String.format("md5=%s", currentTask),
                            String.format("ranges=%s", getNextTask(requestId)),
                            String.format("wildcard=%s", " "),
                            String.format("symbolrange=%s", "[[33, 126]]")
                    );
                }
            }).start();
        }

        return String.valueOf(ServerContext.OK_CODE);
    }
}
