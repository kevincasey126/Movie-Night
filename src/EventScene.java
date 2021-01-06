import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class EventScene {
  private Model model;
  private Event thisEvent;
  private ApiQuery apiQuery;

  private static final String IDLE_BUTTON_STYLE = "-fx-background-color: #3892C7";
  private static final String HOVERED_BUTTON_STYLE = "-fx-background-color: #005BFF";
  private static final Paint TEXT_FILL = Color.web("#384BC7");

  @FXML
  private TextArea attendees_text, date_text, location_text, streaming_text;

  @FXML
  private Button yes_attending, no_attending, back_button, add_attendee_button;

  @FXML
  private ImageView profile_picture;

  @FXML
  private Label event_name;

  @FXML
  private GridPane vote_gridpane;

  @FXML
  private AnchorPane vote_anchorpane, main_event_anchorpane;

  private ArrayList<Button> voteButtonList;

  private ArrayList<Label> voteLabelList;

  public EventScene(Model newModel, Event newEvent) {
    model = newModel;
    thisEvent = newEvent;
    apiQuery = new ApiQuery();
    voteButtonList = new ArrayList<>();
    voteLabelList = new ArrayList<>();
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("EventScene.fxml"));
      loader.setController(this);
      model.stage.setScene(new Scene(loader.load()));
      model.stage.setTitle("MovieNight - EventScene");
      User.updateEvents();
      setUpFields();
      displayMovies();

      yes_attending.setStyle(IDLE_BUTTON_STYLE);
      yes_attending.setTextFill(TEXT_FILL);
      yes_attending.setOnMouseEntered(e -> yes_attending.setStyle(HOVERED_BUTTON_STYLE));
      yes_attending.setOnMouseExited(e -> yes_attending.setStyle(IDLE_BUTTON_STYLE));

      no_attending.setStyle(IDLE_BUTTON_STYLE);
      no_attending.setTextFill(TEXT_FILL);
      no_attending.setOnMouseEntered(e -> no_attending.setStyle(HOVERED_BUTTON_STYLE));
      no_attending.setOnMouseExited(e -> no_attending.setStyle(IDLE_BUTTON_STYLE));

      back_button.setStyle(IDLE_BUTTON_STYLE);
      back_button.setTextFill(TEXT_FILL);
      back_button.setOnMouseEntered(e -> back_button.setStyle(HOVERED_BUTTON_STYLE));
      back_button.setOnMouseExited(e -> back_button.setStyle(IDLE_BUTTON_STYLE));

      add_attendee_button.setStyle(IDLE_BUTTON_STYLE);
      add_attendee_button.setTextFill(TEXT_FILL);
      add_attendee_button.setOnMouseEntered(e -> add_attendee_button.setStyle(HOVERED_BUTTON_STYLE));
      add_attendee_button.setOnMouseExited(e -> add_attendee_button.setStyle(IDLE_BUTTON_STYLE));

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Set up all the UI elements in the scene
   *
   */
  private void setUpFields() {
    event_name.setText(thisEvent.getEventName());
    StringBuilder guestText = new StringBuilder();
    for (String guestID : getAttendeesFromServer()) {
      guestText.append(guestID).append("\n");
    }
    attendees_text.setText(guestText.toString());
    attendees_text.setEditable(false);
    date_text.setText(thisEvent.getEventDate());
    date_text.setEditable(false);
    location_text.setText(thisEvent.getEventLocation());
    location_text.setEditable(false);
    StringBuilder serviceText = new StringBuilder();
    for (String service : thisEvent.getEventStreamingServices()) {
      serviceText.append(service).append("\n");
    }
    streaming_text.setText(serviceText.toString());
    streaming_text.setEditable(false);
    profile_picture.setImage(ProfilePicture.getProfilePic(thisEvent.getEventOrganizer()));
    if (!thisEvent.getEventOrganizer().equals(User.getUserName())) {
      add_attendee_button.setDisable(true);
    }
    if (getAttendeesFromServer().contains(User.getUserName())) {
      yes_attending.setDisable(true);
    }
  }

  public void pressedInviteButton(ActionEvent e) throws IOException {
    main_event_anchorpane.setDisable(true);
    InviteToEventScene inviteToEventScene = new InviteToEventScene(model, thisEvent);
    main_event_anchorpane.setDisable(false);
  }


  public void pressedYesAttending(ActionEvent e) throws IOException {
    Server.acceptEventInvite(User.getUserName(), thisEvent.getEventID());
    User.getEventList().acceptInvite(thisEvent);
    yes_attending.setDisable(true);
  }

  /**
   * If you press no, if you're the organizer, delete the event, if you're invited, reject the invite,
   * and if you're an attendee, remove the event from your list
   * @param e IDK FXML stuff
   * @throws IOException ditto
   */
  public void pressedNoAttending(ActionEvent e) throws IOException {
    if (User.getUserName().equals(thisEvent.getEventOrganizer())) {
      Server.removeEvent(thisEvent.getEventID());
      User.getEventList().removeEvent(thisEvent.getEventID());
    } else if (Server.getEventInvitedAttendees(thisEvent.getEventID()).containsKey(User.getUserName())) {
      Server.removeEventInvite(User.getUserName(), thisEvent.getEventID());
      User.getEventList().rejectInvite(thisEvent);
    } else if (Server.getEventInvitedAttendees(thisEvent.getEventID()).containsKey(User.getUserName())) {
      Server.removeAttendingEvent(User.getUserName(), thisEvent.getEventID());
      User.getEventList().removeEvent(thisEvent.getEventID());
    }
    MainScene mainScene = new MainScene(model);
  }

  public void pressedBackButton(ActionEvent e) throws IOException {
    MainScene mainScene = new MainScene(model);
  }

  /**
   * Set up the movies in the grid
   */
  public void displayMovies() {
    String userVote = Server.getUsersVotedMovie(User.getUserName(), thisEvent.getEventID());

    HashMap<String, Map<String, Object>> movieVotes = Server.getEventMovies(thisEvent.getEventID());
    ArrayList<String> movieTitlesList = new ArrayList<>();
    movieVotes.forEach((movieTitle, votes) -> {movieTitlesList.add(movieTitle);});

    int moviesShown = 0;
    for (int rowIndex = 0; rowIndex < (movieVotes.size() / 2) + 1; rowIndex++) {
      for (int columnIndex = 0; columnIndex < 2; columnIndex++) {
        if (moviesShown < movieVotes.size()) {
          Movie movie = apiQuery.getMovie(movieTitlesList.get(rowIndex * 2 + columnIndex));
          VBox vBox = createVotingBox(movie, userVote);
          vote_gridpane.add(vBox, columnIndex, rowIndex);
          //TODO Create a button to bring you to movie page

          GridPane.setHalignment(vBox, HPos.CENTER);
        }
        moviesShown = moviesShown + 1;

      }
    }
    vote_anchorpane.setMinSize(vote_anchorpane.getPrefWidth(), ((2 + (float) movieVotes.size()) / 2 * 155));
    vote_anchorpane.setMaxSize(vote_anchorpane.getPrefWidth(), ((2 + (float) movieVotes.size()) / 2 * 155));
  }

  /**
   * Create the VBox
   *
   * @param movie The movie to create the VBox for
   * @return returns the VBox with the movie picture, vote button and vote total
   */
  private VBox createVotingBox(Movie movie, String userVote) {
    Image image;
    ImageView movieImage = new ImageView();
    try {
      image =
          new Image(movie.getMoviePosterUrl(), 92, 142, false, false);
      movieImage = new ImageView(image);
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
        image = new Image(inputstream, 92, 142, false, false);
        movieImage = new ImageView(image);
      } catch (FileNotFoundException fnfe) {
        image = null;
      }
    }

    VBox vBox = new VBox();
    vBox.getChildren().add(movieImage);

    Button voteButton = new Button("Vote");
    voteButtonList.add(voteButton);
    if (!userVote.equals("no")) {
      voteButton.setDisable(true);
    } else {
      voteButton.setOnAction(
          e -> {
            pressedVoteButton(movie);
          });
    }
    Label voteLabel = new Label();
    voteLabelList.add(voteLabel);
    String votes =
        Server.getEventMovies(thisEvent.getEventID())
            .get(movie.getMovieName())
            .get("vote")
            .toString();
    voteLabel.setText("Votes: " + votes);

    HBox hBox = new HBox();
    hBox.getChildren().addAll(voteButton, voteLabel);

    vBox.getChildren().add(hBox);

    return vBox;
  }

  private void pressedVoteButton(Movie movie) {
    Server.userVoted(User.getUserName(), thisEvent.getEventID(), movie.getMovieName());
    //Update labels/buttons
    for (Button button : voteButtonList) {
      button.setDisable(true);
    }
    for (Label label : voteLabelList) {
      label.setText(Server.getEventMovies(thisEvent.getEventID()).get(movie.getMovieName()).get("vote").toString());
    }
  }

  private ArrayList<String> getAttendeesFromServer() {
    HashMap<String, Map<String, Object>> attendeeList = Server.getEventAttendees(thisEvent.getEventID());
    ArrayList<String> nameList = new ArrayList<>();
    attendeeList.forEach((k, v) -> { nameList.add(k); });
    return nameList;
  }

}
