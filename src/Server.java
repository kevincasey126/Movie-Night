import org.neo4j.driver.*;
import org.neo4j.driver.Record;

import java.util.HashMap;
import java.util.Map;

import static org.neo4j.driver.Values.parameters;

public class Server {

  /**
   * To Do list:
   *  -user's profile pictures, set and get
   */

  //Driver to connect with the database server
  public static Driver driver;

  /**
   * This is a faked static class, should not be instanced
   */
  public Server(){}

  /**
   * connect to the database with a proper connection
   * @param uri localhost or the server IP
   * @param user the username of the server
   * @param password the password for the server
   */
  public static void connectServer(String uri, String user, String password){
    driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
  }

  /**
   * remove all nodes with the exception of the source node
   */
  public static void removeAll(){
    try(Session session = driver.session()){
      session.run("MATCH (n) DETACH DELETE n");
    }
  }

  /**
   * this function checks all usernames as to not create duplicates, only needed when a new user is created
   * @param username the name of the new user
   * @return true if the username is available, false otherwise
   */
  public static boolean checkUsernameAvailability(String username) {
    try (Session session = driver.session()) {
      Result result = session.run("MATCH (a:Person) WHERE toLower(a.username)=$x1 RETURN a",
          parameters("x1", username.toLowerCase()));
      if(result.hasNext()){
        return false;
      }
      return true;
    }
  }

  /**
   * Logs in with username and password, password will be encrypted at some point in the future
   * @param username: person's username
   * @param password: person's password
   * @return returns a custom class for a tuple that contains whether the query was a success,
   * and if it was all the attributes along side it
   */
  public static LogInReturn attemptLogIn(String username, String password){
      try(Session session = driver.session()){
        Result result = session.run("MATCH (a:Person) WHERE toLower(a.username)=$x1" +
            " RETURN properties(a)",
          parameters("x1",username.toLowerCase()));
        if(result.hasNext()){
          Map<String, Object> returnable = result.next().get("properties(a)").asMap();

          if(PasswordHashing.checkHashedPassword(password, (byte[])returnable.get("password"),
            (byte[])returnable.get("salt"))) {
            return new LogInReturn(true, returnable);
          }
          else{
            return new LogInReturn(false, null);
          }

        }
      }
      return new LogInReturn(false, null);
    }

  //Person functions

  /**
   * this function returns all the attributes excluding passwords
   * @param username the username to get the attributes from
   * @return all the user's attributes in a map with the key as the attribute's name and the
   * value as the attribute's value
   */
  public static Map<String, Object> getAttributes(String username){
    try(Session session = driver.session()){
      Result result = session.run("MATCH (a:Person) WHERE toLower(a.username)=$x1" +
              " RETURN properties(a)",
          parameters("x1",username.toLowerCase()));
      if(result.hasNext()){
        //find a way to remove the password, or never query it at all
        return result.next().get("properties(a)").asMap();

      }
    }
    return null;
  }

  /**
   * Add new node into the database, password needs to be encrypted
   * @param username: the name of the new person, other features will be added later
   * @return true if completed, false otherwise
   */
  public static boolean addPerson(String username, String password, String first_name,
                  String last_name) {
    if (checkUsernameAvailability(username)) {

      Map<String, byte[]> hashingInfo = PasswordHashing.hashNewPassword(password);

      try (Session session = driver.session()) {
        session.writeTransaction(transaction -> transaction.run("MERGE (a:Person " +
            "{username:$x1, password:$x2, first_name:$x3, last_name:$x4, " +
            "pic: \"default_profile_pic.png\"," +
            "salt:$x5})",
          parameters("x1", username, "x2", hashingInfo.get("hashedPassword"),
            "x3", first_name, "x4", last_name,  "x5", hashingInfo.get("salt"))));
        return true;
      }
    }
    else{
      System.out.println("this name is already taken");
      return false;
    }
  }

  /**
   * remove a node from the database using their name
   * @param username: name of the person to remove, will use different features in the future
   */
  public static void removePerson(String username){
    try(Session session = driver.session()){
      session.writeTransaction(transaction -> transaction.run(
          "MATCH (a:Person) WHERE toLower(a.username)=$x DETACH DELETE a",
          parameters("x", username.toLowerCase())));
    }
  }

  /**
   * get all the events a user is attending, not including the ones they organized
   * @param username the user to search for their events
   * @return a hash map with the event id as the first string, then the internal map with all the attributes
   */
  public static HashMap<String, Map<String, Object>> getUsersEvents(String username) {
    HashMap<String, Map<String, Object>> events = new HashMap<>();

    try (Session session = driver.session()) {
      Result attending = session.run("Match (a:Person)-[:ATTENDINGEVENT]->(b:Event) " +
          "WHERE toLower(a.username)=$x1 Return properties(b) ", parameters("x1", username.toLowerCase()));
      while (attending.hasNext()) {
        Record attendRecord = attending.next();
        Map<String, Object> attend = attendRecord.get("properties(b)").asMap();
        events.put(attendRecord.get("properties(b)").get("id").asString(),
            attend);
      }
    }
    return events;
  }

