package data.Models


/**
 * Data class with the information about each user
 */
data class User(
    val userId: String,
    val email: String,
    val username:String,
    val fullname :String,
    val savedBooks:MutableList<String>,
    val friendRequests:MutableList<String>,
    val addedFriends:MutableList<String>,
    val profilePicture:String,
    val bio:String,
)

