package ui.state




/**
 * Data class representing the state of a user.
 *
 * @property userId The unique identifier of the user.
 * @property email The user's email address.
 * @property username The user's username.
 * @property fullname The user's full name.
 * @property savedBooks A list of books that the user has saved.
 * @property friendRequests A list of friend requests that the user has received.
 * @property addedFriends A list of friends that the user has added.
 * @property profilePicture The URL of the user's profile picture.
 * @property bio A short biography about the user.
 */
data class UserState(
    val userId: String = "",
    val email: String = "",
    val username:String = "",
    val fullname :String = "",
    val savedBooks:MutableList<String> = mutableListOf(),
    val friendRequests:MutableList<String>  = mutableListOf(),
    val addedFriends:MutableList<String> = mutableListOf(),
    val profilePicture:String = "",
    val bio:String = "User.png",
)