  /**
   * get all the events a user is organizing, not including the ones they are just attending
   * @param username the user to search for their events
   * @return a hash map with the event id as the first string, then the internal map with all the attributes
   */
  public static HashMap<String, Map<String, Object>> getUsersOrganizingEvents(String username) {
    HashMap<String, Map<String, Object>> events = new HashMap<>();

    try (Session session = driver.session()) {
      Result organizing = session.run("Match (a:Person)-[:EVENTORGANIZER]->(b:Event) " +
          "WHERE toLower(a.username)=$x1 Return properties(b) ", parameters("x1", username.toLowerCase()));
      while (organizing.hasNext()) {
        Record orgRecord = organizing.next();
        Map<String, Object> organize = orgRecord.get("properties(b)").asMap();
        events.put(orgRecord.get("properties(b)").get("id").asString(),
            organize);
      }
    }
    return events;
  }

  /**
   * get all of the current live invites for a user
   * @param username the user
   * @return a hash map with the event id as the first string, then the internal map with all the attributes
   */
  public static HashMap<String, Map<String, Object>> getUsersPendingInvites(String username){
    HashMap<String, Map<String, Object>> events = new HashMap<>();

    try (Session session = driver.session()) {
      Result organizing = session.run("Match (a:Person)-[:INVITEPENDING]->(b:Event) " +
        "WHERE toLower(a.username)=$x1 Return properties(b) ", parameters("x1", username.toLowerCase()));
      while (organizing.hasNext()) {
        Record orgRecord = organizing.next();
        Map<String, Object> organize = orgRecord.get("properties(b)").asMap();
        events.put(orgRecord.get("properties(b)").get("id").asString(),
          organize);
      }
    }
    return events;
  }

  /**
   * this function will update the key to the profile picture
   * @param username the user looking to update their profile pic
   * @param new_pic the new key for the S3 server to the new picture
   */
  public static void updateProfilePicURL(String username, String new_pic){
    try (Session session = driver.session()) {
      session.writeTransaction(transaction -> transaction.run("MATCH (a:Person) WHERE " +
          "toLower(a.username)=$x1 SET a.pic=$x2",
        parameters("x1", username.toLowerCase(), "x2", new_pic)));
    }
  }


  /**
   * will update the password without question under a new hash and salt, the user must first be prompted to verify
   * their password elsewhere in the program for this to be truly secure, even after a login
   * @param username the user looking to change their password
   * @param new_password the new password to save under the user
   */
  public static void updatePassword(String username, String new_password){
    try (Session session = driver.session()) {

      Map<String, byte[]> hashingInfo = PasswordHashing.hashNewPassword(new_password);
      System.out.println(hashingInfo.get("hashedPassword"));

      session.writeTransaction(transaction -> transaction.run("MATCH (a:Person) WHERE " +
          "toLower(a.username)=$x1 SET a += {password:$x2, salt:$x3}",
        parameters("x1", username.toLowerCase(), "x2", hashingInfo.get("hashedPassword"),
                  "x3", hashingInfo.get("salt"))));
    }
  }

  //streaming service functions

  /**
   * connects to a main service node to add to streaming services
   * @param newService the name of the new service to connect to
   * @param username the user to connect to the service
   */
  public static void addStreamingService(String newService, String username){
    try (Session session = driver.session()) {
      session.writeTransaction(transaction -> transaction.run("MERGE (a:StreamingService {name:toLower($x1)})",
        parameters("x1", newService)));
      session.writeTransaction(transaction -> transaction.run("MATCH (a:StreamingService), (b:Person) " +
        "WHERE toLower(a.name)=$x2 AND toLower(b.username)=$x1 " +
        "MERGE (b)-[:STREAMINGSERVICE]->(a) " +
        "RETURN a,b", parameters("x1", username.toLowerCase(), "x2", newService.toLowerCase())));
    }
  }

  /**
   * removes a specific streaming service from the user
   * @param removeService the service to remove
   * @param username the user to remove the service from
   */
  public static void removeStreamingService(String removeService, String username){
    try(Session session = driver.session()){
      session.writeTransaction(transaction -> transaction.run(
        "MATCH (a:Person)-[e:STREAMINGSERVICE]->(b:StreamingService) " +
          "WHERE toLower(a.username)=$x1 AND toLower(b.name)=$x2 " +
          "DELETE e",
        parameters("x1", username.toLowerCase(), "x2", removeService.toLowerCase())));
    }
  }

