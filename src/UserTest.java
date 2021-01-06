import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class UserTest {
  @Test
  void testStreamingServices(){
    User.addStream("Netflix");
    User.addStream("Prime");
    String[] expected = new String[]{"Netflix", "Prime"};
    Assertions.assertArrayEquals(expected, User.getStreamingServices().toArray());
    User.removeStream("Netflix");
    expected = new String[]{"Prime"};
    Assertions.assertArrayEquals(expected, User.getStreamingServices().toArray());
  }

}