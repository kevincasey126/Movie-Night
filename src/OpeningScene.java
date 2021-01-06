import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.io.IOException;

public class OpeningScene {
    Model model;
    private static final String IDLE_BUTTON_STYLE = "-fx-background-color: #3892C7";
    private static final String HOVERED_BUTTON_STYLE = "-fx-background-color: #005BFF";
    private static final Paint TEXT_FILL = Color.web("#384BC7");

    //private final Stage thisStage;

    @FXML
    private Button log_in_button;

    @FXML
    private Button create_account_button;

    public OpeningScene(Model newModel) {
        //thisStage = new Stage();
        model = newModel;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("OpeningScene.fxml"));

            loader.setController(this);

            model.stage.setScene(new Scene(loader.load()));

            model.stage.setTitle("MovieNight - OpeningScene");

            log_in_button.setStyle(IDLE_BUTTON_STYLE);
            log_in_button.setTextFill(TEXT_FILL);
            log_in_button.setOnMouseEntered(e -> log_in_button.setStyle(HOVERED_BUTTON_STYLE));
            log_in_button.setOnMouseExited(e -> log_in_button.setStyle(IDLE_BUTTON_STYLE));

            create_account_button.setStyle(IDLE_BUTTON_STYLE);
            create_account_button.setTextFill(TEXT_FILL);
            create_account_button.setOnMouseEntered(e -> create_account_button.setStyle(HOVERED_BUTTON_STYLE));
            create_account_button.setOnMouseExited(e -> create_account_button.setStyle(IDLE_BUTTON_STYLE));


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pressedLogIn(ActionEvent event) throws IOException {
        LogInScene logInScene = new LogInScene(model);
    }

    public void pressedCreateAccount(ActionEvent event) throws IOException {
        CreateAccountScene createAccountScene = new CreateAccountScene(model);
    }

    public void showStage() {
        model.stage.showAndWait();
    }
}