  /**
   * get a hashmap of all the user's streaming services
   * @param username the user to get the streaming services from
   * @return the streaming services
   */
  public static HashMap<String, Map<String, Object>> getUsersStreamingServices(String username){
    HashMap<String, Map<String, Object>> services = new HashMap<>();

    try(Session session = driver.session()){
      Result result = session.run("Match (a:Person)-[:STREAMINGSERVICE]->(b:StreamingService) " +
        "WHERE toLower(a.username)=$x1 Return properties(b) ", parameters("x1", username.toLowerCase()));
      while(result.hasNext()){
        Record record = result.next();
        Map<String, Object> service = record.get("properties(b)").asMap();
        services.put(record.get("properties(b)").get("name").asString(),
          service);
      }
    }
    System.out.println(services);
    return services;
  }

  //Favourite movie functions

  /**
   * adds a favourite movie to the user's profile, without creating duplicates
   * @param newMovie the title of the new movie
   * @param username the user looking to add a movie
   */
  public static void addFavouriteMovie(String newMovie, String username, String rating){
    try (Session session = driver.session()) {
      session.writeTransaction(transaction -> transaction.run("MERGE (a:Movie {name:$x1})",
        parameters("x1", newMovie)));
      session.writeTransaction(transaction -> transaction.run("MATCH (a:Movie), (b:Person) " +
        "WHERE toLower(a.name)=$x2 AND toLower(b.username)=$x1 " +
        "MERGE (b)-[f:FAVMOVIE]->(a) " +
        "SET f.rating = $x3 " +
        "RETURN a,b", parameters("x1", username.toLowerCase(), "x2", newMovie.toLowerCase(),
                                "x3", rating)));
    }
  }

  /**
   * removes a favourite movie from a user's profile, without removing the movie from the database
   * @param remove_movie the name of the movie to remove
   * @param username the user to remove the movie from
   */
  public static void removeFavouriteMovie(String remove_movie, String username){
    try(Session session = driver.session()){
      session.writeTransaction(transaction -> transaction.run(
        "MATCH (a:Person)-[e:FAVMOVIE]->(b:Movie) " +
          "WHERE toLower(a.username)=$x1 AND toLower(b.name)=$x2 " +
          "DELETE e",
        parameters("x1", username.toLowerCase(), "x2", remove_movie.toLowerCase())));
    }
  }

  /**
   * Gets a hashmap of all the user's favourite movies
   * @param username the user to get the movies from
   * @return the movies
   */
  public static HashMap<String, Map<String, Object>> getUsersFavouriteMovies(String username){
    HashMap<String, Map<String, Object>> movies = new HashMap<>();

    try(Session session = driver.session()){
      Result result = session.run("Match (a:Person)-[f:FAVMOVIE]->(b:Movie) " +
        "WHERE toLower(a.username)=$x1 Return properties(b), properties(f) ",
        parameters("x1", username.toLowerCase()));
      while(result.hasNext()){
        Record record = result.next();
        Map<String, Object> ratings = record.get("properties(f)").asMap();
        movies.put(record.get("properties(b)").get("name").asString(),
          ratings);
      }
    }
    return movies;
  }

//Want to watch movie functions

  /**
   * adds a movie the user wants to see to the user's profile, without creating duplicates
   * @param newMovie the title of the new movie
   * @param username the user looking to add a movie
   */
  public static void addWantToWatch(String newMovie, String username){
    try (Session session = driver.session()) {
      session.writeTransaction(transaction -> transaction.run("MERGE (a:Movie {name:$x1})",
        parameters("x1", newMovie)));
      session.writeTransaction(transaction -> transaction.run("MATCH (a:Movie), (b:Person) " +
        "WHERE toLower(a.name)=$x2 AND toLower(b.username)=$x1 " +
        "MERGE (b)-[:WANTTOWATCH]->(a) " +
        "RETURN a,b", parameters("x1", username.toLowerCase(), "x2", newMovie.toLowerCase())));
    }
  }

  /**
   * removes a wanted to see movie from a user's profile, without removing the movie from the database
   * @param remove_movie the name of the movie to remove
   * @param username the user to remove the movie from
   */
  public static void removeWantToWatch(String remove_movie, String username){
    try(Session session = driver.session()){
      session.writeTransaction(transaction -> transaction.run(
        "MATCH (a:Person)-[e:WANTTOWATCH]->(b:Movie) " +
          "WHERE toLower(a.username)=$x1 AND toLower(b.name)=$x2 " +
          "DELETE e",
        parameters("x1", username.toLowerCase(), "x2", remove_movie.toLowerCase())));
    }
  }

