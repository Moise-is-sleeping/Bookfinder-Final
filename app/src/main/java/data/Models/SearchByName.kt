package data.Models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * Data class that recives the information from the api when searching by name
 */
data class SearchByName(
    val docs: List<Doc>,
    @SerialName("num_found") val numFound: Int,
)


data class Doc(
    val key: String? = null, // Provide default values (null in this case)
    val title: String? = null,
    val first_publish_year: String? =null
) {
    constructor() : this(null, null, null) // No-argument constructor
}