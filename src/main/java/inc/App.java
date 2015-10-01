package inc;

import inc.ui.UICmd;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {
        //TODO reading IP addresses from the file
        //TODO reading port number from args[0] and run server immediately
        //TODO query all adresses from file

        UICmd ui = new UICmd();
        Thread uiThread = new Thread(ui, "Ui thread");

        uiThread.start();
    }
}