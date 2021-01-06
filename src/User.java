import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import jdk.jfr.Frequency;

public class User{

  private static String userName = "";

  private static String first_name;

  private static String last_name;

  private static ArrayList<String> streamingServices = new ArrayList<>();

  private static FriendList friendList = new FriendList();

  private static MovieList movieList = new MovieList();

  private static EventList eventList = new EventList();

  private static String profile_pic = "";

  public User(){

  }

  /**
   *
   * @param attributes:
   */
  public static void initialize(Map<String, Object> attributes){
    User.userName = userName;
    if(attributes.containsKey("username")){
      userName = attributes.get("username").toString();
    }
    if(attributes.containsKey("first_name")){
      first_name = attributes.get("first_name").toString();
    }
    if(attributes.containsKey("last_name")){
      last_name = attributes.get("last_name").toString();
    }
    if(attributes.containsKey("pic")){
      profile_pic = attributes.get("pic").toString();
    }

    updateFriends();
    updateFriendRequests();
    updatePendingRequests();
    updateEvents();
    updateEventInvites();
  }

  public static String getUserName() {
    return User.userName;
  }

  public static void setUserName(String userName) {
    User.userName = userName;
  }

  public static void addStream(String streamName){
    User.streamingServices.add(streamName);
  }

  public static boolean removeStream(String streamName){
    return User.streamingServices.remove(streamName);


  }

  public static ArrayList<String> getStreamingServices(){
    return User.streamingServices;
  }

  public static FriendList getFriendList() {
    return friendList;
  }

  public static MovieList getMovieList() {
    return movieList;
  }

  public static EventList getEventList() {
    return eventList;
  }

  public static void updateFriends(){
    Object[] friendsNames = Server.getAllFriends(userName).keySet().toArray();
    for (Object name : friendsNames) {
      friendList.addFriend(name.toString());
    }
  }

  public static void updateFriendRequests(){
    Object[] friendRequests = Server.getFriendRequests(userName).keySet().toArray();
    for (Object name : friendRequests) {
      friendList.addInvitation(name.toString());
    }
  }

  public static void updatePendingRequests(){
    Object[] pendingRequests = Server.getPendingRequests(userName).keySet().toArray();
    for (Object name : pendingRequests) {
      friendList.addPending((name.toString()));
    }
  }

  public static void updateEvents(){
    HashMap<String, Map<String, Object>> events = Server.getUsersEvents(userName);
    Object[] eventIDs = events.keySet().toArray();
    for (Object id : eventIDs) {
      if (!eventList.containsEvent(id.toString())) {
        Event newEvent = new Event(
            events.get(id.toString()).get("eventName").toString(),
            events.get(id.toString()).get("location").toString(),
            events.get(id.toString()).get("date").toString(),
            events.get(id.toString()).get("organizer").toString(),
            events.get(id.toString()).get("id").toString());

          Set<String> movies = Server.getEventMovies(id.toString()).keySet();
          for (String moviename : movies) {
              newEvent.addMovie(moviename);
          }

        eventList.addEvent(newEvent);

      }
      else{
        Event event = eventList.getEvent(id.toString());
        Set<String> movies = Server.getEventMovies(id.toString()).keySet();
        ArrayList<String> existingMovies = event.getEventMovies();
        for (String moviename : movies) {
          if(!existingMovies.contains(moviename)){
            event.addMovie(moviename);
          }
        }
      }
    }

    events = Server.getUsersOrganizingEvents(userName);
    eventIDs = events.keySet().toArray();
    for (Object id : eventIDs) {
      if (!eventList.containsEvent(id.toString())) {
        Event newEvent = new Event(
            events.get(id.toString()).get("eventName").toString(),
            events.get(id.toString()).get("location").toString(),
            events.get(id.toString()).get("date").toString(),
            events.get(id.toString()).get("organizer").toString(),
            events.get(id.toString()).get("id").toString());

        Set<String> movies = Server.getEventMovies(id.toString()).keySet();
        for (String moviename : movies) {
          newEvent.addMovie(moviename);
        }

        eventList.addEvent(newEvent);

      }
      else{
        Event event = eventList.getEvent(id.toString());
        Set<String> movies = Server.getEventMovies(id.toString()).keySet();
        ArrayList<String> existingMovies = event.getEventMovies();
        for (String moviename : movies) {
          if(!existingMovies.contains(moviename)){
            event.addMovie(moviename);
          }
        }
      }
    }
  }

  public static void updateEventInvites(){
    HashMap<String, Map<String, Object>> events = Server.getUsersPendingInvites(userName);
    Object[] eventIDs = events.keySet().toArray();
    for (Object id : eventIDs) {
      eventList.addInvitation(new Event(events.get(id.toString()).get("eventName").toString(),
          events.get(id.toString()).get("location").toString(),
          events.get(id.toString()).get("date").toString(),
          events.get(id.toString()).get("organizer").toString(),
          events.get(id.toString()).get("id").toString()));
    }
  }

  public static String getProfilePicURL(){
    return profile_pic;
  }

  public static void setProfile_pic(String key){
    Server.updateProfilePicURL(userName, key);
    profile_pic = key;
  }

  public static void clearAttributes(){
    userName = "";
    first_name = "";
    last_name = "";
    friendList = new FriendList();
    eventList = new EventList();
    movieList = new MovieList();

  }

  public static void main(String[] args) {
    //User.initialize("soro");
    //FriendList f = User.getFriendList();
    //f.addFriend(userName);
    //f.addFriend(userName);
    //System.out.println(User.getFriendList().confirmedFriends.toString());
  }

}
