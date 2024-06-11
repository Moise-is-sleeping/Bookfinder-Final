package data.Util

import android.app.AlertDialog
import android.content.Context
import android.util.Log
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
import java.io.IOException



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

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://openlibrary.org/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    private val api = retrofit.create(RetrofitApi::class.java)

    private fun showErrorDialog(message: String) {
        Log.d("ERROR",message)
    }

    suspend fun getBook(id: String): Response<Book>? {
        return try {
            api.getBooksApi(id)
        } catch (e: IOException) {
            showErrorDialog("No connection or timeout error. Please try again.")
            null
        }
    }

    suspend fun getRatings(id: String): Response<Ratings>? {
        return try {
            api.getRatings(id)
        } catch (e: IOException) {
            showErrorDialog("No connection or timeout error. Please try again.")
            null
        }
    }

    suspend fun getAuthor(id: String): Response<Author>? {
        return try {
            api.getAuthor(id)
        } catch (e: IOException) {
            showErrorDialog("No connection or timeout error. Please try again.")
            null
        }
    }

    suspend fun searchBooksBySubject(subject: String): Response<SearchBySubject>? {
        return try {
            api.searchBooksbySubjectApi(subject)
        } catch (e: IOException) {
            showErrorDialog("No connection or timeout error. Please try again.")
            null
        }
    }

    suspend fun searchBooksByName(search: String): Response<SearchByName>? {
        return try {
            api.searchBooksbyNameApi(search)
        } catch (e: IOException) {
            showErrorDialog("No connection or timeout error. Please try again.")
            null
        }
    }
}

