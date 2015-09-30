package inc.server.context;

import java.util.Map;

//TODO data here:
// {"ip": "55.66.77.88", "port": "6788", "id": "asasasas", "md5": "siinonmd5string", "result": 0, "resultstring": "sssasasc"}
public class Answermd5 implements ServerCommand {
    @Override
    public String executeCommand(Map<String, Object> request) {
        return "result";
    }
}
