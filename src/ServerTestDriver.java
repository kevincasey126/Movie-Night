import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ServerTestDriver {

  /**
   *
   *
   *
   *
   *
   *
   * RUNNING THIS SERVER TEST WILL DELETE ALL THE CURRENT NODES IN THE SERVER, DO NOT RUN IF ANYONE ELSE IS TESTING
   * OUR PROJECT, AS WELL AS DO NOT EXPECT ANYTHING TO BE HELD IN THE SERVER FROM BEFORE THIS TEST
   *
   *
   *
   *
   *
   *
   *
   *
   */

  @Before
  public void serverTestSetUp(){
  Server.connectServer("bolt://174.2.15.198:7687", "neo4j", "cmpt370");
  //this line is for my local testing, use my external IP above
  //Server.connectServer("bolt://localhost:7687", "neo4j", "password");

  //this top line is temporary as it will literally delete all
  // nodes but the source before beginning
  Server.removeAll();
  Server.addPerson("AdaUN", "password", "Ada", "AdaLN");
  Server.addPerson("AliceUN", "password", "Alice", "AliceLN");
  Server.addPerson("BobUN", "password", "Bob", "BobLN");
  Server.createFriendship("AdaUN", "BobUN");
  Server.createFriendship("AliceUN", "AdaUN");

  }

  @Test
  public void serverTesting(){
  System.out.println("Testing get node count...");
  Assert.assertEquals(3, Server.getNodeCount());
  System.out.println("Passes");

  System.out.println("Testing check username availability...");
  Assert.assertEquals(false, Server.checkUsernameAvailability("BobUN"));
  System.out.println("Passes");

  System.out.println("Testing attempted log in failure...");
  LogInReturn logInAttempt1 = Server.attemptLogIn("BobUN", "asd");
  System.out.println("Passes");

  Assert.assertEquals(false, logInAttempt1.getSuccess());
  System.out.println("Testing add new person...");
  Assert.assertEquals(true, Server.addPerson("JeffUN", "password",
    "Jeff", "JeffLN"));
  System.out.println("Passes");

  System.out.println("Testing attempted log in success...");
  LogInReturn logInAttempt2 = Server.attemptLogIn("JeffUN", "password");
  Assert.assertEquals(true, logInAttempt2.getSuccess());
  System.out.println("Passes");

  System.out.println("Testing get attributes...");
  Assert.assertEquals("AdaUN", Server.getAttributes("AdaUN").get("username"));
  System.out.println("Passes");

  System.out.println("Testing remove person...");
  Server.removePerson("JeffUN");
  Assert.assertEquals(3, Server.getNodeCount());
  Assert.assertEquals(null, Server.getAttributes("JeffUN"));
  Assert.assertEquals(true, Server.checkUsernameAvailability("JeffUN"));
  System.out.println("Passes");

  System.out.println("Testing get all friends...");
  Assert.assertEquals(true, Server.getAllFriends("AdaUN").keySet().contains("BobUN"));
  Assert.assertEquals(true, Server.getAllFriends("AdaUN").keySet().contains("AliceUN"));
  System.out.println("Passes");

  System.out.println("Testing remove friendship...");
  Server.removeFriendship("AdaUN", "AliceUN");
  Assert.assertEquals(false, Server.getAllFriends("AdaUN").keySet().contains("AliceUN"));
  Assert.assertEquals(true, Server.getAllFriends("AdaUN").keySet().contains("BobUN"));
  Assert.assertEquals(false, Server.getAllFriends("AliceUN").keySet().contains("AdaUN"));
  System.out.println("Passes");

  System.out.println("Testing create friendship...");
  Server.createFriendship("AdaUN", "AliceUN");
  Assert.assertEquals(true, Server.getAllFriends("AdaUN").keySet().contains("AliceUN"));
  Assert.assertEquals(true, Server.getAllFriends("AliceUN").keySet().contains("AdaUN"));
  System.out.println("Passes");

  System.out.println("Testing send friend request...");
  Server.addPerson("HenryUN", "password", "Henry", "HenryLN");
  Server.sendFriendRequest("HenryUN", "BobUN");
  Assert.assertEquals(true, Server.getPendingRequests("HenryUN").keySet().contains("BobUN"));
  Assert.assertEquals(true, Server.getFriendRequests("BobUN").keySet().contains("HenryUN"));
  System.out.println("Passes");

  System.out.println("Testing accept friend request..");
  Server.acceptFriendRequest("HenryUN", "BobUN");
  Assert.assertEquals(true, Server.getAllFriends("HenryUN").keySet().contains("BobUN"));
  System.out.println("Passes");

  System.out.println("Testing remove friend request...");
  Server.sendFriendRequest("HenryUN", "AliceUN");
  Assert.assertEquals(true, Server.getPendingRequests("HenryUN").keySet().contains("AliceUN"));
  Server.removeFriendRequest("HenryUN", "AliceUN");
  Assert.assertEquals(false, Server.getPendingRequests("HenryUN").keySet().contains("AliceUN"));
  System.out.println("Passes");

  System.out.println("Testing create event count node...");
  Server.createEventIdCounter();
  Assert.assertEquals("0", Server.getNextEventId());
  System.out.println("Passes");

  System.out.println("Testing increase event id...");
  Assert.assertEquals("0", Server.getNextEventId());
  Server.increaseEventId();
  Assert.assertEquals("1", Server.getNextEventId());
  System.out.println("Passes");

  System.out.println("Testing create a new event...");
  Server.createEvent("BobUN", "Bob's movie night", "Bob's house", "Dec 8");
  Assert.assertEquals("Bob's movie night", Server.getEventAttributes("1").get("eventName").toString());
  System.out.println("Passed");

  System.out.println("Testing get event's attendees...");
  Assert.assertEquals(true, Server.getEventAttendees("1").keySet().contains("BobUN"));
  System.out.println("Passed");


  System.out.println("Testing get user's organizer events...");
  Assert.assertEquals(true, Server.getUsersOrganizingEvents("BobUN").keySet().contains("1"));
  Assert.assertEquals(false, Server.getUsersOrganizingEvents("BobUN").keySet().contains("2"));
  System.out.println("Passed");

  System.out.println("Testing send event invite...");
  Server.sendEventInvite("AdaUN", "1");
  Server.sendEventInvite("HenryUN", "1");
  Assert.assertEquals(true, Server.getUsersPendingInvites("AdaUN").keySet().contains("1"));
  Assert.assertEquals(false, Server.getUsersPendingInvites("AdaUN").keySet().contains("2"));
  Assert.assertEquals(true, Server.getUsersPendingInvites("HenryUN").keySet().contains("1"));
  System.out.println("Passed");

  System.out.println("Testing accept event invite...");
  Server.acceptEventInvite("AdaUN", "1");
  Assert.assertEquals(true, Server.getEventAttendees("1").keySet().contains("AdaUN"));
  Assert.assertEquals(false, Server.getEventAttendees("1").keySet().contains("HenryUN"));
  System.out.println("Passed");

  System.out.println("Testing remove event invite...");
  Server.removeEventInvite("HenryUN", "1");
  Assert.assertEquals(false, Server.getUsersPendingInvites("HenryUN").keySet().contains("1"));
  Assert.assertEquals(false, Server.getEventAttendees("1").keySet().contains("HenryUN"));
  System.out.println("Passed");

  System.out.println("Testing get all user's events...");
  Assert.assertEquals(true, Server.getUsersEvents("AdaUN").keySet().contains("1"));
  Assert.assertEquals(false, Server.getUsersEvents("AdaUN").keySet().contains("2"));
  System.out.println("Passed");

  System.out.println("Testing remove user from event...");
  Server.removeAttendingEvent("AdaUN", "1");
  Assert.assertEquals(false, Server.getEventAttendees("1").keySet().contains("AdaUN"));
  System.out.println("Passed");

  System.out.println("Testing add movie to movie night...");
  Server.addEventMovie("SuperBad", "1");
  Server.addEventMovie("Scream", "1");
  Server.addEventMovie("Get Out", "1");
  Assert.assertEquals(true, Server.getEventMovies("1").keySet().contains("SuperBad"));
  Assert.assertEquals(true, Server.getEventMovies("1").keySet().contains("Scream"));
  Assert.assertEquals(true, Server.getEventMovies("1").keySet().contains("Get Out"));
  System.out.println("Passed");

  System.out.println("Testing add movie vote...");
  Assert.assertEquals("no", Server.getUsersVotedMovie("BobUN", "1"));
  Server.userVoted("BobUN", "1", "SuperBad");
  Assert.assertEquals("SuperBad", Server.getUsersVotedMovie("BobUN", "1"));
  Assert.assertEquals("1", Server.getEventMovies("1").get("SuperBad").get("vote").toString());
  System.out.println("Passed");

  System.out.println("Testing remove movie vote...");
  Server.userRemovedVote("BobUN", "1", "SuperBad");
  Assert.assertEquals("0", Server.getEventMovies("1").get("SuperBad").get("vote").toString());
  Assert.assertEquals("no", Server.getUsersVotedMovie("BobUN", "1"));
  System.out.println("Passed");

  System.out.println("Testing remove movie from movie night vote...");
  Server.removeEventMovie("1", "SuperBad");
  Assert.assertEquals(false, Server.getEventMovies("1").keySet().contains("SuperBad"));
  System.out.println("Passed");

  System.out.println("Testing remove all event movies...");
  Server.removeAllEventMovies("1");
  Assert.assertEquals(false, Server.getEventMovies("1").keySet().contains("Scream"));
  Assert.assertEquals(false, Server.getEventMovies("1").keySet().contains("Get Out"));
  System.out.println("Passed");

  System.out.println("Testing add streaming service...");
  Server.addStreamingService("Netflix", "AdaUN");
  Server.addStreamingService("Hulu", "AdaUN");
  Server.addStreamingService("Netflix", "BobUN");
  Assert.assertEquals(true, Server.getUsersStreamingServices("AdaUN").keySet().contains("netflix"));
  Assert.assertEquals(true, Server.getUsersStreamingServices("BobUN").keySet().contains("netflix"));
  System.out.println("Passed");

  System.out.println("Testing remove streaming service");
  Server.removeStreamingService("Netflix", "BobUN");
  Assert.assertEquals(false, Server.getUsersStreamingServices("BobUN").keySet().contains("netflix"));
  System.out.println("Passed");

  System.out.println("Testing remove event...");
  Server.removeEvent("1");
  Assert.assertEquals(false, Server.getUsersEvents("BobUN").keySet().contains("1"));
  System.out.println("Passed");

  System.out.println("Testing add favourite movie...");
  Server.addFavouriteMovie("Superbad", "BobUN", "4.6/5");
  Server.addFavouriteMovie("Superbad", "AdaUN", "3.6/5");
  Server.addFavouriteMovie("21 Grams", "AdaUN", "3.6/5");
  Server.addFavouriteMovie("1917", "AdaUN", "3.6/5");
  Server.addFavouriteMovie("300", "AdaUN", "3.6/5");


  Assert.assertEquals(true, Server.getUsersFavouriteMovies("BobUN").keySet().contains("Superbad"));
  Assert.assertEquals(false, Server.getUsersFavouriteMovies("BobUN").keySet().contains("Scream"));
  System.out.println("Passed");

  System.out.println("Testing remove favourite movie...");
  Server.removeFavouriteMovie("Superbad", "BobUN");
  Assert.assertEquals(false, Server.getUsersFavouriteMovies("BobUN").keySet().contains("Superbad"));
  System.out.println("Passed");

  System.out.println("Testing get fav movie ratings...");
  Assert.assertEquals("3.6/5", Server.getUsersFavouriteMovies("AdaUN").get("Superbad").get("rating").toString());
  System.out.println("Passed");

  System.out.println("Testing new want to see movie...");
  Server.addWantToWatch("Superbad", "AliceUN");
  Server.addWantToWatch("Scream", "AliceUN");
  Assert.assertEquals(true, Server.getUsersWantToWatchMovies("AliceUN").keySet().contains("Superbad"));
  Assert.assertEquals(false, Server.getUsersWantToWatchMovies("AliceUN").keySet().contains("Anything else"));
  Assert.assertEquals(true, Server.getUsersWantToWatchMovies("AliceUN").keySet().contains("Scream"));
  System.out.println("Passed");

  System.out.println("Testing remove want to see movie...");
  Server.removeWantToWatch("Scream", "AliceUN");
  Assert.assertEquals(false, Server.getUsersWantToWatchMovies("AliceUN").keySet().contains("Scream"));
  System.out.println("Passed");


  System.out.println("Great Success");
  }

  @After
  public void serverTestClose(){
  Server.close();
  }

}
