import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;


public class PasswordHashing {

  public PasswordHashing(){}

  /**
   * this will take a new password, create a new salt and hash the new password
   * @param password the password to hash as a string
   * @return a map with the hashedPassword and the salt
   */
  public static Map<String, byte[]> hashNewPassword(String password){

    Map<String, byte[]> returnable = new HashMap<>();

    try {
      //using sha-256
      MessageDigest md = MessageDigest.getInstance("SHA-256");

      //getting a new salt for a new user
      SecureRandom random = new SecureRandom();
      byte[] salt = new byte[16];
      random.nextBytes(salt);

      md.update(salt);

      byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));

      StringBuilder passwordSB = new StringBuilder();
      for (byte b : hashedPassword)
        passwordSB.append(String.format("%02x", b));

      StringBuilder saltSB = new StringBuilder();
      for (byte b : salt)
        saltSB.append(String.format("%02x", b));

      returnable.put("hashedPassword", hashedPassword);
      returnable.put("salt", salt);

    }
    catch(NoSuchAlgorithmException e){}

    return returnable;
  }

  /**
   * used to hash the newly attempted password and check if it matches the user's stored password
   * @param hashedPassword the stored hashed password from the DB
   * @param checkPassword the new password being attempted
   * @param salt the salt from the db
   * @return
   */
  public static boolean checkHashedPassword(String checkPassword, byte[] hashedPassword,
                                            byte[] salt){

    StringBuilder hashedPasswordSB = new StringBuilder();
    StringBuilder passwordSB = new StringBuilder();

    try{
      MessageDigest md = MessageDigest.getInstance("SHA-256");

      md.update(salt);

      byte[] newHashedPassword = md.digest(checkPassword.getBytes(StandardCharsets.UTF_8));

      for (byte b : newHashedPassword)
        passwordSB.append(String.format("%02x", b));

      for (byte b : hashedPassword)
        hashedPasswordSB.append(String.format("%02x", b));

    }
    catch(NoSuchAlgorithmException e){}

    return hashedPasswordSB.toString().equals(passwordSB.toString());
  }

}
