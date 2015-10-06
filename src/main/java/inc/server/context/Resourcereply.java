package inc.server.context;

import inc.util.Commands;
import inc.util.Util;

import java.util.Map;

public class Resourcereply implements ServerContext {
    @Override
    public String executeCommand(Map<String, String> request) {
        Commands commander = new Commands();
        String toPort = request.get("port");
        String toIp = request.get("ip");
        String requestId = request.get("id");

        String sendip = Util.getCurrentIp();
        int port = new Commands().getServer().getPort();

        boolean isDoneCurrentTask = commander.getResultsDoneFlags().get(requestId);
        String currentTask = commander.getMd5Tasks().get(requestId);

        if(!isDoneCurrentTask){
            //TODO logic for md5 here
            new Thread(() -> {
                commander.sendRequest("POST", String.format("%s:%s/checkmd5", toIp, toPort),
                        String.format("ip=%s", sendip),
                        String.format("port=%s", port),
                        String.format("id=%s", requestId),
                        String.format("md5=%s", currentTask),
                        String.format("ranges=%s", "[\"ax?o?ssss\", \"aa\", \"ab\", \"ab\"]"),
                        String.format("wildcard=%s", "?"),
                        String.format("symbolrange=%s", "[[3,10], [100,150]]")
                );
            }).start();
        }

        return String.valueOf(ServerContext.OK_CODE);
    }
}
