package inc.server.context;

import inc.ui.UICmd;
import inc.util.Commands;
import inc.util.Util;

import java.util.Map;

//TODO crack?md5=asdasdasddasdasd - inital request - sync
public class Crack implements ServerContext {
    @Override
    public String executeCommand(Map<String, String> request) {
        String md5 = request.get("md5");//todo make global to send for crack

        Commands commander = new Commands();
        commander.setDone(false);
        String sendip = Util.getCurrentIp();
        int sendport = new Commands().getServer().getPort();
        String requestId = "ffff";
        int ttlValue = commander.getTtl();

        commander.sendRequest("GET", String.format("%s:%s/resource", sendip, sendport),
                String.format("sendip=%s", sendip),
                String.format("sendport=%s", sendport),
                String.format("id=%s", requestId),
                String.format("ttl=%s", ttlValue),
                String.format("noask=%s", String.format("%s_%s", sendip, sendport))
        );

        int seconds = 0;
        while (!commander.isDone()) {
            if (seconds > 15) {
                return "timeout";
            }
            try {
                Thread.sleep(1000L);
                seconds++;
            } catch (InterruptedException ignored) {
            }
        }

        return "cracked! " + commander.getResult();
    }
}
