import com.sun.javafx.iio.ios.IosDescriptor;
import com.sun.xml.bind.v2.model.core.ID;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HorizontalDirection;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.stage.Window;
import javax.swing.plaf.synth.SynthDesktopIconUI;
import software.amazon.ion.SystemSymbols;

public class MainScene {

  private Model model;
  private static final String IDLE_BUTTON_STYLE = "-fx-background-color: #3892C7";
  private static final String HOVERED_BUTTON_STYLE = "-fx-background-color: #005BFF";
  private static final Paint TEXT_FILL = Color.web("#384BC7");


  @FXML private AnchorPane main_anchor_pane;
  @FXML private TextField movie_search;
  @FXML private Button logout_button;
  @FXML private Button profile_button;
  @FXML private Button create_Events_Button;
  @FXML private Button browse_Button;
  @FXML private Button add_Friends_Button;
  @FXML private TextArea featured_Text_Area;
  @FXML private Button featuredMovie;
  @FXML private Image movieImage;
  @FXML private Button featured1_button, featured2_button, featured3_button, featured4_button;
  @FXML private VBox request_scroll_space;
  @FXML private VBox friends_scroll_space;
  @FXML private VBox events_scroll_space;
  @FXML private VBox pending_events_scroll;
  private ArrayList<Movie> featuredMovies;
  private final int FEATURED_MOVIE_LIST_SIZE = 4;
  private final int FEATURED_TEXT_LENGTH = 8;
  private Button goToMovieScene = new Button();

  public MainScene(Model newModel) {
    model = newModel;

    // Loads the current scene
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScene.fxml"));

      loader.setController(this);

      model.stage.setScene(new Scene(loader.load()));

      model.stage.setTitle("MovieNight - MainScene");

      featured1_button.setStyle(IDLE_BUTTON_STYLE);
      featured1_button.setOnMouseEntered(e -> featured1_button.setStyle(HOVERED_BUTTON_STYLE));
      featured1_button.setOnMouseExited(e -> featured1_button.setStyle(IDLE_BUTTON_STYLE));

      featured2_button.setStyle(IDLE_BUTTON_STYLE);
      featured2_button.setOnMouseEntered(e -> featured2_button.setStyle(HOVERED_BUTTON_STYLE));
      featured2_button.setOnMouseExited(e -> featured2_button.setStyle(IDLE_BUTTON_STYLE));

      featured3_button.setStyle(IDLE_BUTTON_STYLE);
      featured3_button.setOnMouseEntered(e -> featured3_button.setStyle(HOVERED_BUTTON_STYLE));
      featured3_button.setOnMouseExited(e -> featured3_button.setStyle(IDLE_BUTTON_STYLE));

      featured4_button.setStyle(IDLE_BUTTON_STYLE);
      featured4_button.setOnMouseEntered(e -> featured4_button.setStyle(HOVERED_BUTTON_STYLE));
      featured4_button.setOnMouseExited(e -> featured4_button.setStyle(IDLE_BUTTON_STYLE));

      add_Friends_Button.setStyle(IDLE_BUTTON_STYLE);
      add_Friends_Button.setTextFill(TEXT_FILL);
      add_Friends_Button.setOnMouseEntered(e -> add_Friends_Button.setStyle(HOVERED_BUTTON_STYLE));
      add_Friends_Button.setOnMouseExited(e -> add_Friends_Button.setStyle(IDLE_BUTTON_STYLE));

      create_Events_Button.setStyle(IDLE_BUTTON_STYLE);
      create_Events_Button.setTextFill(TEXT_FILL);
      create_Events_Button.setOnMouseEntered(e -> create_Events_Button.setStyle(HOVERED_BUTTON_STYLE));
      create_Events_Button.setOnMouseExited(e -> create_Events_Button.setStyle(IDLE_BUTTON_STYLE));

      profile_button.setStyle(IDLE_BUTTON_STYLE);
      profile_button.setTextFill(TEXT_FILL);
      profile_button.setOnMouseEntered(e -> profile_button.setStyle(HOVERED_BUTTON_STYLE));
      profile_button.setOnMouseExited(e -> profile_button.setStyle(IDLE_BUTTON_STYLE));

      logout_button.setStyle(IDLE_BUTTON_STYLE);
      logout_button.setTextFill(TEXT_FILL);
      logout_button.setOnMouseEntered(e -> logout_button.setStyle(HOVERED_BUTTON_STYLE));
      logout_button.setOnMouseExited(e -> logout_button.setStyle(IDLE_BUTTON_STYLE));

      browse_Button.setStyle(IDLE_BUTTON_STYLE);
      browse_Button.setTextFill(TEXT_FILL);
      browse_Button.setOnMouseEntered(e -> browse_Button.setStyle(HOVERED_BUTTON_STYLE));
      browse_Button.setOnMouseExited(e -> browse_Button.setStyle(IDLE_BUTTON_STYLE));

    } catch (IOException e) {
      e.printStackTrace();
    }

