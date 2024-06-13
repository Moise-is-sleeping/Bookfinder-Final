package data.Routes



sealed class Routes(val route :String) {
    object LoginScreen : Routes("LoginScreen")
    object HomeScreen :Routes("HomeScreen")
    object RegisterScreen:Routes("RegisterScreen")
    object SearchScreen:Routes("SearchScreen")
    object BookDescriptionScreen:Routes("BookDescriptionScreen")
    object SavedScreen:Routes("SavedScreen")
    object FriendsScreen:Routes("FriendsScreen")
    object UsersProfileScreen:Routes("UsersProfileScreen")
    object SettingsScreen:Routes("SettingsScreen")
    object PostScreen:Routes("PostScreen")
    object GroupsScreen:Routes("GroupsScreen")
    object MessagesScreen:Routes("MessagesScreen")


}