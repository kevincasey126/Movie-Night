import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.util.Scanner;
import org.json.simple.parser.ParseException;

public class ApiQuery {

  private String omdbUrl = "http://www.omdbapi.com/?apikey="; // need '&' after API key
  private String omdbApiKey = "1eafea25";
  private String error = "";

  /*
  JSON format:
  By Movie:
  {Title, Year, Rated, Released, Runtime, Genre, Director, Writer, Actors, Plot, Language, Country,
      Awards, Poster, Ratings[3], Metascore, imdbRating, imdbVotes, imdbID, Type, DVD, Production,
      Website, Response}

  By Search:
  {Search:[{Title, Year, imdbID, Type, Poster}, {Title...       ...}, ... {Title...       ...}],
      totalResults, Response}
    Each page contains 10 results
   */

  /**
   * * Search the Open Movie Database API for movies that match a given search term
   *
   * @param titleSearch String: the user-inputted string to search for matches
   * @param pages int: the number of pages of results to return - each page contains up to 10
   *     results
   */
  public ArrayList<SearchResult> searchMovies(String titleSearch, int pages) {
    titleSearch = formatStringForURL(titleSearch);

    ArrayList<SearchResult> results = new ArrayList<>();

    for (int i = 1; i <= pages; i++) {
      try {
        URL url = new URL(omdbUrl + omdbApiKey + "&s=" + titleSearch + "&type=movie&page=" + i);
        // check if the URL works
        if (isResponseCode200(url)) {
          JSONObject json = convertStringToJSON(readTextFromURL(url));

          // search returns an array, add all of them
          if (json != null) {
            JSONArray jsonArray = (JSONArray) json.get("Search");
            if (jsonArray != null) {
              for (int j = 0; j < jsonArray.size(); j++) {
                if (jsonArray.get(j) != null) {
                  String title = (String) ((JSONObject) jsonArray.get(j)).get("Title");
                  String year = (String) ((JSONObject) jsonArray.get(j)).get("Year");
                  int id =
                      Integer.parseInt(
                          ((String) ((JSONObject) jsonArray.get(j)).get("imdbID"))
                              .replaceAll("t", ""));
                  String posterUrl = (String) ((JSONObject) jsonArray.get(j)).get("Poster");
                  SearchResult result = new SearchResult(title, year, id, posterUrl);
                  results.add(result);
                }
              }
            }
          } else {
            error = "Error: could not read JSON";
            return results;
          }

        } else {
          error = "Error: bad response from URL";
        }
      } catch (IOException e) {
        error = "Error: malformed URL";
        return results;
      }
    }
    return results;
  }

  /**
   * * Search the Open Movie Database API for movies that match a given search term
   *
   * @param titleSearch String: the user-inputted string to search for matches
   * @param startPage int: the start of the range of pages of results to return - each page contains up to 10
   *     results
   * @param endPage int: the end of the range of pages of results to return
   */
  public ArrayList<SearchResult> searchMovies(String titleSearch, int startPage, int endPage) {
    titleSearch = formatStringForURL(titleSearch);

    ArrayList<SearchResult> results = new ArrayList<>();

    for (int i = startPage; i <= endPage; i++) {
      try {
        URL url = new URL(omdbUrl + omdbApiKey + "&type=movie&s=" + titleSearch + "&page=" + i);
        // check if the URL works
        if (isResponseCode200(url)) {
          JSONObject json = convertStringToJSON(readTextFromURL(url));

          // search returns an array, add all of them
          if (json != null) {
            JSONArray jsonArray = (JSONArray) json.get("Search");
            if (jsonArray != null) {
              for (int j = 0; j < jsonArray.size(); j++) {
                if (jsonArray.get(j) != null) {
                  String title = (String) ((JSONObject) jsonArray.get(j)).get("Title");
                  String year = (String) ((JSONObject) jsonArray.get(j)).get("Year");
                  int id =
                      Integer.parseInt(
                          ((String) ((JSONObject) jsonArray.get(j)).get("imdbID"))
                              .replaceAll("t", ""));
                  String posterUrl = ((JSONObject) jsonArray.get(j)).get("Poster").toString();
                  SearchResult result = new SearchResult(title, year, id, posterUrl);
                  results.add(result);
                }
              }
            }
          } else {
            error = "Error: could not read JSON";
            return results;
          }

        } else {
          error = "Error: bad response from URL";
        }
      } catch (IOException e) {
        error = "Error: malformed URL";
        return results;
      }
    }
    return results;
  }

