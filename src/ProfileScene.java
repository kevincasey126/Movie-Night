import com.amazonaws.auth.profile.internal.Profile;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class ProfileScene {
  private static final String IDLE_BUTTON_STYLE = "-fx-background-color: #3892C7";
  private static final String HOVERED_BUTTON_STYLE = "-fx-background-color: #005BFF";
  private static final Paint TEXT_FILL = Color.web("#384BC7");
  private Model model;

  private ApiQuery api;

  @FXML private Button profile_back_button, change_ProfilePicture_Button, add_Service_Button, change_Password_Button;

  @FXML private MenuItem netflix_click,
      hulu_click,
      crave_click,
      prime_click,
      netflix_remove,
      hulu_remove,
      crave_remove,
      prime_remove;

  @FXML private ImageView profile_picture;

  @FXML private Label username_text, change_error;

  @FXML private ListView services_list;

  @FXML private VBox movie_ratings_vbox;

  @FXML private AnchorPane anchor;

  @FXML private AnchorPane anchorDesired;

  @FXML private PasswordField password_field;

  @FXML private VBox desired_movie_vbox;

  public ProfileScene(Model newModel) {
    model = newModel;
    api = new ApiQuery();
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("ProfileScene.fxml"));

      loader.setController(this);

      model.stage.setScene(new Scene(loader.load()));

      model.stage.setTitle("MovieNight - ProfileScene");

      profile_back_button.setStyle(IDLE_BUTTON_STYLE);
      profile_back_button.setTextFill(TEXT_FILL);
      profile_back_button.setOnMouseEntered(e -> profile_back_button.setStyle(HOVERED_BUTTON_STYLE));
      profile_back_button.setOnMouseExited(e -> profile_back_button.setStyle(IDLE_BUTTON_STYLE));

      change_ProfilePicture_Button.setStyle(IDLE_BUTTON_STYLE);
      change_ProfilePicture_Button.setTextFill(TEXT_FILL);
      change_ProfilePicture_Button.setOnMouseEntered(e -> change_ProfilePicture_Button.setStyle(HOVERED_BUTTON_STYLE));
      change_ProfilePicture_Button.setOnMouseExited(e -> change_ProfilePicture_Button.setStyle(IDLE_BUTTON_STYLE));

      change_Password_Button.setStyle(IDLE_BUTTON_STYLE);
      change_Password_Button.setTextFill(TEXT_FILL);
      change_Password_Button.setOnMouseEntered(e -> change_Password_Button.setStyle(HOVERED_BUTTON_STYLE));
      change_Password_Button.setOnMouseExited(e -> change_Password_Button.setStyle(IDLE_BUTTON_STYLE));


    } catch (IOException e) {
      e.printStackTrace();
    }

    // set profile picture
    profile_picture.setImage(ProfilePicture.getProfilePic(User.getUserName()));
    // set username
    username_text.setText(User.getUserName());
    // get all streaming services into an pbservable list so the listview can present them
    ObservableList<String> services = FXCollections.observableArrayList();

    services.addAll(Server.getUsersStreamingServices(User.getUserName()).keySet());
    services_list.setItems(services);

    setUpMovieRatingBox();
    setUpDesiredMovieBox();
    change_ProfilePicture_Button.setOnAction(event -> pressedSetProfilePicButton());
    change_Password_Button.setOnAction(event -> pressedConfirmPasswordButton());
    hulu_click.setOnAction(event -> pressedAddServiceButton("Hulu"));
    netflix_click.setOnAction(event -> pressedAddServiceButton("Netflix"));
    crave_click.setOnAction(event -> pressedAddServiceButton("Crave"));
    prime_click.setOnAction(event -> pressedAddServiceButton("Prime"));

    hulu_remove.setOnAction(event -> pressedRemoveServiceButton("Hulu"));
    netflix_remove.setOnAction(event -> pressedRemoveServiceButton("Netflix"));
    crave_remove.setOnAction(event -> pressedRemoveServiceButton("Crave"));
    prime_remove.setOnAction(event -> pressedRemoveServiceButton("Prime"));
  }

  public void pressedBackButton(ActionEvent event) throws IOException {
    MainScene mainScene = new MainScene(model);
  }

  public void pressedSetProfilePicButton() {
    ProfilePicture.setProfilePic();
    profile_picture.setImage(ProfilePicture.getProfilePic(User.getUserName()));
  }

  public void pressedAddServiceButton(String newService) {
    Server.addStreamingService(newService, User.getUserName());
    ObservableList<String> services = FXCollections.observableArrayList();
    services.addAll(Server.getUsersStreamingServices(User.getUserName()).keySet());
    services_list.setItems(services);
  }

  public void pressedRemoveServiceButton(String removeService) {
    Server.removeStreamingService(removeService, User.getUserName());
    ObservableList<String> services = FXCollections.observableArrayList();
    services.addAll(Server.getUsersStreamingServices(User.getUserName()).keySet());
    services_list.setItems(services);
  }

  public void pressedConfirmPasswordButton(){
    LogInReturn password_check = Server.attemptLogIn(User.getUserName(), password_field.getText());

    if(password_check.getSuccess()){
      change_Password_Button.setText("Change Password");
      password_field.clear();
      password_field.setPromptText("Enter New Password");
      change_error.setVisible(false);
      change_Password_Button.setOnAction(event -> pressedChangePasswordButton());
    }
    else{
      change_error.setText("The confirmed password is incorrect!");
      change_error.setVisible(true);
    }
  }

  public void pressedChangePasswordButton(){
    if(!password_field.getText().isBlank()){
      Server.updatePassword(User.getUserName(), password_field.getText());
      password_field.clear();
      password_field.setPromptText("Enter Current Password");
      change_error.setTextFill(Color.BLACK);
      change_error.setText("New Password Set!");
      change_error.setVisible(true);
      change_Password_Button.setOnAction(event -> pressedConfirmPasswordButton());
    }
    else{
      change_error.setText("Must enter a new valid password!");
      change_error.setVisible(true);
    }
  }

  private void setUpMovieRatingBox(){
    // get all the movies into a hash map and iterate through them collecting each movie title and
    // rating as well as
    // getting their movie class to generate a movie post url
    HashMap<String, Map<String, Object>> fav_movies =
        Server.getUsersFavouriteMovies(User.getUserName());

    Iterator movie_it = fav_movies.entrySet().iterator();
    while (movie_it.hasNext()) {
      Map.Entry pair = (Map.Entry) movie_it.next();
      Movie movie = api.getMovie(pair.getKey().toString());

      HBox movie_collection = new HBox();
      ImageView movie_poster;
      Image image;
      try {
        image =
            new Image(movie.getMoviePosterUrl(), 75, 122, false, false);
        movie_poster = new ImageView(image);
      } catch (Exception e) {
        String filePath = "";
        try{
          filePath = new File(ApiQuery.class.getProtectionDomain().getCodeSource().getLocation()
              .toURI()).getParentFile().getPath();
          System.out.println(filePath);
        }
        catch(Exception ignored){

        }
        FileInputStream inputstream;
        try {
          // use placeholder image if failed to retrieve from url
          inputstream = new FileInputStream(filePath + "/placeholder.png");
          image = new Image(inputstream, 75, 112, false, false);
          movie_poster = new ImageView(image);
        } catch (FileNotFoundException fnfe) {
          image = null;
          movie_poster = new ImageView(image);
        }
      }
      Button movie_button = new Button();
      movie_button.setGraphic(movie_poster);
      movie_button.setMaxSize(75,112);
      movie_button.setOnAction(
          actionEvent -> {altMovieScene movieScene = new altMovieScene(model, movie, model.stage.getScene());

          });
      VBox info = new VBox();
      Label movie_title = new Label(pair.getKey().toString());
      Label movie_rating = new Label(fav_movies.get(pair.getKey()).get("rating").toString() + "/5");

      Button removeButton = new Button("Remove");

      movie_title.setMinSize(400, 10);
      movie_title.setFont(new Font(20));
      info.getChildren().addAll(movie_title, movie_rating, removeButton);
      info.setSpacing(15);

      movie_collection.getChildren().addAll(movie_button, info);

      anchor.setMinHeight(anchor.getMinHeight() + 120);
      movie_ratings_vbox.setMinHeight(movie_ratings_vbox.getMinHeight() + 120);
      movie_ratings_vbox.getChildren().add(movie_collection);
      removeButton.setOnAction(
          event -> removeMovieRating(movie.getMovieName(), movie_collection));
    }
  }

  private void setUpDesiredMovieBox(){
    // get all the movies into a hash map and iterate through them collecting each movie title and
    // rating as well as
    // getting their movie class to generate a movie post url
    HashMap<String, Map<String, Object>> fav_movies =
        Server.getUsersWantToWatchMovies(User.getUserName());

    Iterator movie_it = fav_movies.entrySet().iterator();
    while (movie_it.hasNext()) {
      Map.Entry pair = (Map.Entry) movie_it.next();
      Movie movie = api.getMovie(pair.getKey().toString());
      System.out.println(movie.getMovieName());
      HBox movie_collection = new HBox();
      ImageView movie_poster;
      Image image;
      try {
        image =
            new Image(movie.getMoviePosterUrl(), 75, 122, false, false);
        movie_poster = new ImageView(image);
      } catch (Exception e) {
        String filePath = "";
        try{
          filePath = new File(ApiQuery.class.getProtectionDomain().getCodeSource().getLocation()
              .toURI()).getParentFile().getPath();
          System.out.println(filePath);
        }
        catch(Exception ignored){

        }
        FileInputStream inputstream;
        try {
          // use placeholder image if failed to retrieve from url
          inputstream = new FileInputStream(filePath + "/placeholder.png");
          image = new Image(inputstream, 75, 112, false, false);
          movie_poster = new ImageView(image);
        } catch (FileNotFoundException fnfe) {
          image = null;
          movie_poster = new ImageView(image);
        }
      }
      Button movie_button = new Button();
      movie_button.setGraphic(movie_poster);
      movie_button.setMaxSize(75,112);
      movie_button.setOnAction(
          actionEvent -> {altMovieScene movieScene = new altMovieScene(model, movie, model.stage.getScene());

          });
      VBox info = new VBox();
      Label movie_title = new Label(pair.getKey().toString());

      Button removeButton = new Button("Remove");

      movie_title.setMinSize(400, 10);
      movie_title.setFont(new Font(20));
      info.getChildren().addAll(movie_title, removeButton);
      info.setSpacing(15);

      movie_collection.getChildren().addAll(movie_button, info);

      anchorDesired.setMinHeight(anchorDesired.getMinHeight() + 120);
      desired_movie_vbox.setMinHeight(desired_movie_vbox.getMinHeight() + 120);
      desired_movie_vbox.getChildren().add(movie_collection);
      removeButton.setOnAction(
          event -> removeDesiredMovie(movie.getMovieName(), movie_collection));
    }
  }

  private void removeMovieRating(String movieName, HBox hBoxToBeRemoved) {
    Server.removeFavouriteMovie(movieName, User.getUserName());
    movie_ratings_vbox.getChildren().remove(hBoxToBeRemoved);
    movie_ratings_vbox.setMinHeight(movie_ratings_vbox.getMinHeight() - 120);
    anchor.setMinHeight(anchor.getMinHeight() - 120);
  }

  private void removeDesiredMovie(String movieName, HBox hBoxToBeRemoved) {
    if(User.getMovieList().movieNames.contains(movieName)){
      User.getMovieList().movieNames.remove(movieName);
    }
    Server.removeWantToWatch(movieName, User.getUserName());
    desired_movie_vbox.getChildren().remove(hBoxToBeRemoved);
    desired_movie_vbox.setMinHeight(movie_ratings_vbox.getMinHeight() - 120);
    anchorDesired.setMinHeight(anchor.getMinHeight() - 120);
  }
}
