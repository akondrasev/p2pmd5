package inc.controller;

import inc.server.Server;
import inc.util.Commands;
import inc.util.Util;

public class GetNodeInfoCommand implements Command {
    @Override
    public void execute(String... params) {
        Server server = new Commands().getServer();

        if (server == null) {
            System.out.println("not started");
            return;
        }

        int port = server.getPort();
        String host = Util.getCurrentIp();

        System.out.println(String.format("%s:%d", host, port));
    }
}
