package inc.server.context;

import java.util.Map;

public interface ServerContext {
    String executeCommand(Map<String, String> request);
}
