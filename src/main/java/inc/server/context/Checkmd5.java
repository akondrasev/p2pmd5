package inc.server.context;

import inc.util.Commands;
import inc.util.Util;

import java.util.Map;

//TODO data here
// {"ip": "55.66.77.88", "port": "6788", "id": "siinonid", "md5": "siinonmd5string", "ranges": ["ax?o?ssss","aa","ab","ac","ad"], "wildcard": "?", "symbolrange": [[3,10],[100,150]]}
public class Checkmd5 implements ServerContext {
    @Override
    public String executeCommand(Map<String, String> request) {
        Commands commander = new Commands();
        commander.setWorking(true);

        String toIp = request.get("ip");
        String toPort = request.get("port");

        new Thread(() -> {
            try {
                Thread.sleep(7000L);//TODO working bruteforce here
            } catch (InterruptedException ignored) {
            }
            commander.setWorking(false);
            commander.sendRequest("POST", String.format("%s:%s/answermd5", toIp, toPort),
                    String.format("port=%s", commander.getServer().getPort()),
                    String.format("ip=%s", Util.getCurrentIp()),
                    String.format("id=%s", request.get("id")),
                    String.format("md5=%s", request.get("md5")),
                    String.format("result=%s", 0),
                    String.format("resultstring=%s", "resultstring on selline")
            );
        }).start();

        return String.valueOf(ServerContext.OK_CODE);
    }
}
