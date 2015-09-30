package inc.controller;

import inc.util.Commands;

public class StopServerCommand implements Command {
    @Override
    public void execute(String... params) {
        new Commands().stopServer();
    }
}
