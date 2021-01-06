import java.util.ArrayList;

public class FriendList {
  public ArrayList<String> confirmedFriends;

  public ArrayList<String> friendInvites;

  public ArrayList<String> pendingInvites;

  public FriendList(){
    confirmedFriends = new ArrayList<>();
    friendInvites = new ArrayList<>();
    pendingInvites = new ArrayList<>();
  }

  public void addFriend(String username){
    confirmedFriends.add(username);
  }

  public void removeFriend(String username){
    confirmedFriends.remove((Object)username);
  }

  public void acceptInvite(String username){
    friendInvites.remove(username);
    confirmedFriends.add(username);
  }

  public void rejectInvite(String username){
    friendInvites.remove(username);
  }

  public void addInvitation(String username){
    friendInvites.add(username);
  }

  public void addPending(String username){pendingInvites.add(username); }

  public static void main(String[] args){

  }
}