  /**
   * Gets a hashmap of all the user's movies they want to see
   * @param username the user to get the movies from
   * @return the movies
   */
  public static HashMap<String, Map<String, Object>> getUsersWantToWatchMovies(String username){
    HashMap<String, Map<String, Object>> movies = new HashMap<>();

    try(Session session = driver.session()){
      Result result = session.run("Match (a:Person)-[:WANTTOWATCH]->(b:Movie) " +
          "WHERE toLower(a.username)=$x1 Return properties(b)",
        parameters("x1", username.toLowerCase()));
      while(result.hasNext()){
        Record record = result.next();
        Map<String, Object> ratings = record.get("properties(b)").asMap();
        movies.put(record.get("properties(b)").get("name").asString(),
          ratings);
      }
    }
    return movies;
  }

  //Friendship functions
  /**
   * Create a two way friendship between two nodes
   * @param username1: name of the first new friend
   * @param username2: name of the second new friend
   */
  public static void createFriendship(String username1, String username2){
    try(Session session = driver.session()){
      session.writeTransaction(transaction -> transaction.run("MATCH(a:Person),(b:Person) " +
          "WHERE toLower(a.username)=$x1 and toLower(b.username)=$x2 " +
          "MERGE (a)-[f:FRIENDS]->(b) " +
          "RETURN a,b", parameters("x1", username1.toLowerCase(),
          "x2", username2.toLowerCase())));
      session.writeTransaction(transaction -> transaction.run("MATCH(a:Person),(b:Person) " +
          "WHERE toLower(a.username)=$x1 AND toLower(b.username)=$x2 " +
          "MERGE (a)-[f:FRIENDS]->(b) " +
          "RETURN a,b", parameters("x1", username2.toLowerCase(),
          "x2", username1.toLowerCase())));
    }
  }

  /**
   * Gets all of a user's friends, with all of their attributes
   * @param username the user to find their friends
   * @return this will return a hashmap with all the friend's usernames as the keys, and
   * their properties as the value map, with this keys as the attribute and value as the values
   */
  public static HashMap<String, Map<String, Object>> getAllFriends(String username){
    HashMap<String, Map<String, Object>> friends = new HashMap<>();
    try(Session session = driver.session()){
      Result result = session.run("Match (a:Person)-[:FRIENDS]->(b:Person) " +
          "WHERE a.username=$x1 Return properties(b) ", parameters("x1", username));
      while(result.hasNext()){
        Record record = result.next();
        Map<String, Object> friend = record.get("properties(b)").asMap();
        friends.put(record.get("properties(b)").get("username").asString(),
            friend);
      }
    }
    return friends;
  }

  /**
   * remove a friendship between two nodes, in both directions
   * @param username1: name of the first friend to remove
   * @param username2: name of the second friend to remove
   */
  public static void removeFriendship(String username1, String username2){
    try(Session session = driver.session()) {
      session.writeTransaction(transaction -> transaction.run("MATCH(a:Person),(b:Person) " +
              "WHERE a.username=$x1 AND b.username=$x2 " +
              "MATCH (a)-[f:FRIENDS]->(b) " +
              "DELETE f",
          parameters("x1", username1, "x2", username2)));
      session.writeTransaction(transaction -> transaction.run("MATCH(a:Person),(b:Person) " +
              "WHERE a.username=$x1 AND b.username=$x2 " +
              "MATCH (b)-[f:FRIENDS]->(a) " +
              "DELETE f",
          parameters("x1", username1, "x2", username2)));
    }
  }

  //Friend request functions
  /**
   * username1 sends a friend request to username2, with a pending relationship in the other
   * direction
   * @param sender the user sending the request
   * @param receiver the user getting the request
   */
  public static void sendFriendRequest(String sender, String receiver){
    try(Session session = driver.session()){
      session.writeTransaction(transaction -> transaction.run("MATCH(a:Person),(b:Person) " +
          "WHERE toLower(a.username)=$x1 and toLower(b.username)=$x2 " +
          "MERGE (a)-[f:FRIENDREQUEST]->(b) " +
          "RETURN a,b", parameters("x1", sender.toLowerCase(),
          "x2", receiver.toLowerCase())));
      session.writeTransaction(transaction -> transaction.run("MATCH(a:Person),(b:Person) " +
          "WHERE toLower(a.username)=$x1 AND toLower(b.username)=$x2 " +
          "MERGE (a)-[f:REQUESTPENDING]->(b) " +
          "RETURN a,b", parameters("x1", receiver.toLowerCase(),
          "x2", sender.toLowerCase())));
    }
  }

  /**
   * remove a friend request, either when accepted to upgrade to friendship or when denied
   * @param sender the user who sent the request
   * @param receiver the user who received the request
   */
  public static void removeFriendRequest(String sender, String receiver){
    try(Session session = driver.session()) {
      session.writeTransaction(transaction -> transaction.run("MATCH(a:Person),(b:Person) " +
              "WHERE toLower(a.username)=$x1 AND toLower(b.username)=$x2 " +
              "MATCH (a)-[r:FRIENDREQUEST]->(b) " +
              "DELETE r",
          parameters("x1", sender.toLowerCase(), "x2", receiver.toLowerCase())));
      session.writeTransaction(transaction -> transaction.run("MATCH(a:Person),(b:Person) " +
              "WHERE toLower(a.username)=$x1 AND toLower(b.username)=$x2 " +
              "MATCH (b)-[p:REQUESTPENDING]->(a) " +
              "DELETE p",
          parameters("x1", sender.toLowerCase(), "x2", receiver.toLowerCase())));
    }
  }