    // creates a list of movies to be displayed in the featured movie section
    ApiQuery apiQuery = new ApiQuery();
    featuredMovies = getFeaturedMovies(apiQuery.getAllCachedMovies(), FEATURED_MOVIE_LIST_SIZE);
    initializeFeatureMovieScreen();
    setFriends_scroll_space();
    setRequest_scroll_space();
    setEvents_scroll_space();
    setPendingEventsScrollSpace();
  }

  public void pressedLogoutButton(ActionEvent event) throws IOException {
    User.clearAttributes();
    OpeningScene openingScene = new OpeningScene(model);
  }

  public void pressedProfileButton(ActionEvent event) throws IOException {
    ProfileScene profileScene = new ProfileScene(model);
  }

  public void pressedBrowseButton(ActionEvent event) throws IOException {
    BrowseScene browseScene = new BrowseScene(model);
  }

  public void pressedCreateEventsButton(ActionEvent event) throws IOException {
    EventCreateScene eventCreateScene = new EventCreateScene(model);
  }

  public void pressedSearch(ActionEvent event) throws IOException {
    SearchScene searchScene = new SearchScene(model, movie_search.getText());
  }

  private void openFriendProfile(Model model, String friendName, Scene scene) {
    FriendProfileScene friendProfileScene = new FriendProfileScene(model, friendName, scene);
  }

  private void openEventScene(Model model, Event event) {
    EventScene eventScene = new EventScene(model, event);
  }

  public void pressedAddFriendButton(ActionEvent event) throws IOException {
    main_anchor_pane.setDisable(true);
    AddFriendScene addFriendScene = new AddFriendScene(model);
    main_anchor_pane.setDisable(false);

  }
  /**
   * Helper function to obtain a number of movies randomly from a larger list of movies
   *
   * @param cachedMovies: ArrayList<Movie>; the list of all cached movies
   * @param size: int; the number of random movies needed
   * @return a randomly generated list of movies
   */
  private ArrayList<Movie> getFeaturedMovies(ArrayList<Movie> cachedMovies, int size) {
    Collections.shuffle(cachedMovies);
    ArrayList<Movie> featuredList = new ArrayList<>();
    for (int i = 0; i < size; ++i) {
      featuredList.add(cachedMovies.get(i));
    }
    return featuredList;
  }

  /** Initializes elements for the featured movie section */
  private void initializeFeatureMovieScreen() {
    featured1_button.setOnAction(event -> featuredButtonOnClick(featured1_button));
    featured2_button.setOnAction(event -> featuredButtonOnClick(featured2_button));
    featured3_button.setOnAction(event -> featuredButtonOnClick(featured3_button));
    featured4_button.setOnAction(event -> featuredButtonOnClick(featured4_button));
    Movie movie = featuredMovies.get(0);
    setElements(movie);
  }

  /**
   * helper function that gets fired on a click of a button
   *
   * @param button: button element
   */
  private void featuredButtonOnClick(Button button) {
    Movie movie =
        featuredMovies.get(
            Character.getNumericValue(button.getId().charAt(FEATURED_TEXT_LENGTH)) - 1);
    setElements(movie);
  }

  /**
   * helper function to set up the image view and text area for a movie
   *
   * @param movie: Movie; the movie that needs to be displayed
   */
  private void setElements(Movie movie) {

    featuredMovie.setOnAction(event -> {
      altMovieScene movieScene = new altMovieScene(model, movie, model.stage.getScene());
    });
    Image image =
        new Image(
            movie.getMoviePosterUrl(),
            featuredMovie.getWidth(),
            featuredMovie.getHeight(),
            false,
            false);
    ImageView movie_Poster = new ImageView();
    movie_Poster.setImage(image);
    featuredMovie.setGraphic(movie_Poster);

    String text =
        movie.getMovieName()
            + "\n"
            + movie.getMoviePlot()
            + "\n"
            + "Director(s): "
            + movie.getMovieDirector()
            + "\n"
            + "Actors: "
            + movie.getMovieActor().toString().replace("[", "").replace("]", "");
    featured_Text_Area.setText(text);
    featured_Text_Area.setEditable(false);
  }

  private void setFriends_scroll_space(){
    friends_scroll_space.getChildren().clear();
    ArrayList<String> confirmedFriends = User.getFriendList().confirmedFriends;
    for (String friend : confirmedFriends ) {
      Hyperlink hyperlink = new Hyperlink(friend);
      hyperlink.setOnAction(e -> openFriendProfile(model, friend, model.stage.getScene()));
      friends_scroll_space.getChildren().add(hyperlink);
    }
  }

  private void setEvents_scroll_space(){
    events_scroll_space.getChildren().clear();
    ArrayList<Event> confirmedEvents = User.getEventList().confirmedEvents;
    for (Event event : confirmedEvents) {
      Hyperlink hyperlink = new Hyperlink(event.getEventName());
      hyperlink.setOnAction(e -> openEventScene(model, event));
      events_scroll_space.getChildren().add(hyperlink);
    }

  }

  private void setRequest_scroll_space(){
    request_scroll_space.getChildren().clear();
    ArrayList<String> requests = User.getFriendList().friendInvites;
    for (String friend : requests ) {
      HBox friendRequestBox = new HBox();
      Hyperlink friendLink = new Hyperlink(friend);
      friendLink.setOnAction(e -> openFriendProfile(model, friend, model.stage.getScene()));
      Button acceptButton = new Button("✓");
      acceptButton.setPadding(new Insets(0, 0, 0, 0));
      acceptButton.setOnAction(event -> onFriendRequestAccept(acceptButton, friend));
      Button rejectButton = new Button("✗");
      rejectButton.setPadding(new Insets(0, 0, 0, 0));
      rejectButton.setOnAction(event -> onFriendRequestReject(rejectButton, friend));
      friendRequestBox.getChildren().addAll(friendLink, acceptButton, rejectButton);
      request_scroll_space.getChildren().add(friendRequestBox);
    }
  }

  private void setPendingEventsScrollSpace(){
    pending_events_scroll.getChildren().clear();
    ArrayList<Event> pending = User.getEventList().eventInvites;
    for(Event pEvent : pending){
      HBox pendingEventBox = new HBox();

      Hyperlink eventName = new Hyperlink();
      eventName.setText(pEvent.getEventName());

      Button acceptButton = new Button("✓");
      acceptButton.setPadding(new Insets(0, 0, 0, 0));
      acceptButton.setOnAction(event -> onEventRequestAccept(acceptButton, pEvent));
      Button rejectButton = new Button("✗");
      rejectButton.setPadding(new Insets(0, 0, 0, 0));
      rejectButton.setOnAction(event -> onEventRequestReject(rejectButton, pEvent));
      pendingEventBox.getChildren().addAll(eventName, acceptButton, rejectButton);
      pending_events_scroll.getChildren().add(pendingEventBox);
    }
  }

  private void onFriendRequestAccept(Button button, String userName){
    User.getFriendList().acceptInvite(userName);
    Server.acceptFriendRequest(userName, User.getUserName());
    setRequest_scroll_space();
    setFriends_scroll_space();
  }

  private void onFriendRequestReject(Button button, String userName){
    User.getFriendList().rejectInvite(userName);
    Server.removeFriendRequest(userName, User.getUserName());
    setRequest_scroll_space();
  }

  private void onEventRequestAccept(Button button, Event event){
    User.getEventList().acceptInvite(event);
    Server.acceptEventInvite(User.getUserName(),event.getEventID());
    setEvents_scroll_space();
    setPendingEventsScrollSpace();
  }

  private void onEventRequestReject(Button button, Event event){
    User.getEventList().rejectInvite(event);
    Server.removeEventInvite(User.getUserName(), event.getEventID());
    setEvents_scroll_space();
    setPendingEventsScrollSpace();
  }

}
