package inc;

import inc.ui.UICmd;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {
        UICmd ui = new UICmd();
        Thread uiThread = new Thread(ui, "Ui thread");

        uiThread.start();
    }
}