  /**
   * gets all of a user's requests, with all of their attributes
   * @param username the user to get their requests
   * @return this will return a hashmap with all the request's usernames as the keys, and
   * their properties as the value map, with this keys as the attribute and value as the values
   */
  public static HashMap<String, Map<String, Object>> getFriendRequests(String username){
    HashMap<String, Map<String, Object>> requests = new HashMap<>();
    try(Session session = driver.session()){
      Result result = session.run("Match (a:Person)<-[:FRIENDREQUEST]-(b:Person) " +
          "WHERE toLower(a.username)=$x1 Return properties(b) ", parameters("x1", username.toLowerCase()));
      while(result.hasNext()){
        Record record = result.next();
        Map<String, Object> friend = record.get("properties(b)").asMap();
        requests.put(record.get("properties(b)").get("username").asString(),
            friend);
      }
    }
    return requests;
  }

  /**
   * get the people this user has sent requests to
   * @param username the user to find the requests from
   * @return this will return a hashmap with all the pending request's usernames as the keys, and
   * their properties as the value map, with this keys as the attribute and value as the values
   */
  public static HashMap<String, Map<String, Object>> getPendingRequests(String username){
    HashMap<String, Map<String, Object>> requests = new HashMap<>();
    try(Session session = driver.session()){
      Result result = session.run("Match (a:Person)<-[:REQUESTPENDING]-(b:Person) " +
          "WHERE toLower(a.username)=$x1 Return properties(b) ", parameters("x1", username.toLowerCase()));
      while(result.hasNext()){
        Record record = result.next();
        Map<String, Object> friend = record.get("properties(b)").asMap();
        requests.put(record.get("properties(b)").get("username").asString(),
            friend);
      }
    }
    return requests;
  }

  /**
   * this function removes the friend request and upgrades it to a friendship
   * @param sender the user that sent the friend request
   * @param receiver the user that received the request
   */
  public static void acceptFriendRequest(String sender, String receiver){
    removeFriendRequest(sender, receiver);
    createFriendship(sender, receiver);
  }

  //Event id functions

  /**
   * this node gives a count of how many events there are, gives a specific id to every node
   */
  public static void createEventIdCounter(){
    try (Session session = driver.session()) {
      session.writeTransaction(transaction -> transaction.run("MERGE (a:EventCounter {count:0})"));
    }
  }

  /**
   * gets the next available event id
   * @return the string for the next available id
   */
  public static String getNextEventId() {
    try (Session session = driver.session()) {
      Result result = session.run("Match (a:EventCounter) Return properties(a)");
      while (result.hasNext()) {
        String eventCount = result.next().get("properties(a)").asMap().get("count").toString();
        return eventCount;
      }
    }
    return "";
  }

  /**
   * increases the value of the event id for a unique id
   */
  public static void increaseEventId(){
    try (Session session = driver.session()) {
      Result result = session.run("Match (a:EventCounter) Return properties(a)");
      int intCount = Integer.parseInt(result.next().get("properties(a)").asMap().get("count").toString()) + 1;
      session.writeTransaction(transaction -> transaction.run("Match (a:EventCounter) Set a.count=$x1",
          parameters("x1", Integer.toString(intCount))));
    }
  }

  //Event functions

  /**
   * creates a new event from a specific organizer
   * @param organizer the user that created this event
   * @param eventName the name of the event
   * @param location the location for the movie night
   * @param date the date of the event
   */
  public static String createEvent(String organizer, String eventName, String location, String date){
    String eventID = getNextEventId();
    increaseEventId();
    try (Session session = driver.session()) {
      session.writeTransaction(transaction -> transaction.run("MERGE (a:Event " +
              "{eventName:$x1, location:$x2, date:$x3, organizer:$x4, id:$x5})",
          parameters("x1", eventName, "x2", location, "x3", date, "x4", organizer,
              "x5", eventID)));
            session.writeTransaction(transaction -> transaction.run("MATCH(a:Person),(b:Event) " +
          "WHERE toLower(a.username)=$x1 and toLower(b.eventName)=$x2 " +
          "MERGE (a)-[:EVENTORGANIZER]->(b) " +
          "RETURN a,b", parameters("x1", organizer.toLowerCase(),
          "x2", eventName.toLowerCase())));
      session.writeTransaction(transaction -> transaction.run("MATCH(a:Person),(b:Event) " +
          "WHERE toLower(a.username)=$x1 and toLower(b.eventName)=$x2 " +
          "MERGE (b)-[e:EVENTATTENDEE]->(a) " +
          "SET e.voted='no'" +
          "RETURN a,b", parameters("x1", organizer.toLowerCase(),
          "x2", eventName.toLowerCase())));
    }
    return eventID;
  }

