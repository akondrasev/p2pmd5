package inc.server.context;

import inc.ui.UICmd;
import inc.util.Commands;
import inc.util.Util;

import java.util.Map;

//TODO get appropriate task md5
public class Resourcereply implements ServerCommand {
    @Override
    public String executeCommand(Map<String, Object> request) {
        Commands commander = new Commands();
        String toPort = (String) request.get("port");
        String toIp = (String) request.get("ip");
        String requestId = (String) request.get("id");

        String sendip = Util.getCurrentHostIp();
        int port = new Commands().getServer().getPort();

        commander.sendRequest("POST", String.format("%s:%s/checkmd5", toIp, toPort),
                String.format("ip=%s", sendip),
                String.format("port=%s", port),
                String.format("id=%s", requestId),
                String.format("md5=%s", "hash"),
                String.format("ranges=%s", "[\"ax?o?ssss\", \"aa\", \"ab\", \"ab\"]"),
                String.format("wildcard=%s", "?"),
                String.format("symbolrange=%s", "[[3,10], [100,150]]")
        );
        return "checkmd5 sent";
    }
}
