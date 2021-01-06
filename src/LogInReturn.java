import org.neo4j.driver.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LogInReturn {

  boolean success;
  Map<String, Object> profile;

  /**
   * this entire class gives the user the ability to attempt to log in, and let the system know
   * if it was a success, and if so the attributes are in a list for the user.
   * @param success
   * @param profile
   */
  public LogInReturn(boolean success, Map<String, Object> profile){
    this.success = success;
    this.profile = profile;
  }

  public boolean getSuccess(){
    return this.success;
  }

  public Map<String, Object> getProfile(){
    return profile;
  }

}
