public class SearchResult {

  private String title;

  private String year;

  private int imdbID;

  private String posterUrl;

  public SearchResult(String title, String year, int imdbID, String posterUrl) {
    this.title = title;
    this.year = year;
    this.imdbID = imdbID;
    this.posterUrl = posterUrl;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getYear() {
    return year;
  }

  public void setYear(String year) {
    this.year = year;
  }

  public int getImdbID() {
    return imdbID;
  }

  public void setImdbID(int imdbID) {
    this.imdbID = imdbID;
  }

  public String getPosterUrl() {
    return posterUrl;
  }

  public void setPosterUrl(String posterUrl) {
    this.posterUrl = posterUrl;
  }
}
