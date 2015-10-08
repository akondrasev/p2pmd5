package inc.server.context;

import inc.controller.Command;
import inc.util.Commands;
import inc.util.Util;

import java.util.Map;

public class Resourcereply implements ServerContext {
    @Override
    public String executeCommand(final Map<String, String> request) {
        final Commands commander = new Commands();
        final String toPort = request.get("port");
        final String toIp = request.get("ip");
        final String requestId = request.get("id");

        final String sendip = Util.getCurrentIp();
        final int port = new Commands().getServer().getPort();
        final String currentTask = commander.getMd5Tasks().get(requestId);
        boolean isDoneCurrentTask = commander.getResultsDoneFlags().get(requestId);


        if(!isDoneCurrentTask){
            //TODO logic for md5 here
            new Thread(new Runnable() {
                @Override
                public void run() {
                    {
                        commander.sendRequest("POST", String.format("%s:%s/checkmd5", toIp, toPort),
                                String.format("ip=%s", sendip),
                                String.format("port=%s", port),
                                String.format("id=%s", requestId),
                                String.format("md5=%s", currentTask),
                                String.format("ranges=%s", "[\"ax?o?ssss\", \"aa\", \"ab\", \"ab\"]"),
                                String.format("wildcard=%s", "?"),
                                String.format("symbolrange=%s", "[[3,10], [100,150]]")
                        );
                    }
                }
            } ).start();
        }

        return String.valueOf(ServerContext.OK_CODE);
    }
}
