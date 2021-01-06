import com.sun.xml.bind.v2.model.core.ID;
import java.util.HashMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.io.IOException;

public class LogInScene implements SceneModelListener {
  private static final String IDLE_BUTTON_STYLE = "-fx-background-color: #3892C7";
  private static final String HOVERED_BUTTON_STYLE = "-fx-background-color: #005BFF";
  private static final Paint TEXT_FILL = Color.web("#384BC7");

  private Model model;

  @FXML private Button back_button;

  @FXML private Button enter_button;

  @FXML private TextField username_field;

  @FXML private TextField password_field;

  /*
    * LogInScene takes in the model, calls the loader, sets the controller and sets the scene
    * To change scenes to this scene, simply call the constructor for this scene

  */
  public LogInScene(Model newModel) {
    model = newModel;

    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("LogInScene.fxml"));

      loader.setController(this);

      model.stage.setScene(new Scene(loader.load()));

      model.stage.setTitle("MovieNight - LogInScene");

      back_button.setStyle(IDLE_BUTTON_STYLE);
      back_button.setTextFill(TEXT_FILL);
      back_button.setOnMouseEntered(e -> back_button.setStyle(HOVERED_BUTTON_STYLE));
      back_button.setOnMouseExited(e -> back_button.setStyle(IDLE_BUTTON_STYLE));

      enter_button.setStyle(IDLE_BUTTON_STYLE);
      enter_button.setTextFill(TEXT_FILL);
      enter_button.setOnMouseEntered(e -> enter_button.setStyle(HOVERED_BUTTON_STYLE));
      enter_button.setOnMouseExited(e -> enter_button.setStyle(IDLE_BUTTON_STYLE));


    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void pressedBack(ActionEvent event) throws IOException {
    OpeningScene openingScene = new OpeningScene(model);
  }

  public void pressedEnter(ActionEvent event) throws IOException {
    String user = username_field.getText();
    String pass = password_field.getText();

    LogInReturn logInReturn = Server.attemptLogIn(user, pass);
    if(logInReturn.getSuccess()){
      User.initialize(logInReturn.getProfile());
      MainScene mainScene = new MainScene(model);
    }
  }

  @Override
  public void modelChanged() {}
}
