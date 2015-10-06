package inc.server.context;

import inc.util.Commands;
import inc.util.Util;

import java.util.Map;

public class Crack implements ServerContext {
    @Override
    public String executeCommand(Map<String, String> request) {
        String md5 = request.get("md5");

        Commands commander = new Commands();

        String sendip = Util.getCurrentIp();
        int sendport = new Commands().getServer().getPort();
        String requestId = String.valueOf(commander.incrementReqCount()+"ID");
        commander.getResultsDoneFlags().put(requestId, false);
        int ttlValue = commander.getTtl();

        commander.getResultsMap().put(requestId, null);
        commander.getMd5Tasks().put(requestId, md5);

        new Thread(() -> {
            commander.sendRequest("GET", String.format("%s:%s/resource", sendip, sendport),
                    String.format("sendip=%s", sendip),
                    String.format("sendport=%s", sendport),
                    String.format("id=%s", requestId),
                    String.format("ttl=%s", ttlValue),
                    String.format("noask=%s", String.format("%s_%s", sendip, sendport))
            );
        }).start();


        for (int i = 0; !commander.getResultsDoneFlags().get(requestId); i++){
            if (i > commander.getTimeout()) {
                return String.format("Timeout %ss", commander.getTimeout());
            }
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException ignored) {}
        }
        return String.format("Result for request id '%s': %s = %s", requestId, commander.getResultsMap().get(requestId), md5);
    }
}
