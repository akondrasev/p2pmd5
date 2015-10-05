package inc.server.context;

import inc.util.Commands;

import java.util.Map;

// {"ip": "55.66.77.88", "port": "6788", "id": "asasasas", "md5": "siinonmd5string", "result": 0, "resultstring": "sssasasc"}
public class Answermd5 implements ServerContext {
    @Override
    public String executeCommand(Map<String, String> request) {
        Commands commander = new Commands();
        commander.setDone(true);

        if (request.get("result").equals("0")) {
            commander.setResult(request.get("resultstring"));
        }

        return "result";
    }
}
