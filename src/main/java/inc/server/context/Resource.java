package inc.server.context;

import inc.ui.UICmd;
import inc.util.Commands;
import inc.util.Util;

import java.util.Map;

//post request back {"ip": "55.66.77.88", "port": "6788", "id": "asasasas", "resource": 100 }
public class Resource implements ServerCommand {

    @Override
    public String executeCommand(Map<String, String> request) {
        Commands commander = new Commands();

        String toIp = request.get("sendip");
        String toPort = request.get("sendport");
        String requestId = request.get("id");
        String ttl = request.get("ttl");

        int ttlValue = 0;
        if(!(ttl == null || ttl.equals(""))){
            ttlValue = Integer.parseInt(ttl);
            ttlValue--;
        }


        String sendip = Util.getCurrentHostIp();
        int port = commander.getServer().getPort();
        commander.sendRequest(
                "POST", String.format("%s:%s/resourcereply", toIp, toPort),
                String.format("ip=%s", sendip),
                String.format("port=%s", port),
                String.format("id=%s", requestId),
                String.format("resource=%s", 100)
        );

        if (ttlValue > 1) {
            for (int i = 0; i < UICmd.knownUrls.length; i++) {
                commander.sendRequest(
                        "GET",
                        String.format("%s/resource", UICmd.knownUrls[i]),
                        String.format("sendip=%s", sendip),
                        String.format("sendport=%s", port),
                        String.format("ttl=%s", ttlValue),
                        String.format("id=%s", requestId)
                );
            }
        }

        return "all resources done";
    }
}
