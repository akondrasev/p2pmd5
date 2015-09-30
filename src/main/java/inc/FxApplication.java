package inc;

import inc.ui.UICmd;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class FxApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(25, 25, 25, 25));

        Scene scene = new Scene(gridPane, 400, 200);

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

        startServerButton.setOnAction(event -> {
            String port = textFieldPort.getText();
            new UICmd().doAction(String.format("startserver %s", port));
        });

        stopServerButton.setOnAction(event -> {
            new UICmd().doAction("stopserver");
        });

        primaryStage.show();
    }

    public static void main(String... args) {
        launch(args);
    }
}
