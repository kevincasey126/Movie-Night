import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MovieListTest {
  @Test
  void testComparingMovieLists(){
    MovieList firstList = new MovieList();
    MovieList secondList = new MovieList();
    firstList.addMovie("superbad");
    firstList.addMovie("21 Grams");
    secondList.addMovie("21 Grams");
    secondList.addMovie("spiderman");
    MovieList movieList = firstList.compareMovies(secondList);
    System.out.println(movieList.movieNames.toString());
  }
}