  /**
   * remove an event
   * @param eventId the event to remove
   */
  public static void removeEvent(String eventId){
    removeAllEventMovies(eventId);
    try(Session session = driver.session()){
      session.writeTransaction(transaction -> transaction.run(
        "MATCH (a:Event) WHERE a.id=$x1 DETACH DELETE a",
        parameters("x1", eventId)));
    }
  }

  /**
   * gets all of an event's attributes in a map
   * @param eventId the event id to collect the attributes from
   * @return the map of attributes
   */
  public static Map<String, Object> getEventAttributes(String eventId){
    try(Session session = driver.session()){
      Result result = session.run("MATCH (a:Event) WHERE a.id=$x1" +
              " RETURN properties(a)",
          parameters("x1",eventId));
      if(result.hasNext()){
        //find a way to remove the password, or never query it at all
        return result.next().get("properties(a)").asMap();

      }
    }
    return null;
  }

  /**
   * get all the attendees of an event
   * @param eventId the event id
   * @return a Hashmap with the first string as the user's username with a map of their attributes,
   * with that string being the attribute's name and the object is the value
   */
  public static HashMap<String, Map<String, Object>> getEventAttendees(String eventId){
      HashMap<String, Map<String, Object>> attendees = new HashMap<>();

      try(Session session = driver.session()){
        Result result = session.run("Match (a:Event)-[:EVENTATTENDEE]->(b:Person) " +
            "WHERE a.id=$x1 Return properties(b) ", parameters("x1", eventId));
        while(result.hasNext()){
          Record record = result.next();
          Map<String, Object> friend = record.get("properties(b)").asMap();
          attendees.put(record.get("properties(b)").get("username").asString(),
              friend);
        }
      }
      return attendees;
    }

  /**
   * get all the invited people to an event
   * @param eventId the event id
   * @return a Hashmap with the first string as the user's username with a map of their attributes,
   * with that string being the attribute's name and the object is the value
   */
  public static HashMap<String, Map<String, Object>> getEventInvitedAttendees(String eventId){
    HashMap<String, Map<String, Object>> attendees = new HashMap<>();

    try(Session session = driver.session()){
      Result result = session.run("Match (a:Event)-[:EVENTINVITE]->(b:Person) " +
        "WHERE a.id=$x1 Return properties(b) ", parameters("x1", eventId));
      while(result.hasNext()){
        Record record = result.next();
        Map<String, Object> friend = record.get("properties(b)").asMap();
        attendees.put(record.get("properties(b)").get("username").asString(),
          friend);
      }
    }
    return attendees;
  }

  //Event invite functions

  /**
   * send a user an invite to the event with the event id
   * @param username the user to invite
   * @param eventId the id of the event
   */
  public static void sendEventInvite(String username, String eventId){
    try(Session session = driver.session()){
      session.writeTransaction(transaction -> transaction.run("MATCH(a:Person),(b:Event) " +
          "WHERE toLower(a.username)=$x1 and b.id=$x2 " +
          "MERGE (a)-[f:INVITEPENDING]->(b) " +
          "RETURN a,b", parameters("x1", username.toLowerCase(), "x2", eventId)));
      session.writeTransaction(transaction -> transaction.run("MATCH(a:Event),(b:Person) " +
          "WHERE a.id=$x1 AND toLower(b.username)=$x2 " +
          "MERGE (a)-[f:EVENTINVITE]->(b) " +
          "RETURN a,b", parameters("x1", eventId, "x2", username.toLowerCase())));
    }
  }

  /**
   * confirms an event invite by updating the relationships to be attendee
   * @param username the user accepting the invite
   * @param eventId the event the user will be attending
   */
  public static void acceptEventInvite(String username, String eventId){
    removeEventInvite(username, eventId);
    try(Session session = driver.session()){
      session.writeTransaction(transaction -> transaction.run("MATCH(a:Person),(b:Event) " +
        "WHERE toLower(a.username)=$x1 and b.id=$x2 " +
        "MERGE (a)-[f:ATTENDINGEVENT]->(b) " +
        "RETURN a,b", parameters("x1", username.toLowerCase(), "x2", eventId)));
      session.writeTransaction(transaction -> transaction.run("MATCH(a:Event),(b:Person) " +
        "WHERE a.id=$x1 AND toLower(b.username)=$x2 " +
        "MERGE (a)-[e:EVENTATTENDEE]->(b) " +
        "SET e.voted = 'no' " +
        "RETURN a,b", parameters("x1", eventId, "x2", username.toLowerCase())));
    }
  }

