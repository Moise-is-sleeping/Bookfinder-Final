package data.Util

import android.util.Log
import data.Models.Author
import data.Models.Book
import data.Models.Ratings
import data.Models.SearchByName
import data.Models.SearchBySubject
import retrofit2.Response
import ui.state.AuthorState
import ui.state.BookState
import ui.state.RatingsState
import ui.state.SearchByNameState
import ui.state.SearchBySubjectState

/**
 * Repository class for interacting with the Book API service,contains functions that allow this to
 * happen as well as functions that convert the received information to states so they can be used in the UI
 *
 * @param bookapi The instance of BookApiService used for making API calls.
 */
class BookRepository(private val bookapi : BookApiService) {
    var error = false

    /**
     * Searches for books by name and returns the result as a SearchByNameState.
     * @param search The title to search for.
     * @return the information in an SearchByNameState object
     */
    suspend fun searchByNameState(search:String): SearchByNameState {
        val response = bookapi.searchBooksByName(search)
        if (response != null && response.isSuccessful){
            return  response.body()?.toSearchByNameState()?: SearchByNameState()
        }else{
            error = true
            return  SearchByNameState()

        }
    }

    /**
     * Retrieves information about an author by ID and returns the result as an AuthorState.
     * @param id The identifier of the author.
     * @return the information in an AuthorState object
     */
    suspend fun getAuthor(id:String): AuthorState {
        val response = bookapi.getAuthor(id)
        return if (response != null &&  response.isSuccessful){
            response.body()?.toAuthorState()?:AuthorState()
        }
        else{
            error = true
            AuthorState()
        }
    }

    /**
     * Searches for books by subject and returns the result as a SearchBySubjectState.
     * @param subject The subject to search for.
     * @return the information in an SearchBySubjectState object.
     */
    suspend fun searchBySubjectState(subject:String): SearchBySubjectState {
        val response1 = bookapi.searchBooksBySubject(subject)
        if (response1 != null && response1.isSuccessful){
            return response1.body()?.toSearchBySubjectState()?:SearchBySubjectState() }
        else{
            error = true
                return SearchBySubjectState()
        }



    }
    /**
     * Retrieves information about a book by ID and returns the result as a BookState.
     * @param id The identifier of the book.
     * @return the information in an BookState object.
     */
    suspend fun getBookState(id:String): BookState {
        val response = bookapi.getBook(id)
        return if(response != null && response.isSuccessful){
            response.body()?.toBookSate()?: BookState()
        }else{
            error = true
            BookState()
        }
    }
    /**
     * Retrieves ratings for a book by ID and returns the result as a RatingsState.
     * @param id The identifier of the book.
     * @return the information in an RatingsState object.
     */
    suspend fun getRatings(id:String): RatingsState {
        val response = bookapi.getRatings(id)
        return if(response != null && response.isSuccessful){
            response.body()?.toRatingsSate()?: RatingsState()
        }else{
            error = true
            RatingsState()
        }
    }
    /**
     * Converts an Author object to an AuthorState.
     */
    private fun Author.toAuthorState():AuthorState{
        return AuthorState(
            name = this.name
        )
    }

    /**
     * Converts a Ratings object to a RatingsState.
     */
    private fun Ratings.toRatingsSate(): RatingsState {
        return RatingsState(
            summary = this.summary
        )
    }
    /**
     * Converts a SearchByName object to a SearchByNameState.
     */
    private fun SearchByName.toSearchByNameState(): SearchByNameState {
        return SearchByNameState(
            docs = this.docs,
            numFound = this.numFound
        )

    }
    /**
     * Converts a SearchBySubject object to a SearchBySubjectState.
     */
    private fun SearchBySubject.toSearchBySubjectState(): SearchBySubjectState {
        return SearchBySubjectState(
            works = this.works,


        )
    }
    /**
     * Converts a Book object to a BookState.
     */
    private fun Book.toBookSate(): BookState {
        return BookState(
            title = this.title,
            covers = this.covers,
            subjects = this.subjects,
            links = this.links,
            type = this.type,
            description = this.description,
            authors = this.authors,
            created = this.created

        )
    }
}