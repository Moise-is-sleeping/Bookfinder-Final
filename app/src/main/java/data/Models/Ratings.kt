package data.Models


/**
 * Data class the receives the information for the ratings from the api
 */
data class Ratings (
    var summary:Summary
)

data class Summary(
    var average:Float
)