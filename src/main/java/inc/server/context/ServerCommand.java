package inc.server.context;

import java.util.Map;

public interface ServerCommand {
    String executeCommand(Map<String, String> request);
}
