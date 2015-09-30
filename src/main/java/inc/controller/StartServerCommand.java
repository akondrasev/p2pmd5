package inc.controller;

import inc.util.Commands;

public class StartServerCommand implements Command {

    @Override
    public void execute(String... params) {
        int port = 80;
        if (params.length == 0) {
            System.out.println("Port number is not specified, trying to use default 80");
        } else {
            try {
                port = Integer.parseInt(params[0]);
            } catch (NumberFormatException e) {
                System.out.println("port number expected");
            }
        }

        new Commands().startServer(port);
    }

}