package inc.server.context;

import inc.dto.Answer;
import inc.util.Commands;
import inc.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// {"ip": "55.66.77.88", "port": "6788", "id": "asasasas", "md5": "siinonmd5string", "result": 0, "resultstring": "sssasasc"}
public class Answermd5 implements ServerContext {

    private static final String OK_ANSWER = "0";
    private static final String CANNOT_FIND_ANSWER = "1";
    private static final String NO_ENOUGH_TIME_ANSWER = "2";

    @Override
    public String executeCommand(Map<String, String> request) {
        Commands commander = new Commands();

        String requestId = request.get("id");
        String ip = request.get("ip");
        String port = request.get("port");
        String resultCode = request.get("result");

        boolean isDoneCurrent = commander.getResultsDoneFlags().get(requestId);

        if (isDoneCurrent) {
            return String.valueOf(ServerContext.IGNORED_REQUEST_CODE);
        }

        String answerString = null;
        switch (resultCode) {
            case "0":
                String resultstring = request.get("resultstring");
                commander.getResultsMap().put(requestId, resultstring);
                answerString = resultstring;
                commander.getResultsDoneFlags().put(requestId, true);
                break;
            case "1":
                answerString = "not found";
                String sendip = Util.getCurrentIp();
                int sendport = commander.getServer().getPort();
                new Crack().sendResource(sendip, sendport, requestId, commander.getTtl(), String.format("%s_%s", sendip, sendport));
                break;
            case "2":
                answerString = "did not find within appropriate time";
                break;
        }


        Answer answer = new Answer(ip + ":" + port, answerString);
        List<Answer> answers = commander.getAnswersMap().get(requestId);

        if (answers == null) {
            answers = new ArrayList<>();
            commander.getAnswersMap().put(requestId, answers);
        }
        answers.add(answer);

        return String.valueOf(ServerContext.OK_CODE);
    }
}
