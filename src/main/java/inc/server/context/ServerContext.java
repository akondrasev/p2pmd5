package inc.server.context;

import java.util.Map;

public interface ServerContext {
    int OK_CODE = 0;
    int UNKNOWN_CONTEXT_CODE = 1;
    int WRONG_REQUEST_PARAMS_CODE = 2;
    int IGNORED_REQUEST_CODE = 3;
    String executeCommand(Map<String, String> request);
}
