package inc;

import inc.util.Commands;
import inc.util.Util;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class FxApplication extends Application {

    private Commands commander = new Commands();

    public static void main(String... args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        final Text serverIndicator = new Text("Stopped");
        serverIndicator.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        serverIndicator.setFill(Color.RED);
        Text sceneTitle = new Text("Peer to Peer MD5");
        sceneTitle.setFont(Font.font("Arial", FontWeight.NORMAL, 20));

        Text machinesText = new Text("Machines:");
        machinesText.setFill(Color.BLUEVIOLET);
        machinesText.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Label portLabel = new Label("Port:");
        Label ttlLabel = new Label("Ttl:");
        Label timoutLabel = new Label("Timeout:");
        final TextField textFieldPort = new TextField();
        final TextField ttlTextField = new TextField();
        final TextField timoutField = new TextField();
        ttlTextField.setText(String.valueOf(commander.getTtl()));
        textFieldPort.setMaxWidth(50);
        timoutField.setMaxWidth(50);
        ttlTextField.setMaxWidth(50);
        Button startServerButton = new Button("Start Server");
        Button setTtlButton = new Button("Set TTL");
        Button setTimeout = new Button("Set Timout");
        startServerButton.setDefaultButton(true);
        Button stopServerButton = new Button("Stop Server");
        final TextArea textArea = new TextArea();
        textArea.setEditable(false);

        timoutField.setText(String.valueOf(commander.getTimeout()));

        setTimeout.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int timeout = Integer.parseInt(timoutField.getText());
                commander.setTimeout(timeout);
                textArea.appendText(String.format("Timeout property set to '%d'", timeout) + Util.CRLF);
            }
        });

        startServerButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int port;
                try {
                    port = Integer.parseInt(textFieldPort.getText());
                } catch (NumberFormatException e) {
                    port = 1215;
                }
                textArea.appendText(commander.startServer(port) + Util.CRLF);
                serverIndicator.setText("Started: " + port);
                serverIndicator.setFill(Color.GREEN);
            }
        });

        stopServerButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                textArea.appendText(commander.stopServer() + Util.CRLF);
                serverIndicator.setText("Stopped");
                serverIndicator.setFill(Color.RED);
            }
        });
        textArea.appendText(commander.readConfigFromFile("machines.txt") + Util.CRLF);

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                commander.stopServer();
                Platform.exit();
                System.exit(0);
            }
        });

        setTtlButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int ttl = Integer.parseInt(ttlTextField.getText());
                commander.setTtl(ttl);
                System.out.println(String.format("TTL values is set to '%s'", ttl));
            }
        });

        System.setOut(new PrintStream(new OutputStream() {
            @Override
            public void write(final int b) throws IOException {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        textArea.appendText(String.valueOf((char) b));
                    }
                });
            }
        }));

        GridPane pane = new GridPane();
        pane.setPadding(new Insets(25));
        pane.setHgap(10);
        pane.setVgap(12);

        Scene scene = new Scene(pane, 800, 300);

        primaryStage.setTitle("Peer to Peer (md5)");
        primaryStage.setScene(scene);

        pane.add(sceneTitle, 1, 1, 3, 1);

        pane.add(portLabel, 1, 2, 1, 1);
        pane.add(textFieldPort, 2, 2, 1, 1);
        pane.add(startServerButton, 3, 2, 1, 1);
        pane.add(stopServerButton, 4, 2, 1, 1);

        pane.add(ttlLabel, 1, 3);
        pane.add(ttlTextField, 2, 3);
        pane.add(setTtlButton, 3, 3);

        pane.add(timoutLabel, 1, 4);
        pane.add(timoutField, 2, 4);
        pane.add(setTimeout, 3, 4);

        pane.add(textArea, 5, 1, 10, 10);
        pane.add(serverIndicator, 4, 3);
        pane.add(machinesText, 1, 5, 3, 1);


        VBox vBox = new VBox();
        if (Commands.computers != null && Commands.computers.length > 0) {
            for (String ip : Commands.computers) {
                Text comp = new Text(ip);
                comp.setFill(Color.VIOLET);
                vBox.getChildren().add(comp);
            }
        }
        pane.add(vBox, 1, 6, 3, 5);

        primaryStage.show();
    }
}
