package inc.server.context;

import inc.dto.CrackResult;
import inc.util.Commands;
import inc.util.Util;

import java.util.*;

public class Checkmd5 implements ServerContext {
    @Override
    public String executeCommand(Map<String, String> request) {
        final Commands commander = new Commands();

        final String toIp = request.get("ip");
        final String toPort = request.get("port");
        final String md5 = request.get("md5");
        final String requestId = request.get("id");
        final String[] ranges = Util.getStringTemplatesFromRanges(request.get("ranges"));
        final String wildcard = request.get("wildcard");//symbol
        final int[][] symbolrange = Util.getSymbolrange(request.get("symbolrange"));

        final Thread crackThread = new Thread(new ThreadGroup("Cracker"), new Runnable() {

            @Override
            public void run() {
                commander.setWorking(true);
                CrackResult result = Util.checkMd5(md5, wildcard, ranges, symbolrange);

                if(!commander.isWorking()){
                    return;
                }
                commander.setWorking(false);
                commander.sendRequest("POST", String.format("%s:%s/answermd5", toIp, toPort),
                        String.format("port=%s", commander.getServer().getPort()),
                        String.format("ip=%s", Util.getCurrentIp()),
                        String.format("id=%s", requestId),
                        String.format("md5=%s", md5),
                        String.format("result=%s", result.getResultCode()),
                        String.format("resultstring=%s", result.getResultstring())
                );
            }
        }, "crack process", 1024);

        crackThread.start();

        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(!commander.isWorking()){
                    return;
                }
//                System.out.print("*** Aboritng cracking range " + Arrays.toString(ranges) + Util.CRLF);
                crackThread.interrupt();
                commander.setWorking(false);

                commander.sendRequest("POST", String.format("%s:%s/answermd5", toIp, toPort),
                        String.format("port=%s", commander.getServer().getPort()),
                        String.format("ip=%s", Util.getCurrentIp()),
                        String.format("id=%s", requestId),
                        String.format("md5=%s", md5),
                        String.format("result=%s", 2)
                );
                timer.cancel();
            }
        }, 7000);

        return String.valueOf(ServerContext.OK_CODE);
    }
}
