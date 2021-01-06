**Compilation Instructions:**

NOTE: Servers are no longer active and application requires log in to use

To run the JAR file, you must be running Java 15.
The moviecache folder and placeholder image file must be in the same directory as the JAR file.

Libraries used (libraries were acquired through the built-in Maven search in IntelliJ):
 - com.amazonaws:aws-java-sdk-s3:1.11.43
 - com.guicedee.services:jakarta.xml.bind-api:62
 - org.neo4j.driver:neo4j-java-driver:4.1.1
 - JUnit 5
 - Ensure JavaFX is installed, and in the VM Options in Run>Edit Configurations in IntelliJ, put --module-path "C:\Path to JavaFX\lib" --add-modules javafx.controls,javafx.fxml
   JavaFX must also be a library in the Project Structure. To set this library open File>Project Structure>Libraries, then with the little "+" sign, add a library from Java which will you will find under your Path to JavaFX\lib

To compile from the source, you must install the libraries above in IntelliJ. Add the moviecache and placeholder image to your out/production folder in your project. From there it should run in IntelliJ, if you want to pack it into a JAR you must follow this workaround to package the JavaFX.

The OMDb API is used for movie data, but is a REST API and requires no installation.

**Running the project:**

There are two ways to run this:

1. Through the jar file: After unzipping the [Jar](https://git.cs.usask.ca/CMPT370-01-2020/group8/-/blob/5e76bba1b2283a465ca93b40ee4a264f67608777/group8_jar.zip) folderSimply double-click the jar file(user does not need to install javafx for this).

2. Through IntelliJ: In order to run the project this way, user needs to run the Main class. 


**Running tests:**

After making a pull from gitlab, all the tests can be run by running each test file separately. The test files are as follows:

1. ApiQueryTest.java : This test class tests whether api is working as expected.

2. FriendListTest.java : This class tests the FriendList class.

3. MovieListTest.java : This class tests the MovieList class.

4. UserTest.java : This class tests the User class.

5. ServerTestDriver.java : Runs all the tests related to the server. (NOTE: Running the server tests will clean all the data which had previously been stored in the server.)
