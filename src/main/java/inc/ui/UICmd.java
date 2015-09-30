package inc.ui;

import inc.controller.*;
import inc.util.Commands;
import inc.util.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class UICmd implements Runnable {

    private static final String CONSOLE_COMMAND_KILL = "kill";
    private static final String CONSOLE_COMMAND_START_SERVER = "startserver";
    private static final String CONSOLE_COMMAND_STOP_SERVER = "stopserver";
    private static final String CONSOLE_COMMAND_SEND_REQ = "send";
    private static final String CONSOLE_COMMAND_NODE_INFO = "nodeinfo";

    private Map<String, Command> installedCommands;

    private static boolean isTerminated;
    public static final String[] knownUrls = new String[]{
        "127.0.0.1:1111", "127.0.0.1:2222", "127.0.0.1:3333"
    };

    public UICmd() {
        installedCommands = new HashMap<>();

        installedCommands.put(CONSOLE_COMMAND_START_SERVER, new StartServerCommand());
        installedCommands.put(CONSOLE_COMMAND_SEND_REQ, new SendRequestCommand());
        installedCommands.put(CONSOLE_COMMAND_STOP_SERVER, new StopServerCommand());
        installedCommands.put(CONSOLE_COMMAND_NODE_INFO, new GetNodeInfoCommand());
    }

    @Override
    public void run() {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        while (!isTerminated) {
            askACommand(bufferedReader);
        }

    }

    private void askACommand(BufferedReader bufferedReader) {
        System.out.print("Command: ");

        String inputLine = null;
        try {
            inputLine = bufferedReader.readLine();
        } catch (IOException ignored) {
        }
        if (inputLine == null || inputLine.equals("")) {
            return;
        }

        doAction(inputLine);
    }


    public void doAction(String input) {
        String command = Util.getCommandFromInput(input);
        String[] params = Util.getParamsFromInput(input);

        if (CONSOLE_COMMAND_KILL.equals(command)) {
            isTerminated = true;
            System.out.println("Terminating...");
            new Commands().stopServer();
        }

        Command controller = installedCommands.get(command);

        if (controller != null) {
            controller.execute(params);
        } else {
            System.out.println(String.format("No such command installed '%s'", command));
        }
    }

}
