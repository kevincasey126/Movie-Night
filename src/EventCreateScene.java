import com.sun.javafx.iio.ios.IosDescriptor;

import com.sun.xml.bind.v2.model.core.ID;
import java.io.IOException;
import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javax.swing.Action;
import javax.swing.Scrollable;

public class EventCreateScene {
  private Model model;
  private ApiQuery apiQuery;
  private int maxMovies;
  ArrayList<Movie> movies = new ArrayList<>();

  private static final String IDLE_BUTTON_STYLE = "-fx-background-color: #3892C7";
  private static final String HOVERED_BUTTON_STYLE = "-fx-background-color: #005BFF";
  private static final Paint TEXT_FILL = Color.web("#384BC7");

  @FXML private Button back_button;

  @FXML private Button add_movie_button;

  @FXML private Button create_event_button;

  @FXML private TextField event_name_field, date_field, location_field, add_movie_field;

  @FXML private VBox selected_movie_box;

  @FXML private Label error_label;

  public EventCreateScene(Model newModel) {
    model = newModel;
    apiQuery = new ApiQuery();
    movies = new ArrayList<>();
    maxMovies = 4;

    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("EventCreateScene.fxml"));

      loader.setController(this);

      model.stage.setScene(new Scene(loader.load()));

      model.stage.setTitle("MovieNight - EventCreateScene");

      add_movie_button.setStyle(IDLE_BUTTON_STYLE);
      add_movie_button.setTextFill(TEXT_FILL);
      add_movie_button.setOnMouseEntered(e -> add_movie_button.setStyle(HOVERED_BUTTON_STYLE));
      add_movie_button.setOnMouseExited(e -> add_movie_button.setStyle(IDLE_BUTTON_STYLE));

      create_event_button.setStyle(IDLE_BUTTON_STYLE);
      create_event_button.setTextFill(TEXT_FILL);
      create_event_button.setOnMouseEntered(e -> create_event_button.setStyle(HOVERED_BUTTON_STYLE));
      create_event_button.setOnMouseExited(e -> create_event_button.setStyle(IDLE_BUTTON_STYLE));

      back_button.setStyle(IDLE_BUTTON_STYLE);
      back_button.setTextFill(TEXT_FILL);
      back_button.setOnMouseEntered(e -> back_button.setStyle(HOVERED_BUTTON_STYLE));
      back_button.setOnMouseExited(e -> back_button.setStyle(IDLE_BUTTON_STYLE));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void pressedBackButton(ActionEvent event) throws IOException {
    MainScene mainScene = new MainScene(model);
  }

  public void pressedAddMovieButton(ActionEvent event) throws IOException {
    String movieTitle = add_movie_field.getText();
    if (!movieTitle.equals("")) {
      ArrayList<SearchResult> moviesToAdd = apiQuery.searchMovies(movieTitle);
      if (moviesToAdd.size() != 0) {
        SearchResult movieToAdd = moviesToAdd.get(0);
        Movie movieObject = apiQuery.getMovie(movieToAdd.getImdbID());
        if (movieObject != null) {

          movies.add(movieObject);

          HBox addedMovie = new HBox();
          Label title = new Label();
          Button remove = new Button();


          title.setText(movieObject.getMovieName());
          remove.setText("Remove");
          remove.setFont(Font.font("Berlin Sans FB Demi", 12));
          remove.setTextFill(TEXT_FILL);
          remove.setStyle(IDLE_BUTTON_STYLE);
          remove.setOnMouseEntered(e -> remove.setStyle(HOVERED_BUTTON_STYLE));
          remove.setOnMouseExited(e -> remove.setStyle(IDLE_BUTTON_STYLE));

          addedMovie.getChildren().add(title);
          addedMovie.getChildren().add(remove);
          addedMovie.setAlignment(Pos.CENTER_LEFT);
          addedMovie.setSpacing(5);

          selected_movie_box.getChildren().add(addedMovie);

          remove.setOnAction(
              new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                  movies.remove(movieObject);
                  selected_movie_box.getChildren().remove(addedMovie);
                }
              });
        } else {
          error_label.setText("No Results Found");
          error_label.setVisible(true);
        }
      } else {
        error_label.setText("No Results Found");
        error_label.setVisible(true);
      }
    } else {
      error_label.setText("Nothing in search field");
      error_label.setVisible(true);
    }
  }

  public void pressedCreateEventButton(ActionEvent event) throws IOException {
    String eventName = event_name_field.getText();
    String location = location_field.getText();
    String date = date_field.getText();
    if (!eventName.equals("") || !location.equals("") || !date.equals("")) {
      String eventID = Server.createEvent(User.getUserName(), eventName, location, date);

      Event newEvent = new Event(eventName, location, date, User.getUserName(), eventID);
      for (Movie i : movies) {
        String name = i.getMovieName();
        newEvent.addMovie(name);
        Server.addEventMovie(i.getMovieName(), eventID);
      }

      User.getEventList().addEvent(newEvent);
      MainScene mainScene = new MainScene(model);
    }
    else{
      error_label.setText("Empty event field");
    }
  }
}