  /**
   * * Search the Open Movie Database API for movies that match a given search term
   *
   * @param titleSearch String: the user-inputted string to search for matches
   */
  public ArrayList<SearchResult> searchMovies(String titleSearch) {
    titleSearch = formatStringForURL(titleSearch);

    ArrayList<SearchResult> results = new ArrayList<>();

    try {
      URL url = new URL(omdbUrl + omdbApiKey + "&s=" + titleSearch + "&type=movie");
      // check if the URL works
      if (isResponseCode200(url)) {
        JSONObject json = convertStringToJSON(readTextFromURL(url));

        // search returns an array, add all of them
        if (json != null) {
          JSONArray jsonArray = (JSONArray) json.get("Search");
          if(jsonArray != null){
            for (int j = 0; j < jsonArray.size(); j++) {
              if(jsonArray.get(j) != null){
                String title = (String) ((JSONObject) jsonArray.get(j)).get("Title");
                String year = (String) ((JSONObject) jsonArray.get(j)).get("Year");
                int id =
                    Integer.parseInt(
                        ((String) ((JSONObject) jsonArray.get(j)).get("imdbID")).replaceAll("t", ""));
                String posterUrl = (String) ((JSONObject) jsonArray.get(j)).get("Poster");
                SearchResult result = new SearchResult(title, year, id, posterUrl);
                results.add(result);
              }
            }
          }
        } else {
          error = "Error: could not read JSON";
          return results;
        }

      } else {
        error = "Error: bad response from URL";
      }
    } catch (IOException e) {
      error = "Error: malformed URL";
      return results;
    }
    return results;
  }

  /**
   * Retrieve information on a specific movie
   *
   * @param titleInput String: the title of the movie to retrieve
   * @return a Movie object containing all the relevant information about the movie
   */
  public Movie getMovie(String titleInput) {
    titleInput = formatStringForURL(titleInput);

    try {
      // &t for title, &type=movie to only return movies
      URL url = new URL(omdbUrl + omdbApiKey + "&t=" + titleInput + "&type=movie&plot=full");

      // response code 200 means the URL is valid
      if (isResponseCode200(url)) {
        JSONObject json = convertStringToJSON(readTextFromURL(url));
        if (json != null && json.get("Response").equals("True")) {
          writeJSON(json);
          return formatMovie(json);
        } else {
          error = "Error: could not read JSON";
          return null;
        }
      } else return null;
    } catch (IOException e) {
      error = "Error: malformed URL";
      return null;
    }
  }

  /**
   * Retrieve information on a specific movie
   *
   * @param imdbID int: the IMDB ID of the movie to retrieve
   * @return a Movie object containing all the relevant information about the movie
   */
  public Movie getMovie(int imdbID) {
    String id;
    // some 8 digit IDs exist, but all other IDs need to be padded to 7 digits
    if (imdbID < 10000000) {
      id = String.format("%07d", imdbID);
    } else if (imdbID > 99999999) {
      error = "Error: invalid IMDB ID";
      return null;
    } else {
      id = ((Integer) imdbID).toString();
    }

    // &i for ID, &type=movie to only return movies, &plot=full to get full description
    try {
      URL url = new URL(omdbUrl + omdbApiKey + "&i=tt" + id + "&type=movie&plot=full");

      // response code 200 means the URL is valid
      if (isResponseCode200(url)) {
        JSONObject json = convertStringToJSON(readTextFromURL(url));
        if (json != null && json.get("Response").toString().equals("True")) {
          writeJSON(json);
          return formatMovie(json);
        } else {
          error = "Error: could not read JSON";
          return null;
        }

      } else return null;
    } catch (IOException e) {
      error = "Error: malformed URL";
      return null;
    }
  }

  /**
   * Retrieve information on a specific movie in JSON format
   *
   * @param imdbID int: the IMDB ID of the movie to retrieve
   * @return a Movie object containing all the relevant information about the movie
   */
  public JSONObject getMovieJSON(int imdbID) {
    String id;
    // some 8 digit IDs exist, but all other IDs need to be padded to 7 digits
    if (imdbID < 10000000) {
      id = String.format("%07d", imdbID);
    } else if (imdbID > 99999999) {
      error = "Error: invalid IMDB ID";
      return null;
    } else {
      id = ((Integer) imdbID).toString();
    }

    // &i for ID, &type=movie to only return movies, &plot=full to get full description
    try {
      URL url = new URL(omdbUrl + omdbApiKey + "&i=tt" + id + "&type=movie&plot=full");

      // response code 200 means the URL is valid
      if (isResponseCode200(url)) {
        JSONObject json = convertStringToJSON(readTextFromURL(url));
        if (json != null) {
          return json;
        } else {
          error = "Error: could not read JSON";
          return null;
        }

      } else return null;
    } catch (IOException e) {
      error = "Error: malformed URL";
      return null;
    }
  }

  /**
   * write JSON data to a file
   *
   * @param json the JSON data to write
   */
  public void writeJSON(JSONObject json) {
    String filePath = "";
    try{
      filePath = new File(ApiQuery.class.getProtectionDomain().getCodeSource().getLocation()
          .toURI()).getParentFile().getPath();
      System.out.println(filePath);
    }
    catch (Exception e){

    }

    String path = "/movieCache/";
    String fileName = (String) json.get("Title");
    fileName = fileName.replaceAll("\\W", "");
    fileName = filePath + path + fileName + ".json";
    System.out.println(fileName);

    try {
      File newFile = new File(fileName);
      if (newFile.createNewFile()) {
        System.out.println("File created: " + newFile.getName());
        FileWriter myWriter = new FileWriter(fileName);
        myWriter.write(json.toString() + "\n");
        myWriter.close();
      } else {
        System.out.println("File already exists.");
      }

    } catch (IOException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }
  }

