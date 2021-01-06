import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

public class MovieList {
  public Dictionary<String, String> movieRatings; // dictionary to map movie to its rating

  public ArrayList<String> movieNames;

  public MovieList() {
    movieNames = new ArrayList<>();
    movieRatings = new Hashtable<>();
  }

  public void addMovie(String movieName) {
    this.movieNames.add(movieName);
  }

  public boolean giveRating(String movieName, String rating) {
    // rating can only be given if the movie is in user's list
    if (this.movieNames.contains(movieName)) {
      this.movieRatings.put(movieName, rating);
      return true;
    }
    return false;
  }

  public MovieList compareMovies(MovieList other) {
    MovieList commonMovies = new MovieList();
    for (String thisMovieName : this.movieNames) {
      for (String otherMovieName : other.movieNames) {
        if (thisMovieName.equals(otherMovieName)) {
          commonMovies.addMovie(thisMovieName);
          break;
        }
      }
    }
    return commonMovies;
  }
}
