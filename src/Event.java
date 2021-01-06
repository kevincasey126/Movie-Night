import java.util.ArrayList;
import java.util.HashMap;

public class Event {
  private String eventName;

  private String eventLocation;

  private String eventDate;

  private String eventOrganizer;

  private ArrayList<String> eventGuestList;

  private String eventID;

  private ArrayList<String> eventMovies;

  private ArrayList<String> eventStreamingServices;

  public Event(String name, String location, String date, String organizer, String id){
    this.eventName = name;
    this.eventLocation = location;
    this.eventDate = date;
    this.eventOrganizer = organizer;
    this.eventID = id;
    this.eventGuestList = new ArrayList<>();
    this.eventMovies = new ArrayList<>();
    this.eventStreamingServices = new ArrayList<>();
  }

  public String getEventName() {
    return eventName;
  }

  public void setEventName(String eventName) {
    this.eventName = eventName;
  }

  public String getEventLocation() {
    return eventLocation;
  }

  public void setEventLocation(String eventLocation) {
    this.eventLocation = eventLocation;
  }

  public String getEventDate() {
    return eventDate;
  }

  public void setEventDate(String eventDate) {
    this.eventDate = eventDate;
  }

  public String getEventOrganizer() {
    return eventOrganizer;
  }

  public void setEventOrganizer(String eventOrganizer) {
    this.eventOrganizer = eventOrganizer;
  }

  public String getEventID() {
    return eventID;
  }

  public void setEventID(String eventID) {
    this.eventID = eventID;
  }

  public ArrayList<String> getEventMovies(){
    return this.eventMovies;
  }

  public void addMovie(String title){ this.eventMovies.add(title); }

  public void removeMovie(String title){
    // movieID has to be casted to Object, otherwise treated as an index
    this.eventMovies.remove((Object)title);
  }

  public ArrayList<String> getEventGuestList() {
    return eventGuestList;
  }

  public void addEventGuest(String id) {
    this.eventGuestList.add(id);
  }

  public void removeEventGuest(String id) {
    this.eventGuestList.remove(id);
  }

  public ArrayList<String> getEventStreamingServices() { return eventStreamingServices; }

  public void addStreamingService(String service) { this.eventStreamingServices.add(service); }

  public void removeStreamingService(String service) { this.eventStreamingServices.remove(service); }

  public static void main(String[] args){

  }
}
