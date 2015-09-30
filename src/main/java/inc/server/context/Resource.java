package inc.server.context;

import inc.ui.UICmd;
import inc.util.Commands;
import inc.util.Util;

import java.util.Map;

//post request back {"ip": "55.66.77.88", "port": "6788", "id": "asasasas", "resource": 100 }
public class Resource implements ServerCommand {

    @Override
    public String executeCommand(Map<String, Object> request) {
        UICmd commander = new UICmd();

        String toIp = (String) request.get("sendip");
        String toPort = (String) request.get("sendport");
        String requestId = (String) request.get("id");
        String ttl = (String) request.get("ttl");

        int ttlValue = Integer.parseInt(ttl);
        ttlValue--;

        String sendip = Util.getCurrentHostIp();
        int port = new Commands().getServer().getPort();
        commander.doAction(String.format("send post %s:%s/resourcereply sendip=%s port=%s id=%s resource=%s",
                toIp, toPort, sendip, port, requestId, 100));

        if(ttlValue > 1 ){
            for(int i = 0; i < UICmd.knownUrls.length; i++){
                commander.doAction(String.format("send get %s/resource sendip=%s sendport=%d id=%s ttl=%d",
                        UICmd.knownUrls[i], sendip, port, requestId, ttlValue));
            }
        }

        return "all resources done";
    }
}
