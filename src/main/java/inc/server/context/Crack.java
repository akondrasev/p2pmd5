package inc.server.context;

import inc.ui.UICmd;
import inc.util.Commands;
import inc.util.Util;

import java.util.Map;

//TODO crack?md5=asdasdasddasdasd - inital request - sync
public class Crack implements ServerCommand {
    @Override
    public String executeCommand(Map<String, String> request) {
        String md5 = request.get("md5");

        Commands commander = new Commands();
        String sendip = Util.getCurrentIp();
        int sendport = new Commands().getServer().getPort();
        String requestId = "ffff";
        int ttlValue = 3;

        for(int i = 0; i < UICmd.knownUrls.length; i++){
            commander.sendRequest("GET", String.format("%s/resource", UICmd.knownUrls[i]),
                    String.format("sendip=%s", sendip),
                    String.format("sendport=%s", sendport),
                    String.format("id=%s", requestId),
                    String.format("ttl=%s", ttlValue)
            );
        }

        //TODO sleep until answer done

        return "cracked! " + md5;
    }
}
