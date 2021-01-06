import java.util.ArrayList;
import javafx.stage.Stage;

public class Model {

  public User user;
  public ApiQuery apiQuery;
  private ArrayList<SceneModelListener> subscribers;
  public Stage stage;

  public Model() {
    apiQuery = new ApiQuery();
    subscribers = new ArrayList<>();
    stage = new Stage();
  }

  public void addSubscribers(SceneModelListener sub) { subscribers.add(sub); }

  public void notifySubscribers() { subscribers.forEach(sub -> sub.modelChanged()); }

}