  /**
   * Read in JSON data from a file and convert it into a Movie object
   *
   * @param filePath the path to the JSON file
   * @return a Movie object containing the movie data
   */
  public Movie getMovieFromFile(String filePath) {
    try {
      File file = new File(filePath);
      Scanner scanner = new Scanner(file);
      JSONObject json = new JSONObject();
      while (scanner.hasNextLine()) {
        String data = scanner.nextLine();
        JSONParser parser = new JSONParser();
        json = (JSONObject) parser.parse(data);
      }
      scanner.close();

      return formatMovie(json);
    } catch (FileNotFoundException | ParseException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }
    return null;
  }

  public ArrayList<Movie> getAllCachedMovies() {
    String filePath = "";
    try{
      filePath = new File(ApiQuery.class.getProtectionDomain().getCodeSource().getLocation()
          .toURI()).getParentFile().getPath();
      System.out.println(filePath);
    }
    catch(Exception e){

    }

    File folder = new File(filePath + "/movieCache/");
    File[] listOfFiles = folder.listFiles();

    ArrayList<Movie> cachedMovies = new ArrayList<>();

    for (File json : listOfFiles) {
      cachedMovies.add(getMovieFromFile(json.getAbsolutePath()));
    }
    return cachedMovies;
  }

  /**
   * Get the last error thrown by the ApiRequest
   *
   * @return error - the string of the error message
   */
  public String getError() {
    return error;
  }

  private Movie formatMovie(JSONObject json) {
    if (json.get("Title") == null) {
      error = error = "Error: could not read JSON";
      return null;
    }
    String title = (String) json.get("Title");
    int id = idStringToInt((String) json.get("imdbID"));
    String director = (String) json.get("Director");
    String year = (String) json.get("Year");
    String releaseDate = (String) json.get("Released");
    String rating = (String) json.get("Rated");
    String plot = (String) json.get("Plot");
    String posterUrl = (String) json.get("Poster");
    String runtimeString = (String) json.get("Runtime");
    int runtime;
    try{
      runtime = Integer.parseInt(runtimeString.substring(0, runtimeString.length() - 4));
    }
    catch(Exception e){
      runtime = 0;
    }
    String[] genres = ((String) json.get("Genre")).split(",");
    ArrayList<String> genreList = new ArrayList<String>();

    for (String genre : genres) {
      genre = genre.trim();
      genreList.add(genre);
    }

    Movie movie =
        new Movie(title, id, director,  year, releaseDate, rating, genreList, plot, runtime, posterUrl);

    String[] actors = ((String) json.get("Actors")).split(",");
    for (String actor : actors) {
      actor = actor.trim();
      movie.addMovieActor(actor);
    }
    return movie;
  }

  private int idStringToInt(String idString) {
    return Integer.parseInt((idString).replaceAll("t", ""));
  }

  private boolean isResponseCode200(URL url) {
    try {
      HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
      urlConnection.setRequestMethod("GET");
      int responseCode = urlConnection.getResponseCode();
      return (responseCode == 200);
    } catch (IOException e) {
      error = "Error: Could not open connection to URL";
      return false;
    }
  }

  private String readTextFromURL(URL url) {
    String inline = "";
    try {
      Scanner scanner = new Scanner(url.openStream());
      while (scanner.hasNext()) {
        inline += scanner.nextLine();
      }
      scanner.close();

      return inline;
    } catch (IOException e) {
      error = "Error: Could not open connection to URL";
      return null;
    }
  }

  private JSONObject convertStringToJSON(String text) {
    JSONParser parser = new JSONParser();
    try {
      return (JSONObject) parser.parse(text);
    } catch (ParseException e) {
      error = "Error: Could not convert string to JSON";
      return null;
    }
  }

  private String formatStringForURL(String input) {
    input = input.strip();
    return input.replaceAll("\\s", "+");
  }

  private void generateMassJSON() {
    ApiQuery apiQuery = new ApiQuery();
    ArrayList<String> idStrings = new ArrayList<>();
    try {
      File file = new File("D:/CMPT 370/group8/src/movieIDs.txt");
      Scanner scanner = new Scanner(file);

      while (scanner.hasNextLine()) {
        idStrings.add(scanner.nextLine());
      }
      scanner.close();

    } catch (FileNotFoundException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }

    ArrayList<Integer> ids = new ArrayList<>();
    ArrayList<Integer> failedids = new ArrayList<>();

    for (String idstring : idStrings) {
      ids.add(apiQuery.idStringToInt(idstring));
    }

    for (Integer id : ids) {
      try {
        JSONObject json = apiQuery.getMovieJSON(id);
        apiQuery.writeJSON(json);
      } catch (Exception e) {
        failedids.add(id);
      }
    }

    System.out.println(failedids);
  }

  public static void main(String[] args) {
    ApiQuery apiQuery = new ApiQuery();
    Movie movie = apiQuery.getMovie("Batman Begins");
    System.out.println(movie.getMovieName());
  }
}
