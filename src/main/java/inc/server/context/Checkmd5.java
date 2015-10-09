package inc.server.context;

import inc.dto.CrackResult;
import inc.util.Commands;
import inc.util.Util;

import java.util.Map;

// {"ip": "55.66.77.88", "port": "6788", "id": "siinonid", "md5": "siinonmd5string", "ranges": ["ax?o?ssss","aa","ab","ac","ad"], "wildcard": "?", "symbolrange": [[3,10],[100,150]]}
public class Checkmd5 implements ServerContext {
    @Override
    public String executeCommand(Map<String, String> request) {
        final Commands commander = new Commands();

        final String toIp = request.get("ip");
        final String toPort = request.get("port");
        final String md5 = request.get("md5");
        String requestId = request.get("id");
        final String[] ranges = Util.getStringTemplatesFromRanges(request.get("ranges"));
        final String wildcard = request.get("wildcard");//symbol
        final int[][] symbolrange = Util.getSymbolrange(request.get("symbolrange"));

        final String finalRequestId = requestId;
        new Thread(new ThreadGroup("Cracker"), new Runnable() {
            @Override
            public void run() {
                commander.setWorking(true);
                CrackResult result = Util.checkMd5(md5, wildcard, ranges, symbolrange);

                commander.setWorking(false);
                commander.sendRequest("POST", String.format("%s:%s/answermd5", toIp, toPort),
                        String.format("port=%s", commander.getServer().getPort()),
                        String.format("ip=%s", Util.getCurrentIp()),
                        String.format("id=%s", finalRequestId),
                        String.format("md5=%s", md5),
                        String.format("result=%s", result.getResultCode()),
                        String.format("resultstring=%s", result.getResultstring())
                );
            }
        }, "crack process", 1024).start();

        return String.valueOf(ServerContext.OK_CODE);
    }
}
