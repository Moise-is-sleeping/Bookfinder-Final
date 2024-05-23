package ui.state

import data.Models.Authors
import data.Models.Works
import kotlinx.serialization.Serializable

data class SearchBySubjectState(
    var works: List<Works> = emptyList(),

)

data class Works(
    var key :String,
    var title:String,
    var authors: List<Authors>,
    var cover_id :Int
)