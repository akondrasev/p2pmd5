package inc.server.context;

import inc.util.Commands;
import inc.util.Util;

import java.util.Map;

public class Resourcereply implements ServerContext {

    private final Commands commander = new Commands();

    private String getNextTask(String requestId, String host) {
        String task = Util.getNextTask(requestId);
        commander.getTasksForComputers().put(requestId+host, task);
        return task;
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
                    String host = String.format("%s:%s", toIp, toPort);
                    String ranges = getNextTask(requestId, host);
//                    System.out.println(String.format("Start cracking '%s'", ranges));
                    commander.sendRequest("POST", String.format("%s/checkmd5", host),
                            String.format("ip=%s", sendip),
                            String.format("port=%s", port),
                            String.format("id=%s", requestId),
                            String.format("md5=%s", currentTask),
                            String.format("ranges=%s", ranges),
                            String.format("wildcard=%s", " "),
                            String.format("symbolrange=%s", "[[33, 126]]")
                    );
                }
            }).start();
        }

        return String.valueOf(ServerContext.OK_CODE);
    }
}
