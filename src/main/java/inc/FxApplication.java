package inc;

import inc.util.Commands;
import inc.util.Util;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Timer;
import java.util.TimerTask;

public class FxApplication extends Application {

    private Commands commander = new Commands();

    public static void main(String... args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Text serverIndicator = new Text("Stopped");
        serverIndicator.setFont(Font.font("Arial", FontWeight.NORMAL, 20));
        serverIndicator.setFill(Color.RED);
        Text sceneTitle = new Text("Peer to Peer MD5");
        sceneTitle.setFont(Font.font("Arial", FontWeight.NORMAL, 20));

        Text machinesText = new Text("Machines:");
        machinesText.setFill(Color.BLUEVIOLET);
        machinesText.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Label portLabel = new Label("Port:");
        TextField textFieldPort = new TextField();
        textFieldPort.setMaxWidth(50);
        Button startServerButton = new Button("Start Server");
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

        primaryStage.show();
        primaryStage.setOnCloseRequest(t -> {
            commander.stopServer();
            Platform.exit();
            System.exit(0);
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

        HBox hBox = new HBox();
        hBox.setSpacing(5);
        hBox.setPadding(new Insets(5));
        hBox.getChildren().addAll(portLabel, textFieldPort, startServerButton, stopServerButton);
        pane.add(sceneTitle, 1,1,1,1);
        pane.add(hBox, 1, 2, 2, 1);
        pane.add(textArea, 3, 1, 1, 5);
        pane.add(serverIndicator, 3, 6);
        pane.add(machinesText, 1, 3);

        VBox vBox = new VBox();
        for (String ip : Commands.computers){
            Text comp = new Text(ip);
            comp.setFill(Color.VIOLET);
            vBox.getChildren().add(comp);
        }

        pane.add(vBox, 1, 4, 1, 5);
    }
}
