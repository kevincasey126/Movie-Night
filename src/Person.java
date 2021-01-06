import java.util.ArrayList;

public class Person {

  //User's saved info, this will be saved on the service
  String username;
  String password;
  int userID;
  ArrayList<String> streamingServices;
  MovieList movieList;

  /**
   * this is used for creating a new person
   * @param username: the new username of the person
   * @param password: the new password of the person
   * @param userID: the userID given to the node from neo4j when connected to the database
   *       (needs further implementation, just a random number for now)
   * @param streamingServices: the list of streaming services the new person is subscribed to
   */
  public Person(String username, String password, int userID, ArrayList<String> streamingServices){
    this.username = username;
    this.userID = userID;
    this.streamingServices = streamingServices;
    this.movieList = new MovieList();
  }

  /**
   * Currently takes a string to be added to the streaming services list, this will be restricted
   * to specific platforms only in the future
   * @param newStream the name of the new streaming service
   */
  public void addStream(String newStream){
    this.streamingServices.add(newStream);
  }

  /**
   * remove a service by name
   * @param removable the name of the service to remove
   */
  public void removeStream(String removable){
    this.streamingServices.remove(removable);
  }

  /**
   *  this returns the list of all the user's streaming services
   * @return the list of streaming services
   */
  public ArrayList<String> getStreamingServices(){
    return this.streamingServices;
  }

  public MovieList getMovieList(){
    return this.movieList;
  }

  public void setMovieList(MovieList mList){
    this.movieList = mList;
  }

  public void addMovie(String movieName){
    movieList.addMovie(movieName);
  }
}
