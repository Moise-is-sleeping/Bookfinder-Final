package ui.state

import data.Models.Authors
import data.Models.Created
import data.Models.Link
import data.Models.Type

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable



data class BookState(
    val title: String? = DEFAUTL_TITLE,
    val covers: List<Int>? = mutableListOf(0),
    val subjects: List<String>? = emptyList(),
    val links: List<Link>? = emptyList(),
    val type: Type? = Type(""),
    @Contextual val description: Any? = "",
    @Contextual  var authors:Any? = "",
    var bookID :String = "",
    val created: Created = Created("")
){
    companion object{
        const val DEFAUTL_TITLE = "none"
    }
}

