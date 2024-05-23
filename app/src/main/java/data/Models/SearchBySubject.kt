package data.Models


/**
 * Data class that recives the information from the api when searching by subject
 */
data class SearchBySubject(
    var works : List<Works>,


)

/**
 * Data class with the book information received in a list when searching by subject
 */
data class Works(
    var key :String,
    var title:String,
    var authors:List<Authors>,
    var cover_id :Int,

)

data class Authors(
    var key: String,
    var name:String
)