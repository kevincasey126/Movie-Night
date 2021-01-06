import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javafx.event.ActionEvent;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class BrowseScene {
  private Model model;
  private ApiQuery apiQuery;
  private ArrayList<Movie> movieList;
  private ArrayList<Button> displayedMovies = new ArrayList<>();
  private ArrayList<Movie> displayedMovieObjects = new ArrayList<>();
  private ArrayList<Button> filteredMovies = new ArrayList<>();
  private int rowsDisplayed;
  private int checkPages;
  private int lastsize = 0;
  private static final String IDLE_BUTTON_STYLE = "-fx-background-color: #3892C7";
  private static final String HOVERED_BUTTON_STYLE = "-fx-background-color: #005BFF";
  private static final Paint TEXT_FILL = Color.web("#384BC7");

  private Map<CheckBox, String> genreCheckboxes = new HashMap<CheckBox, String>();
  private Map<CheckBox, String> ratingCheckboxes = new HashMap<CheckBox, String>();

  @FXML private CheckBox action_checkbox;
  @FXML private CheckBox adventure_checkbox;
  @FXML private CheckBox animation_checkbox;
  @FXML private CheckBox biography_checkbox;
  @FXML private CheckBox comedy_checkbox;
  @FXML private CheckBox crime_checkbox;
  @FXML private CheckBox documentary_checkbox;
  @FXML private CheckBox drama_checkbox;
  @FXML private CheckBox family_checkbox;
  @FXML private CheckBox fantasy_checkbox;
  @FXML private CheckBox filmnoir_checkbox;
  @FXML private CheckBox gameshow_checkbox;
  @FXML private CheckBox history_checkbox;
  @FXML private CheckBox horror_checkbox;
  @FXML private CheckBox music_checkbox;
  @FXML private CheckBox musical_checkbox;
  @FXML private CheckBox mystery_checkbox;
  @FXML private CheckBox news_checkbox;
  @FXML private CheckBox reality_checkbox;
  @FXML private CheckBox romance_checkbox;
  @FXML private CheckBox scifi_checkbox;
  @FXML private CheckBox sport_checkbox;
  @FXML private CheckBox talkshow_checkbox;
  @FXML private CheckBox thriller_checkbox;
  @FXML private CheckBox war_checkbox;
  @FXML private CheckBox western_checkbox;
  @FXML private CheckBox g_rating_checkbox;
  @FXML private CheckBox pg_rating_checkbox;
  @FXML private CheckBox pg13_rating_checkbox;
  @FXML private CheckBox r_rating_checkbox;
  @FXML private CheckBox nc17_rating_checkbox;

  @FXML private Button genre_filter_button, year_filter_button, rating_filter_button, back_button;

  @FXML private TextField startyear_textfield, endyear_textfield, searchbar_textfield;

  @FXML private GridPane movie_gridpane;

  @FXML private AnchorPane movie_anchorpane;

  @FXML private ScrollPane movie_scrollpane;

  public BrowseScene(Model newModel) {
    model = newModel;
    apiQuery = new ApiQuery();
    movieList = apiQuery.getAllCachedMovies();
    Collections.shuffle(movieList);
    rowsDisplayed = 0;

    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("BrowseScene.fxml"));

      loader.setController(this);

      model.stage.setScene(new Scene(loader.load()));

      model.stage.setTitle("MovieNight - BrowseScene");

      back_button.setStyle(IDLE_BUTTON_STYLE);
      back_button.setTextFill(TEXT_FILL);
      back_button.setOnMouseEntered(e -> back_button.setStyle(HOVERED_BUTTON_STYLE));
      back_button.setOnMouseExited(e -> back_button.setStyle(IDLE_BUTTON_STYLE));

      genre_filter_button.setStyle(IDLE_BUTTON_STYLE);
      genre_filter_button.setTextFill(TEXT_FILL);
      genre_filter_button.setOnMouseEntered(e -> genre_filter_button.setStyle(HOVERED_BUTTON_STYLE));
      genre_filter_button.setOnMouseExited(e -> genre_filter_button.setStyle(IDLE_BUTTON_STYLE));

      year_filter_button.setStyle(IDLE_BUTTON_STYLE);
      year_filter_button.setTextFill(TEXT_FILL);
      year_filter_button.setOnMouseEntered(e -> year_filter_button.setStyle(HOVERED_BUTTON_STYLE));
      year_filter_button.setOnMouseExited(e -> year_filter_button.setStyle(IDLE_BUTTON_STYLE));

      rating_filter_button.setStyle(IDLE_BUTTON_STYLE);
      rating_filter_button.setTextFill(TEXT_FILL);
      rating_filter_button.setOnMouseEntered(e -> rating_filter_button.setStyle(HOVERED_BUTTON_STYLE));
      rating_filter_button.setOnMouseExited(e -> rating_filter_button.setStyle(IDLE_BUTTON_STYLE));

      genreCheckboxes.put(action_checkbox, "Action");
      genreCheckboxes.put(adventure_checkbox, "Adventure");
      genreCheckboxes.put(animation_checkbox, "Animation");
      genreCheckboxes.put(biography_checkbox, "Biography");
      genreCheckboxes.put(comedy_checkbox, "Comedy");
      genreCheckboxes.put(crime_checkbox, "Crime");
      genreCheckboxes.put(documentary_checkbox, "Documentary");
      genreCheckboxes.put(drama_checkbox, "Drama");
      genreCheckboxes.put(family_checkbox, "Family");
      genreCheckboxes.put(fantasy_checkbox, "Fantasy");
      genreCheckboxes.put(filmnoir_checkbox, "Film-Noir");
      genreCheckboxes.put(gameshow_checkbox, "Game-Show");
      genreCheckboxes.put(history_checkbox, "History");
      genreCheckboxes.put(horror_checkbox, "Horror");
      genreCheckboxes.put(music_checkbox, "Music");
      genreCheckboxes.put(musical_checkbox, "Musical");
      genreCheckboxes.put(mystery_checkbox, "Mystery");
      genreCheckboxes.put(news_checkbox, "News");
      genreCheckboxes.put(reality_checkbox, "Reality-TV");
      genreCheckboxes.put(romance_checkbox, "Romance");
      genreCheckboxes.put(scifi_checkbox, "Sci-Fi");
      genreCheckboxes.put(sport_checkbox, "Sport");
      genreCheckboxes.put(talkshow_checkbox, "Talk-Show");
      genreCheckboxes.put(thriller_checkbox, "Thriller");
      genreCheckboxes.put(war_checkbox, "War");
      genreCheckboxes.put(western_checkbox, "Western");

      ratingCheckboxes.put(g_rating_checkbox, "G");
      ratingCheckboxes.put(pg_rating_checkbox, "PG");
      ratingCheckboxes.put(pg13_rating_checkbox, "PG-13");
      ratingCheckboxes.put(r_rating_checkbox, "R");
      ratingCheckboxes.put(nc17_rating_checkbox, "NC-17");

      ScrollBar scrollBar = (ScrollBar) movie_scrollpane.lookup(".scroll-bar:vertical");
      scrollBar
          .valueProperty()
          .addListener(
              (observable, oldValue, newValue) -> {
                if ((Double) newValue == 1.0) {
                  populateSearchField();
                }
              });

      populateSearchField();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void pressedBackButton(ActionEvent event) throws IOException {
    MainScene mainScene = new MainScene(model);
  }

  public void onSearch(ActionEvent event) throws IOException {
    SearchScene newSearchScene = new SearchScene(model, searchbar_textfield.getText());
  }

  public void yearFilterPressed() {
    lastsize = 0;
    filter();
  }

  public void filter() {
    int startYear;
    int endYear;
    try {
      startYear = Integer.parseInt(startyear_textfield.getText());
    } catch (Exception e) {
      startYear = 0;
    }

    try {
      endYear = Integer.parseInt(endyear_textfield.getText());
    } catch (Exception e) {
      endYear = 10000;
    }
    filterResults(startYear, endYear);
    condenseFilteredResults();
  }

  /**
   * Populate 3 rows of images for the browse page when called and increments the rowDsiplayed
   * counter.
   */
  public void populateSearchField() {
    for (int rowIndex = rowsDisplayed; rowIndex < rowsDisplayed + 3; rowIndex++) {
      for (int columnIndex = 0; columnIndex < 5; columnIndex++) {
        if (rowIndex * 5 + columnIndex < movieList.size()) {
          Movie movie = movieList.get(rowIndex * 5 + columnIndex);
          Button button = new Button();
          button.setMaxSize(75, 112);
          displayedMovies.add(button);
          displayedMovieObjects.add(movie);
          button.setOnAction(
              actionEvent -> {
                altMovieScene movieScene = new altMovieScene(model, movie, model.stage.getScene());
              });
          movie_gridpane.add(button, columnIndex, rowIndex);
          GridPane.setHalignment(button, HPos.CENTER);
        }
      }
    }
    rowsDisplayed = rowsDisplayed + 3;
    filter();
  }

  private void filterResults(int startYear, int endYear) {
    filteredMovies.clear();

    ArrayList<String> checkedGenres = new ArrayList<>();
    for (CheckBox checkBox : genreCheckboxes.keySet()) {
      if (checkBox.isSelected()) {
        checkedGenres.add(genreCheckboxes.get(checkBox));
      }
    }

    ArrayList<String> checkedRatings = new ArrayList<>();
    for (CheckBox checkBox : ratingCheckboxes.keySet()) {
      if (checkBox.isSelected()) {
        checkedRatings.add(ratingCheckboxes.get(checkBox));
      }
    }
    for (int i = 0; i < displayedMovies.size(); i++) { // check year filters
      int year = Integer.parseInt(movieList.get(i).getMovieYear());
      if (year < startYear || year > endYear) {
        movie_gridpane.getChildren().remove(displayedMovies.get(i));
      } else { // check genre filters
        ArrayList<String> genres = movieList.get(i).getMovieGenre();
        boolean filterGenre = false;
        for (String genre : checkedGenres) {
          if (genres.contains(genre)) {
            filterGenre = true;
            break;
          }
        }
        if (!filterGenre) {
          movie_gridpane.getChildren().remove(displayedMovies.get(i));
        } else {
          String rating = movieList.get(i).getMovieRating();
          boolean filterRating = false;
          for (String ratingFilter : checkedRatings) {
            if (ratingFilter.equals(rating)) {
              filterRating = true;
              break;
            }
          }
          if (!filterRating) {
            movie_gridpane.getChildren().remove(displayedMovies.get(i));
          } else {
            Button movieToAdd = displayedMovies.get(i);
            Image image;
            ImageView movieImage;
            try {
              if (movieToAdd.getGraphic() == null) {
                image =
                    new Image(
                        displayedMovieObjects.get(i).getMoviePosterUrl(), 75, 112, false, false);
                movieImage = new ImageView(image);
                movieToAdd.setGraphic(movieImage);
              }
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
              } catch (FileNotFoundException fnfe) {
                break;
              }
              image = new Image(inputstream, 75, 112, false, false);
              movieImage = new ImageView(image);
              movieToAdd.setGraphic(movieImage);
            }
            filteredMovies.add(movieToAdd);
          }
        }
      }
    }
  }

  private void condenseFilteredResults() {
    movie_gridpane.getChildren().clear();
    for (int i = 0; i < filteredMovies.size(); i++) {
      movie_gridpane.add(filteredMovies.get(i), i % 5, i / 5);
    }

    movie_anchorpane.setMinSize(
        movie_anchorpane.getPrefWidth(), (float) (filteredMovies.size() / 5 * 130));
    movie_anchorpane.setMaxSize(
        movie_anchorpane.getPrefWidth(), (float) (filteredMovies.size() / 5 * 130));
    if ((filteredMovies.size() < 15 || (filteredMovies.size() - lastsize) < 5)
        && displayedMovies.size() < movieList.size()) {
      populateSearchField();
    } else {
      lastsize = filteredMovies.size();
    }
  }
}
