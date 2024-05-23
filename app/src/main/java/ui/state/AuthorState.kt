package ui.state

data class AuthorState(
    var name:String? = DEFAULT_AUTHOR
){
    companion object{
        const val DEFAULT_AUTHOR = "Unknown"
    }
}
