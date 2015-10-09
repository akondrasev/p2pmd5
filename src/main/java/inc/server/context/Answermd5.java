package inc.server.context;

import inc.util.Commands;

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
        boolean isDoneCurrent = commander.getResultsDoneFlags().get(requestId);

        if (isDoneCurrent) {
            return String.valueOf(ServerContext.IGNORED_REQUEST_CODE);
        }


        commander.getResultsDoneFlags().put(requestId, true);
        String resultCode = request.get("result");
        if (resultCode.equals(OK_ANSWER)) {
            commander.getResultsMap().put(requestId, request.get("resultstring"));
        } else if (resultCode.equals(CANNOT_FIND_ANSWER)) {
            commander.getResultsMap().put(requestId, "cannot find answer");
        } else if (resultCode.equals(NO_ENOUGH_TIME_ANSWER)) {
            commander.getResultsMap().put(requestId, "did not have enough time to crack");
        }

        return String.valueOf(ServerContext.OK_CODE);
    }
}
