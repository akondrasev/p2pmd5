package inc.server.context;

import inc.controller.Command;
import inc.ui.UICmd;
import inc.util.Commands;
import inc.util.Util;

import java.util.Map;

//TODO crack?md5=asdasdasddasdasd - inital request - sync
public class Crack implements ServerCommand {
    @Override
    public String executeCommand(Map<String, Object> request) {
        String md5 = (String) request.get("md5");

        UICmd commander = new UICmd();
        String sendip = Util.getCurrentHostIp();
        int sendport = new Commands().getServer().getPort();
        String requestId = "ffff";
        int ttlValue = 3;

        for(int i = 0; i < UICmd.knownUrls.length; i++){
            commander.doAction(String.format("send get %s/resource sendip=%s sendport=%d id=%s ttl=%d",
                    UICmd.knownUrls[i], sendip, sendport, requestId, ttlValue));
        }

        return "cracked! " + md5;
    }
}
