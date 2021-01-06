import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class AddMovieToEventScene {
  private Model model;
  private Stage movieEventStage;
  private Map<String, Map<String, Object>> eventMap;
  private Map<String, String> reverseEventMap = new HashMap<>();
  private Movie movie;

  private static final String IDLE_BUTTON_STYLE = "-fx-background-color: #3892C7";
  private static final String HOVERED_BUTTON_STYLE = "-fx-background-color: #005BFF";
  private static final Paint TEXT_FILL = Color.web("#384BC7");

  @FXML
  private Button back_button;

  @FXML
  private Label title_label;

  @FXML
  private ComboBox event_list;

  @FXML
  private Button add_button;

  private String sentRequestName;

  public AddMovieToEventScene(Model newModel, Movie movie) {
    model = newModel;

    this.movie = movie;

    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("AddMovieToEventScene.fxml"));

      loader.setController(this);

      movieEventStage = new Stage();

      movieEventStage.setScene(new Scene(loader.load()));
      movieEventStage.setTitle("MovieNight - AddMovieToEventScene");

      title_label.setText("Adding " + movie.getMovieName() + " to an Event:");
      title_label.setTextFill(TEXT_FILL);
      title_label.setFont(Font.font("Berlin Sans FB Demi", 12));

      back_button.setStyle(IDLE_BUTTON_STYLE);
      back_button.setTextFill(TEXT_FILL);
      back_button.setOnMouseEntered(e -> back_button.setStyle(HOVERED_BUTTON_STYLE));
      back_button.setOnMouseExited(e -> back_button.setStyle(IDLE_BUTTON_STYLE));

      add_button.setStyle(IDLE_BUTTON_STYLE);
      add_button.setTextFill(TEXT_FILL);
      add_button.setOnMouseEntered(e -> add_button.setStyle(HOVERED_BUTTON_STYLE));
      add_button.setOnMouseEntered(e -> add_button.setStyle(IDLE_BUTTON_STYLE));

      eventMap = Server.getUsersOrganizingEvents(User.getUserName());
      ObservableList<String> eventNames = FXCollections.observableArrayList();

      for(String key : eventMap.keySet()){
        eventNames.add((String)(eventMap.get(key).get("eventName")));
        reverseEventMap.put(eventMap.get(key).get("eventName").toString(), key);
      }

      event_list.setItems(eventNames);

      movieEventStage.showAndWait();

    } catch (IOException e) {
      e.printStackTrace();
    }


  }

  /**
   * close the popup and return to the previous scene
   */
  public void pressedBackButton(){
    movieEventStage.close();
  }

  /**
   * Add the movie to the event selected in the combobox
   */
  public void addMovieToEvent(){
    String pickedEvent = event_list.getValue().toString();
    Server.addEventMovie(movie.getMovieName(), reverseEventMap.get(pickedEvent));
    System.out.println(Server.getEventMovies(reverseEventMap.get(pickedEvent)));
    movieEventStage.close();
  }


}