  /**
   * remove an event invite between the given user and the event id
   * @param username the username of the user
   * @param eventId the id of the event
   */
  public static void removeEventInvite(String username, String eventId){
    try(Session session = driver.session()) {
      session.writeTransaction(transaction -> transaction.run("MATCH(a:Person),(b:Event) " +
          "WHERE toLower(a.username)=$x1 AND b.id=$x2 " +
          "MATCH (a)-[r:INVITEPENDING]->(b) " +
          "DELETE r",
        parameters("x1", username.toLowerCase(), "x2", eventId)));
      session.writeTransaction(transaction -> transaction.run("MATCH(a:Person),(b:Event) " +
          "WHERE toLower(a.username)=$x1 AND b.id=$x2 " +
          "MATCH (b)-[p:EVENTINVITE]->(a) " +
          "DELETE p",
        parameters("x1", username.toLowerCase(), "x2", eventId)));
    }
  }

  /**
   * remove an event the user is currently attending
   * @param username the user leaving the event
   * @param eventId the event id
   */
  public static void removeAttendingEvent(String username, String eventId){
    try(Session session = driver.session()) {
      session.writeTransaction(transaction -> transaction.run("MATCH(a:Person),(b:Event) " +
          "WHERE toLower(a.username)=$x1 AND b.id=$x2 " +
          "MATCH (a)-[r:ATTENDINGEVENT]->(b) " +
          "DELETE r",
        parameters("x1", username.toLowerCase(), "x2", eventId)));
      session.writeTransaction(transaction -> transaction.run("MATCH(a:Person),(b:Event) " +
          "WHERE toLower(a.username)=$x1 AND b.id=$x2 " +
          "MATCH (b)-[p:EVENTATTENDEE]->(a) " +
          "DELETE p",
        parameters("x1", username.toLowerCase(), "x2", eventId)));
    }
  }

  //Set event movies

  /**
   * adds a movie to the runnings for a movie event
   * @param movieTitle the title of the movie
   * @param eventId the event to add the movie to
   */
  public static void addEventMovie(String movieTitle, String eventId){
    try (Session session = driver.session()) {
      session.writeTransaction(transaction -> transaction.run("MERGE (a:EventMovie {name:$x1, vote:0, eventId:$x2})",
        parameters("x1", movieTitle, "x2", eventId)));
      session.writeTransaction(transaction -> transaction.run("MATCH (a:EventMovie), (b:Event) " +
        "WHERE toLower(a.name)=$x1 AND a.eventId=$x2 AND b.id=$x2 " +
        "MERGE (b)-[:EVENTMOVIE]->(a) " +
        "RETURN a,b", parameters("x1", movieTitle.toLowerCase(), "x2", eventId)));
    }
  }

  /**
   * add a single vote to one of the movies of a movie event
   * @param movieTitle the movie that got the vote
   * @param eventId the event id where someone voted
   */
  public static void addMovieVote(String movieTitle, String eventId){
    try (Session session = driver.session()) {
      Result result = session.run("Match (a:Event)-[m:EVENTMOVIE]->(b:EventMovie) " +
        "Where a.id=$x1 and toLower(b.name)=$x2 AND b.eventId=$x1  " +
        "Return properties(b)", parameters("x1", eventId, "x2", movieTitle.toLowerCase()));
      int intCount = Integer.parseInt(result.next().get("properties(b)").asMap().get("vote").toString()) + 1;
      session.writeTransaction(transaction -> transaction.run("Match (a:Event)-[m:EVENTMOVIE]->(b:EventMovie) " +
          "Where a.id=$x1 and toLower(b.name)=$x2 AND b.eventId=$x1 SET b.vote=$x3",
        parameters("x1", eventId, "x2", movieTitle.toLowerCase(), "x3", Integer.toString(intCount))));
    }
  }

  /**
   * remove a single vote from one of the movies
   * @param movieTitle the movie that got the removal
   * @param eventId the event id where someone removed a voted
   */
  public static void reduceMovieVote(String movieTitle, String eventId){
    try (Session session = driver.session()) {
      Result result = session.run("Match (a:Event)-[m:EVENTMOVIE]->(b:EventMovie) " +
        "Where a.id=$x1 and toLower(b.name)=$x2 AND b.eventId=$x1  " +
        "Return properties(b)", parameters("x1", eventId, "x2", movieTitle.toLowerCase()));
      int intCount = Integer.parseInt(result.next().get("properties(b)").asMap().get("vote").toString()) - 1;
      int vote;
      if(intCount < 0){
        vote = 0;
      }
      else{
        vote = intCount;
      }
      session.writeTransaction(transaction -> transaction.run("Match (a:Event)-[m:EVENTMOVIE]->(b:EventMovie) " +
          "Where a.id=$x1 and toLower(b.name)=$x2 AND b.eventId=$x1 Set b.vote=$x3",
        parameters("x1", eventId, "x2", movieTitle.toLowerCase(), "x3", Integer.toString(vote))));
    }
  }

