package inc.server.context;

import inc.ui.UICmd;
import inc.util.Commands;
import inc.util.Util;

import java.util.Map;

//TODO get appropriate task md5
public class Resourcereply implements ServerCommand {
    @Override
    public String executeCommand(Map<String, Object> request) {
        String resourceAvailable = (String) request.get("resource");

        if(Integer.parseInt(resourceAvailable) > 0){
            String toPort = (String) request.get("port");
            String toIp = (String) request.get("sendip");
            String requestId = (String) request.get("id");

            String sendip = Util.getCurrentHostIp();
            int port = new Commands().getServer().getPort();
            String cmd = String.format("send post %s:%s/checkmd5 sendip=%s port=%s id=%s md5=%s ranges=%s wildcard=%s symbolrange=%s",
                    toIp, toPort, sendip, port, requestId, "md5", "[\"ax?o?ssss\", \"aa\", \"ab\", \"ab\"]", "?", "[[3,10], [100,150]]");

            new UICmd().doAction(cmd);
            return "checkmd5 sent";
        }
        return "too low resource";
    }
}
