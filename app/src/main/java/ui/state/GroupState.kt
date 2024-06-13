package ui.state

data class GroupState (
    var groupName: String,
    var groupID: String,
    var groupDescription: String,
    var messages : MutableList<Map<String, Message>>,
    var members: MutableList<String>,
    var pfpName:String
)
data class Message(
    val date: String,
    val text: String

)