  /**
   * this is called when the user votes for a new movie, as it will set which movie the user voted for and add a
   * vote into the event movie
   * @param username the user doing the voting
   * @param eventID the event
   * @param movieTitle the movie to vote for
   */
  public static void userVoted(String username, String eventID, String movieTitle){
    try(Session session = driver.session()){
      session.writeTransaction(transaction -> transaction.run("MATCH (a:Person)<-[e:EVENTATTENDEE]-(b:Event) " +
        "WHERE toLower(a.username)=$x1 AND b.id=$x2 " +
        "SET e.voted=$x3", parameters("x1", username.toLowerCase(), "x2", eventID, "x3", movieTitle)));
    }
    addMovieVote(movieTitle, eventID);
  }

  /**
   * the user revokes their vote on a specific movie in an event, while removing the vote from the event
   * @param username the user going back on their vote
   * @param eventID the event they are removing the event from
   * @param movieTitle the movie to get unvoted
   */
  public static void userRemovedVote(String username, String eventID, String movieTitle){
    try(Session session = driver.session()){
      session.writeTransaction(transaction -> transaction.run("MATCH (a:Person)<-[e:EVENTATTENDEE]-(b:Event) " +
        "WHERE toLower(a.username)=$x1 AND b.id=$x2 " +
        "SET e.voted='no'", parameters("x1", username.toLowerCase(), "x2", eventID)));
    }
    reduceMovieVote(movieTitle, eventID);
  }

  /**
   * get the movie the user voted for in an event
   * @param username the user
   * @param eventID the specific event to get the vote from
   * @return either the movie title as a string or no if they still have their vote
   */
  public static String getUsersVotedMovie(String username, String eventID){
    try(Session session = driver.session()){
      Result result = session.run("MATCH (a:Person)<-[e:EVENTATTENDEE]-(b:Event) " +
        "WHERE toLower(a.username)=$x1 AND b.id=$x2 " +
        "RETURN properties(e)", parameters("x1", username.toLowerCase(), "x2", eventID));
      if(result.hasNext()){
        Record record = result.next();
        return record.get("properties(e)").get("voted").toString().replace("\"", "");
      }
    }
    return "no";
  }

  /**
   * gets all the potential movies for an event and their vote count
   * @param eventId the event to find the movies from
   * @return a hashmap with the titles as their key, and the value is a map with two attributes "vote" and "eventId",
   * which eventId can be ignored as it is to prevent multiple events voting on a single movie
   */
  public static HashMap<String, Map<String, Object>> getEventMovies(String eventId){
    HashMap<String, Map<String, Object>> movies = new HashMap<>();

    try(Session session = driver.session()){
      Result result = session.run("Match (a:Event)-[:EVENTMOVIE]->(b:EventMovie) " +
        "WHERE a.id=$x1 Return properties(b) ", parameters("x1", eventId));
      while(result.hasNext()){
        Record record = result.next();
        Map<String, Object> movie = record.get("properties(b)").asMap();
        movies.put(record.get("properties(b)").get("name").asString(),
          movie);
      }
    }
    return movies;
  }

  /**
   * remove a potential movie from a event
   * @param eventId the event to remove the movie from
   * @param movieTitle the movie to remove
   */
  public static void removeEventMovie(String eventId, String movieTitle){
    try(Session session = driver.session()){
      session.writeTransaction(transaction -> transaction.run(
        "MATCH (a:Event)-[:EVENTMOVIE]->(b:EventMovie) " +
          "WHERE a.id=$x1 and toLower(b.name)=$x2 AND b.eventId=$x1 DETACH DELETE b",
        parameters("x1", eventId, "x2", movieTitle.toLowerCase())));
    }
  }

  /**
   * removes all movies connected to an event
   * @param eventId the event to remove all movies from
   */
  public static void removeAllEventMovies(String eventId){
    try(Session session = driver.session()){
      session.writeTransaction(transaction -> transaction.run(
        "MATCH (a:Event)-[:EVENTMOVIE]->(b:EventMovie) " +
          "WHERE a.id=$x1 DETACH DELETE b",
        parameters("x1", eventId)));
    }
  }

  /**
   * counts how many nodes are in the database, excluding the source node
   * @return the int of how many nodes there are for testing purposes
   */
  public static int getNodeCount(){
    try(Session session = driver.session()) {
      Result result = session.run("MATCH (n) RETURN n");
      int count = (int)result.stream().count();
      return count;
    }
  }

  /**
   * close out of driver after use
   */
  public static void close(){

    driver.close();
  }
}
