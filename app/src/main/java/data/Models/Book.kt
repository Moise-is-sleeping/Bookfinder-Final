package data.Models

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import ui.state.BookState


/**
 * data class that receives the information about a specific book from the api
 */
data class Book(
    val title: String? = BookState.DEFAUTL_TITLE,
    val covers: List<Int>? = mutableListOf(0),
    val subjects: List<String>? = emptyList(),
    val links: List<Link>? = emptyList(),
    //the api was super inconsistent when it cames to certain values, it would somtimes give a string
    //and other times an object, so i had to the type as any? to prevent errors
    val type: Type? = Type(""),
    @Contextual val description: Any? = "",
    @Contextual  var authors:Any = "",
    var created:Created
)

data class Created(
    val value: String
)

@Serializable
data class Link(
    val url: String,
    val title: String,
    val type: Type
)

@Serializable
data class Type(
    val key: String
)



