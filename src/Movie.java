import java.util.ArrayList;

public class Movie {
  private String movieName;

  private int movieID;

  private String movieDirector;

  private String movieYear;

  private String movieReleaseDate;

  private ArrayList<String> movieActor;

  private ArrayList<String> movieStreamingSite;

  private String movieRating;

  private ArrayList<String> movieGenre;

  private String moviePlot;

  private int movieRuntime; //minutes

  private String moviePosterUrl;

  public Movie(String movieName, int id, String director, String year, String release, String rating,
      ArrayList<String> genre, String plot, int runtime, String posterUrl){
      this.movieName = movieName;
      this.movieID = id;
      this.movieDirector = director;
      this.movieYear = year;
      this.movieReleaseDate = release;
      this.movieRating = rating;
      this.movieGenre = genre;
      this.movieStreamingSite = new ArrayList<>();
      this.movieActor = new ArrayList<>();
      this.moviePlot = plot;
      this.movieRuntime = runtime;
      this.moviePosterUrl = posterUrl;
  }

  public String getMovieName() {
    return movieName;
  }

  public void setMovieName(String movieName) {
    this.movieName = movieName;
  }

  public int getMovieID() {
    return movieID;
  }

  public void setMovieID(int movieID) {
    this.movieID = movieID;
  }

  public String getMovieDirector() {
    return movieDirector;
  }

  public void setMovieDirector(String movieDirector) {
    this.movieDirector = movieDirector;
  }

  public String getMovieReleaseDate() {
    return movieReleaseDate;
  }

  public void setMovieReleaseDate(String movieReleaseDate) {
    this.movieReleaseDate = movieReleaseDate;
  }

  public ArrayList<String> getMovieActor() {
    return movieActor;
  }

  public void addMovieActor(String actor) {
    this.movieActor.add(actor);
  }

  public ArrayList<String> getMovieStreamingSite() {
    return movieStreamingSite;
  }

  public void addMovieStreamingSite(String streamingSite) {
    this.movieStreamingSite.add(streamingSite);
  }

  public String getMovieRating() {
    return movieRating;
  }

  public void setMovieRating(String movieRating) {
    this.movieRating = movieRating;
  }

  public ArrayList<String> getMovieGenre() {
    return movieGenre;
  }

  public void setMovieGenre(ArrayList<String> movieGenre) {
    this.movieGenre = movieGenre;
  }

  public String getMoviePlot() {
    return moviePlot;
  }

  public void setMoviePlot(String moviePlot) {
    this.moviePlot = moviePlot;
  }

  public int getMovieRuntime() {
    return movieRuntime;
  }

  public void setMovieRuntime(int movieRuntime) {
    this.movieRuntime = movieRuntime;
  }

  public String getMoviePosterUrl() {
    return moviePosterUrl;
  }

  public void setMoviePosterUrl(String moviePosterUrl) {
    this.moviePosterUrl = moviePosterUrl;
  }

  public String getMovieYear() {
    return movieYear;
  }

  public void setMovieYear(String movieYear) {
    this.movieYear = movieYear;
  }
}
