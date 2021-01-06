import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class FriendListTest {
  @Test
  void testAddingAndRemovingFriends() {
    FriendList friendList = User.getFriendList();
    friendList.addFriend("1");
    friendList.addFriend("2");
    friendList.addInvitation("3");
    friendList.acceptInvite("3");
    Assertions.assertAll(
        "Adding friends",
        () -> assertEquals("[1, 2, 3]", friendList.confirmedFriends.toString()),
        () -> assertEquals("[]", friendList.friendInvites.toString()));

    friendList.removeFriend("2");
    Assertions.assertEquals("[1, 3]", friendList.confirmedFriends.toString());

    friendList.addInvitation("4");
    friendList.rejectInvite("4");
    Assertions.assertAll(
        "Rejecting friends",
        () -> assertEquals("[1, 3]", friendList.confirmedFriends.toString()),
        () -> assertEquals("[]", friendList.friendInvites.toString()));
  }
}

