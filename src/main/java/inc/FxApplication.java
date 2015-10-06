package inc;

import inc.util.Commands;
import inc.util.Util;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

//http://127.0.0.1:1111/resource?sendip=127.0.0.1&sendport=1111&ttl=3&id=123&noask=192.168.10.76_1111&noask=127.0.0.1_1111
public class FxApplication extends Application {

    private Commands commander = new Commands();

    public static void main(String... args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Text serverIndicator = new Text("Stopped");
        serverIndicator.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        serverIndicator.setFill(Color.RED);
        Text sceneTitle = new Text("Peer to Peer MD5");
        sceneTitle.setFont(Font.font("Arial", FontWeight.NORMAL, 20));

        Text machinesText = new Text("Machines:");
        machinesText.setFill(Color.BLUEVIOLET);
        machinesText.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Label portLabel = new Label("Port:");
        Label ttlLabel = new Label("Ttl:");
        TextField textFieldPort = new TextField();
        TextField ttlTextField = new TextField();
        ttlTextField.setText(String.valueOf(commander.getTtl()));
        textFieldPort.setMaxWidth(50);
        ttlTextField.setMaxWidth(50);
        Button startServerButton = new Button("Start Server");
        Button setTtlButton = new Button("Set TTL");
        startServerButton.setDefaultButton(true);
        Button stopServerButton = new Button("Stop Server");
        TextArea textArea = new TextArea();
        textArea.setEditable(false);

        startServerButton.setOnAction(event -> {
            int port;
            try {
                port = Integer.parseInt(textFieldPort.getText());
            } catch (NumberFormatException e) {
                port = 1215;
            }
            textArea.appendText(commander.startServer(port) + Util.CRLF);
            serverIndicator.setText("Started: " + port);
            serverIndicator.setFill(Color.GREEN);
        });

        stopServerButton.setOnAction(event -> {
            textArea.appendText(commander.stopServer() + Util.CRLF);
            serverIndicator.setText("Stopped");
            serverIndicator.setFill(Color.RED);
        });
        textArea.appendText(commander.readConfigFromFile("machines.txt") + Util.CRLF);

        primaryStage.setOnCloseRequest(t -> {
            commander.stopServer();
            Platform.exit();
            System.exit(0);
        });

        setTtlButton.setOnAction(event -> {
            int ttl = Integer.parseInt(ttlTextField.getText());
            commander.setTtl(ttl);
            System.out.println(String.format("TTL values is set to '%s'", ttl));
        });

        System.setOut(new PrintStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                Platform.runLater(() -> textArea.appendText(String.valueOf((char) b)));
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

        pane.add(textArea, 5, 1, 10, 10);
        pane.add(serverIndicator, 4, 3);
        pane.add(machinesText, 1, 4, 3, 1);


        VBox vBox = new VBox();
        for (String ip : Commands.computers) {
            Text comp = new Text(ip);
            comp.setFill(Color.VIOLET);
            vBox.getChildren().add(comp);
        }

        pane.add(vBox, 1, 5, 3, 5);

        Platform.runLater(primaryStage::show);
    }
}
