package ui.state

import data.Models.Doc
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


data class SearchByNameState(
    @Contextual val docs: List<Doc> = emptyList(),
    @SerialName("num_found") val numFound: Int = -1,
)


