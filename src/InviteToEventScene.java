import java.io.IOException;
import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

public class InviteToEventScene {
  private Model model;
  private Event thisEvent;
  private Stage eventStage;
  private String sentRequestName;
  private static final String IDLE_BUTTON_STYLE = "-fx-background-color: #3892C7";
  private static final String HOVERED_BUTTON_STYLE = "-fx-background-color: #005BFF";
  private static final Paint TEXT_FILL = Color.web("#384BC7");

  @FXML
  private Button back_button;

  @FXML
  private TextField username_textfield;

  @FXML
  private Label response_label;

  public InviteToEventScene(Model newModel, Event event) {
    model = newModel;
    thisEvent = event;

    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("InviteToEventScene.fxml"));

      loader.setController(this);

      eventStage = new Stage();

      eventStage.setScene(new Scene(loader.load()));
      eventStage.setTitle("MovieNight - InviteToEventScene");
      eventStage.showAndWait();

      back_button.setStyle(IDLE_BUTTON_STYLE);
      back_button.setTextFill(TEXT_FILL);
      back_button.setOnMouseEntered(e -> back_button.setStyle(HOVERED_BUTTON_STYLE));
      back_button.setOnMouseExited(e -> back_button.setStyle(IDLE_BUTTON_STYLE));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void pressedBackButton(ActionEvent e) throws IOException {
    eventStage.close();
  }

  public void sendEventRequest(ActionEvent e) throws IOException {
    sentRequestName = username_textfield.getText();

    if (Server.checkUsernameAvailability(sentRequestName)) {
      response_label.setText("User does not exist");
    } else {
      if (!checkFriendList(sentRequestName)) {
        response_label.setText("You are not friends with this user");
      } else {
        if (Server.getEventAttendees(thisEvent.getEventID()).containsKey(sentRequestName)) {
          response_label.setText("User is already attending this event");
        } else {
          if (Server.getEventInvitedAttendees(thisEvent.getEventID()).containsKey(sentRequestName)) {
            response_label.setText("User is already invited to this event");
          } else {
            Server.sendEventInvite(sentRequestName, thisEvent.getEventID());
            response_label.setText("Event Invite Sent");
          }
        }
      }
    }
  }

  /**
   * Checks the users friendlist if the sent name is in it, case insensitive
   * @param sentName The name to check
   * @return True if the name is on the friendlist, else false
   */
  private Boolean checkFriendList(String sentName) {
    ArrayList<String> friendList = User.getFriendList().confirmedFriends;
    for (String friend : friendList) {
      if (friend.toLowerCase().equals(sentName.toLowerCase())) {
        return true;
      }
    }
    return false;
  }
}
