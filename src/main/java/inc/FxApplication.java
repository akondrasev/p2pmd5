package inc;

import inc.util.Commands;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class FxApplication extends Application {

    private String machinesJson;

    private Commands commander = new Commands();

    public static void main(String... args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(25, 25, 25, 25));

        Scene scene = new Scene(gridPane, 800, 300);

        primaryStage.setTitle("Peer to Peer (md5)");
        primaryStage.setScene(scene);

        Text sceneTitle = new Text("Choose Action");
        sceneTitle.setFont(Font.font("Arial", FontWeight.NORMAL, 20));
        gridPane.add(sceneTitle, 0, 0, 2, 1);


        Label total = new Label("Port:");
        gridPane.add(total, 0, 1);

        TextField textFieldPort = new TextField();
        gridPane.add(textFieldPort, 1, 1);
        Button startServerButton = new Button("Start Server");
        startServerButton.setDefaultButton(true);
        gridPane.add(startServerButton, 2, 1);
        Button stopServerButton = new Button("Stop Server");
        gridPane.add(stopServerButton, 3, 1);
        stopServerButton.cancelButtonProperty();

        TextArea textArea = new TextArea();
        textArea.setWrapText(true);
        gridPane.add(textArea, 1, 2);

        startServerButton.setOnAction(event -> {
            int port;
            try {
                port = Integer.parseInt(textFieldPort.getText());
            } catch (NumberFormatException e) {
                port = 1215;
            }
            commander.startServer(port);
        });

        stopServerButton.setOnAction(event -> commander.stopServer()
        );

        System.setOut(new PrintStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                textArea.appendText(String.valueOf((char) b));
            }
        }));

        primaryStage.show();
        //TODO make server close on exit
    }
}
