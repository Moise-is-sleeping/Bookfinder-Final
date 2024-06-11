package data.Util

import com.google.gson.GsonBuilder
import data.Models.Author
import data.Models.Book
import data.Models.Ratings
import data.Models.SearchByName
import data.Models.SearchBySubject
import retrofit2.Response

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


/**
 * Defines the end points in order to access the api and receive the information about books
 */
interface RetrofitApi{
    /**
     * Receives the information on one specific book
     * Uses the @Get tag to specify the end point and the @Path tag to add a placeholder variable to add the parameters
     * @return The book information
     */
    @GET("works/{id}.json/")
    suspend fun getBooksApi(@Path(value ="id")id:String): Response<Book>

    /**
     * Receives a list with books about a certain subject
     * Uses the @Get tag to specify the end point and the @Path tag to add a placeholder variable to add the parameters
     * @return The data class with all the books in a list
     */
    @GET("/subjects/{subject}.json?limit=100")
    suspend fun searchBooksbySubjectApi(@Path(value ="subject")id:String): Response<SearchBySubject>

    /**
     * Receives the ratings on one specific book
     * Uses the @Get tag to specify the end point and the @Path tag to add a placeholder variable to add the parameters
     * @return The data class which contains the average rating
     */
    @GET("https://openlibrary.org/works/{id}/ratings.json")
    suspend fun getRatings(@Path(value = "id")id:String):Response<Ratings>


    /**
     * Receives the name of the author
     * Uses the @Get tag to specify the end point and the @Path tag to add a placeholder variable to add the parameters
     * @return The data class which contains the author name
     */
    @GET("/authors/{id}.json")
    suspend fun getAuthor(@Path(value = "id")id:String):Response<Author>

    /**
     * Receives a list with books with the same or similar name
     * Uses the @Get tag to specify the end point and the @Path tag to add a placeholder variable to add the parameters
     * @return The data class with all the books in a list
     */
    @GET("search.json")
    suspend fun searchBooksbyNameApi(@Query("title")search:String, @Query("mode")mode:String="everything", @Query("limit")limit:String="20"):Response<SearchByName>
}



/**
 * Provides methods to interact with the Open Library API for retrieving book information.
 */
class BookApiService() {
    /**
     * Sets the serializer to lenient so it ignores certain inconsistencies in the received json
     */
    private val gson = GsonBuilder()
        .setLenient()
        .create()

    /**
     * Retrofit object used for making API calls.
     */
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://openlibrary.org/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()


    /**
     * Retrieves information about a specific book.
     * @param id The identifier of the book.
     * @return the information received from the api containing information about the book.
     */
    suspend fun getBook(id :String) : Response<Book>{
        return retrofit.create(RetrofitApi::class.java).getBooksApi(id)
    }
    /**
     * Retrieves ratings for a specific book.
     * @param id The identifier of the book.
     * @return the information received from the api containing ratings information for the book.
     */
    suspend fun getRatings(id :String) : Response<Ratings>{
        return retrofit.create(RetrofitApi::class.java).getRatings(id)
    }
    /**
     * Retrieves information about the author of a specific book.
     * @param id The identifier of the author.
     * @return the information received from the api containing information about the author.
     */
    suspend fun getAuthor(id :String) : Response<Author>{
        return retrofit.create(RetrofitApi::class.java).getAuthor(id)
    }
    /**
     * Searches for books based on a specified subject.
     * @param subject The subject to search for.
     * @return the information received from the api containing a list of books related to the specified subject.
     */
    suspend fun searchBooksBySubject(subject:String):Response<SearchBySubject>{
        return retrofit.create(RetrofitApi::class.java).searchBooksbySubjectApi(subject)
    }

    /**
     * Searches for books based on a specified title.
     * @param search The title to search for.
     * @return the information received from the api containing a list of books related to the specified title.
     */
    suspend fun searchBooksByName(search :String) : Response<SearchByName> {
        return retrofit.create(RetrofitApi::class.java).searchBooksbyNameApi(search)
    }
}

