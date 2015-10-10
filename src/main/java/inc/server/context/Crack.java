package inc.server.context;

import inc.dto.Answer;
import inc.util.Commands;
import inc.util.Util;

import java.util.List;
import java.util.Map;

public class Crack implements ServerContext {
    private final Commands commander = new Commands();
    @Override
    public String executeCommand(Map<String, String> request) {
        String md5 = request.get("md5");

        final String sendip = Util.getCurrentIp();
        final int sendport = new Commands().getServer().getPort();
        final String requestId = String.valueOf(commander.incrementReqCount() + "ID");
        commander.getResultsDoneFlags().put(requestId, false);
        final int ttlValue = commander.getTtl();

        commander.getResultsMap().put(requestId, null);
        commander.getMd5Tasks().put(requestId, md5);

        sendResource(sendip, sendport, requestId, ttlValue, String.format("%s_%s", sendip, sendport));


        for (int i = 0; !commander.getResultsDoneFlags().get(requestId); i++) {
            if (i > commander.getTimeout()) {
                StringBuilder stringBuffer = new StringBuilder();
                List<Answer> answers = commander.getAnswersMap().get(requestId);
                if(answers != null){
                    for(Answer answer : answers){
                        stringBuffer.append(answer.toString()).append(Util.CRLF);
                    }
                }
                stringBuffer.append("Time is out.");
                stringBuffer.append(" (");
                stringBuffer.append(commander.getTimeout());
                stringBuffer.append(" seconds)");
                return stringBuffer.toString();
            }
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException ignored) {
            }
        }
        return String.format("Result for request id '%s': %s = %s", requestId, commander.getResultsMap().get(requestId), md5);
    }

    public void sendResource(final String sendip, final int sendport, final String requestId, final int ttl, final String noask){
        new Thread(new Runnable() {
            @Override
            public void run() {
                commander.sendRequest("GET", String.format("%s:%s/resource", sendip, sendport),
                        String.format("sendip=%s", sendip),
                        String.format("sendport=%s", sendport),
                        String.format("id=%s", requestId),
                        String.format("ttl=%s", ttl),
                        String.format("noask=%s", noask)//String.format("%s_%s", sendip, sendport)
                );
            }
        }).start();
    }
}
