import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.ArrayList;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ApiQueryTest {

  @Test
  void testMovieName() {
    ApiQuery apiQuery = new ApiQuery();
    Movie movie = apiQuery.getMovie("star wars");
    Assertions.assertEquals(movie.getMovieName(), "Star Wars: Episode IV - A New Hope");
  }

  @Test
  void testMovieID() {
    ApiQuery apiQuery = new ApiQuery();
    Movie movie = apiQuery.getMovie("star wars");
    Assertions.assertEquals(76759, movie.getMovieID());
  }

  @Test
  void testMovieDirector() {
    ApiQuery apiQuery = new ApiQuery();
    Movie movie = apiQuery.getMovie("star wars");
    Assertions.assertEquals("George Lucas", movie.getMovieDirector());
  }

  @Test
  void testMovieGenres() {
    ApiQuery apiQuery = new ApiQuery();
    Movie movie = apiQuery.getMovie("star wars");
    ArrayList<String> expected = new ArrayList<>();
    expected.add("Action");
    expected.add("Adventure");
    expected.add("Fantasy");
    expected.add("Sci-Fi");
    Assertions.assertEquals(expected, movie.getMovieGenre());
  }

  @Test
  void testMovieActors() {
    ApiQuery apiQuery = new ApiQuery();
    Movie movie = apiQuery.getMovie("star wars");
    ArrayList<String> expected = new ArrayList<>();
    expected.add("Mark Hamill");
    expected.add("Harrison Ford");
    expected.add("Carrie Fisher");
    expected.add("Peter Cushing");
    Assertions.assertEquals(expected, movie.getMovieActor());
  }

  @Test
  void testInvalidIDNineDigit() {
    ApiQuery apiQuery = new ApiQuery();
    Movie movie = apiQuery.getMovie(123456789);
    assertNull(movie);
    assertEquals(apiQuery.getError(), "Error: invalid IMDB ID");
  }

  @Test
  void testInvalidIDNonexistent() {
    ApiQuery apiQuery = new ApiQuery();
    Movie movie = apiQuery.getMovie(9999999);
    assertNull(movie);
    assertEquals(apiQuery.getError(), "Error: could not read JSON");
  }

  @Test
  void testGetMovieById() {
    ApiQuery apiQuery = new ApiQuery();
    Movie movie = apiQuery.getMovie(76759);
    Assertions.assertEquals("Star Wars: Episode IV - A New Hope", movie.getMovieName());
    Assertions.assertEquals("George Lucas", movie.getMovieDirector());
  }

  @Test
  void testGetMovieByIdEightDigit() {
    ApiQuery apiQuery = new ApiQuery();
    Movie movie = apiQuery.getMovie(10001870);
    Assertions.assertEquals("Disrupted Land", movie.getMovieName());
  }

  @Test
  void testMovieRuntime() {
    ApiQuery apiQuery = new ApiQuery();
    Movie movie = apiQuery.getMovie("star wars");
    Assertions.assertEquals(121, movie.getMovieRuntime());
  }

  @Test
  void testMovieRelease() {
    ApiQuery apiQuery = new ApiQuery();
    Movie movie = apiQuery.getMovie("star wars");
    Assertions.assertEquals("25 May 1977", movie.getMovieReleaseDate());
  }

  @Test
  void testMoviePlot() {
    ApiQuery apiQuery = new ApiQuery();
    Movie movie = apiQuery.getMovie("star wars");
    Assertions.assertEquals(
        "The Imperial Forces, under orders from cruel Darth Vader, "
            + "hold Princess Leia hostage in their efforts to quell the rebellion against the Galactic "
            + "Empire. Luke Skywalker and Han Solo, captain of the Millennium Falcon, work together"
            + " with the companionable droid duo R2-D2 and C-3PO to rescue the beautiful princess,"
            + " help the Rebel Alliance and restore freedom and justice to the Galaxy.",
        movie.getMoviePlot());
  }

  @Test
  void testMoviePosterUrl() {
    ApiQuery apiQuery = new ApiQuery();
    Movie movie = apiQuery.getMovie("star wars");
    Assertions.assertEquals(
        "https://m.media-amazon.com/images/M/MV5BNzVlY2MwMjktM2E4OS00Y2Y3LWE3ZjctYzhkZGM3YzA1ZWM2XkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_SX300.jpg",
        movie.getMoviePosterUrl());
  }

  @Test
  void testMovieSearchOnePage() {
    ApiQuery apiQuery = new ApiQuery();
    ArrayList<SearchResult> results = apiQuery.searchMovies("star wars", 1);
    Assertions.assertEquals(10, results.size());
    Assertions.assertEquals("Star Wars: Episode IV - A New Hope", results.get(0).getTitle());
    Assertions.assertEquals(
        "Star Wars: Episode IX - The Rise of Skywalker", results.get(9).getTitle());
  }

  @Test
  void testMovieSearchMultiPage() {
    ApiQuery apiQuery = new ApiQuery();
    ArrayList<SearchResult> results = apiQuery.searchMovies("star wars", 4);
    Assertions.assertEquals(40, results.size());
    Assertions.assertEquals("Star Wars: Episode IV - A New Hope", results.get(0).getTitle());
  }

  @Test
  void testGetMovieJSON() {
    ApiQuery apiQuery = new ApiQuery();
    JSONObject json = apiQuery.getMovieJSON(76759);
    apiQuery.writeJSON(json);
  }

  @Test
  void testReadJSON() {
    ApiQuery apiQuery = new ApiQuery();
    String filePath = new File("").getAbsolutePath();
    Movie movie =
        apiQuery.getMovieFromFile(filePath + "/moviecache/StarWarsEpisodeIVANewHope.json");
    Assertions.assertEquals("Star Wars: Episode IV - A New Hope", movie.getMovieName());
  }

  @Test
  void testBuildMoviesFromCache() {
    ApiQuery apiQuery = new ApiQuery();
    ArrayList<Movie> movies = apiQuery.getAllCachedMovies();
    for (Movie movie : movies) {
      assertNotEquals(null, movie.getMovieName());
    }
  }

  @Test
  void testInvalidSearch() {
    ApiQuery apiQuery = new ApiQuery();
    ArrayList<SearchResult> movies = apiQuery.searchMovies("a");
    assertEquals(0, movies.size());
  }

  @Test
  void testSearchPoster() {
    ApiQuery apiQuery = new ApiQuery();
    ArrayList<SearchResult> movies = apiQuery.searchMovies("star wars");
    assertEquals("https://m.media-amazon.com/images/M/MV5BNzVlY2MwMjktM2E4OS00Y2Y3LWE3ZjctYzhkZGM3YzA1ZWM2XkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_SX300.jpg",
        movies.get(0).getPosterUrl());
  }
}
