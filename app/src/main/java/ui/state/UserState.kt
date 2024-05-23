package ui.state




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
