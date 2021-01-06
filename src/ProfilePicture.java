import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ProfilePicture {

  public static AmazonS3 s3;

  public ProfilePicture(){}

  public static void connectS3(){
    AWSCredentials credentials = new BasicAWSCredentials("AKIA5S7HITHSTXKQF3QW",
      "JfD5LbzkXCJtD5iYgV1HtIUE8DX7JJ6IK0s7Tw4P");

    s3 = AmazonS3ClientBuilder
      .standard()
      .withRegion("ca-central-1")
      .withCredentials(new AWSStaticCredentialsProvider(credentials))
      .build();
  }

  /**
   * using the stored url, the function retrieves the profile picture stored on the S3 server
   * @param username the user to get the image from
   * @return the FX image of the profile pic
   */
  public static Image getProfilePic(String username){

    String bucketName = "movienightbucket";
    String key = Server.getAttributes(username).get("pic").toString();

    S3Object fullObject = s3.getObject(new GetObjectRequest(bucketName, key));
    try {
      ImageInputStream iis = ImageIO.createImageInputStream(fullObject.getObjectContent());
      BufferedImage bimage = ImageIO.read(iis);
      Image image = SwingFXUtils.toFXImage(bimage, null);
      return image;
    }
    catch (IOException e){
      e.printStackTrace();
    }
    return null;
  }

  /**
   * It pops up a file choosing window in order to update the user's own profile pic
   */
  public static void setProfilePic(){

    FileChooser chooser = new FileChooser();
    chooser.setTitle("Choose a profile picture");

    FileChooser.ExtensionFilter extFilterALL =
      new FileChooser.ExtensionFilter("All Files", "*.jpeg", "*.jpg",
        "*.tiff", "*.tif","*.JPG","*.JPEG", "*.TIFF", "*.TIF", "*.png", "*.PNG");
    FileChooser.ExtensionFilter extFilterJPG =
      new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG", "*.jpg",
        "*.JPEG", "*.jpeg");
    FileChooser.ExtensionFilter extFilterTIFF =
      new FileChooser.ExtensionFilter("TIFF files (*.tiff)", "*.TIF", "*.TIFF",
        "*.tif", "*.tiff");
    FileChooser.ExtensionFilter extFilterPNG =
      new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG", "*.png");

    chooser.getExtensionFilters().addAll(extFilterALL,extFilterJPG, extFilterTIFF, extFilterPNG);
    File file = chooser.showOpenDialog(new Stage());

    String bucketName = "movienightbucket";
    String key = User.getUserName() + "_profile_pic";

    s3.putObject(new PutObjectRequest(bucketName, key, file));
    User.setProfile_pic(key);


  }
}
