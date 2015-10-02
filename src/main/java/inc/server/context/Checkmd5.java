package inc.server.context;

import java.util.Map;

//TODO data here
// {"ip": "55.66.77.88", "port": "6788", "id": "siinonid", "md5": "siinonmd5string", "ranges": ["ax?o?ssss","aa","ab","ac","ad"], "wildcard": "?", "symbolrange": [[3,10],[100,150]]}
public class Checkmd5 implements ServerContext {
    @Override
    public String executeCommand(Map<String, String> request) {
        return "Start checking md5";
    }
}
