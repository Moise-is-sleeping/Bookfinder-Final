package ui.ViewModel

import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.Models.Authors
import data.Models.Doc
import data.Models.Works
import data.Util.BookApiService
import data.Util.BookRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import ui.state.AuthorState
import ui.state.BookState
import ui.state.RatingsState
import ui.state.SearchByNameState
import ui.state.SearchBySubjectState
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * View model responsible for managing the data received from tha api and the information displayed in the ui
 */
class BookViewModel: ViewModel() {

    private var bookApiService = BookApiService()
    private var bookRepository = BookRepository(bookApiService)
    // list that stores the ratings for the books
    val tempList2 = mutableListOf<Float>()

    //list of the books in the home screen
    private var  _homeBookList = MutableStateFlow<List<Works>>(emptyList())
    var homeBookList: StateFlow<List<Works>> = _homeBookList.asStateFlow()

    //state flow list wit the ratings
    private var  _ratingList = MutableStateFlow<List<Float>>(emptyList())
    var ratingList: StateFlow<List<Float>> = _ratingList.asStateFlow()

    //list that contains the books whgen serached by name
    private var  _bookList = MutableStateFlow<List<Doc?>>(emptyList())
    var bookList: StateFlow<List<Doc?>> = _bookList.asStateFlow()
    //the book being searched by the user
    private var  _searchValue = MutableStateFlow<String>("")
    var searchValue: StateFlow<String> = _searchValue.asStateFlow()
    //bool on whether the user has started searching or not
    private var  _hasSearched = MutableStateFlow(false)
    var hasSearched: StateFlow<Boolean> = _hasSearched.asStateFlow()

    private var  _hasCovers = MutableStateFlow(true)
    var hasCovers: StateFlow<Boolean> = _hasCovers.asStateFlow()

    //Lists for the different categories
    private var  _horrorBookList = MutableStateFlow<List<Works>>(emptyList())
    var horrorBookList: StateFlow<List<Works>> = _horrorBookList.asStateFlow()
    private var  _romanceBookList = MutableStateFlow<List<Works>>(emptyList())
    var romanceBookList: StateFlow<List<Works>> = _romanceBookList.asStateFlow()
    private var  _sciFiBookList = MutableStateFlow<List<Works>>(emptyList())
    var sciFiBookList: StateFlow<List<Works>> = _sciFiBookList.asStateFlow()
    private var  _novelBookList = MutableStateFlow<List<Works>>(emptyList())
    var novelBookList: StateFlow<List<Works>> = _novelBookList.asStateFlow()
    private var  _mysteryBookList = MutableStateFlow<List<Works>>(emptyList())
    var mysteryBookList: StateFlow<List<Works>> = _mysteryBookList.asStateFlow()
    //the id for the current book
    val currentBookId = mutableStateOf("")
    //the object that contains information on the current book
    var _bookDetails = MutableStateFlow(BookState())
    var bookDetails : StateFlow<BookState> = _bookDetails.asStateFlow()
    //the author of the current book
    private var _author = MutableStateFlow(AuthorState())
    var author : StateFlow<AuthorState> = _author.asStateFlow()


    /**
     * Fetches book details asynchronously from the id.
     * @param id The ID of the book to fetch.
     */
    fun getBooks(id:String){
        // Sets the currentBookId to the given id
        currentBookId.value = id
        viewModelScope.launch(Dispatchers.IO) {
            val book = bookRepository.getBookState(id)
            // Attempts to access the cover image URL from the fetched book data
            try {
                book.covers!![0].toString()
            }catch (e:Exception){
                _hasCovers.value = false
            }
            _bookDetails.value = book
            // If author details are not null or blank
            if (!book.authors.toString().isNullOrBlank()){
                getAuthor(extractId(book.authors.toString()))
            }else{
                _author.value = AuthorState("Unknown")
            }
        }
    }


    /**
     * If the descretion contains the name of the object its stored in, the function removes it.

     * @param value The string from which to extract the description.
     * @return The extracted description string or an empty string if an exception occurs.
     */
    fun extractDescription(value: String): String {
        return try {
            if (value.indexOf("{") !=-1){
                value.substring(24,value.length-1)
            }
            else{
                value
            }
        }
        catch (e:Exception){
            ""
        }
    }


    /**
     * Extracts the ID from the value given

     * @param value The string from which to extract the ID.
     * @return The extracted ID string or "unknown" if an exception occurs.
     */
    fun extractId(value:String): String {
        try {
            val end = value.indexOf(",")
            return value.substring(23,end-1)
        }
        // the api was being very inconsisten with some books, so i had to add an exception
        catch (e:Exception){
            return "unknown"
        }

    }

    fun getAuthor(id:String){
        if (id =="unknown"){
            _author.value = AuthorState("unknown")
        }else{
            viewModelScope.launch {
                val result = bookRepository.getAuthor(id)
                _author.value = result
            }
        }

    }


    /**
     * Retrieves author information based on the provided id.
     * @param id The ID of the author to retrieve.
     */
    fun searchBooksByName(search:String) {
        viewModelScope.launch(Dispatchers.IO) {
            // If the ID is "unknown", set the author state to "unknown" because of the inconsistent results fro the api
            val result = bookRepository.searchByNameState(search)
            _bookList.value = result.docs
        }
    }
    /**
     * Searches for books by the provided subject using the book repository. and uses the value provided to update the correct list
     *
     * @param subject The subject to search for.
     * @param value An integer representing the category of books to update:
     *              - 0: Home book list
     *              - 1: Horror book list
     *              - 2: Romance book list
     *              - 3: Sci-Fi book list
     *              - 4: Novel book list
     *              - 5: Mystery book list
     */
    fun searchBooksBySubject(subject:String,value:Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = bookRepository.searchBySubjectState(subject)
            val tempList = mutableListOf<Works>()
            for (doc in result.works){
                tempList.add(doc)
                if (value == 0){
                    getRatings(doc.key.substring(7))
                }
            }
            // Update the corresponding book list state flow based on the provided value
            when(value){
                0 -> _homeBookList.value = tempList
                1 -> _horrorBookList.value = tempList
                2 -> _romanceBookList.value = tempList
                3 -> _sciFiBookList.value = tempList
                4 -> _novelBookList.value = tempList
                5 -> _mysteryBookList.value = tempList
            }

        }
    }

    /**
     * Retrieves ratings for a book with the provided id using the book repository.
     * Updates the rating list state flow with the retrieved ratings.
     *
     * @param id The ID of the book for which ratings are to be retrieved.
     */
    fun getRatings(id:String){
        viewModelScope.launch(Dispatchers.IO) {
                val result = bookRepository.getRatings(id)
                tempList2.add(result.summary.average)
            _ratingList.value = tempList2
        }
    }

    /**
     * Sets the hasSearched state flow to true when the user searches
     */
    fun hasSearched(){
        _hasSearched.value = true
    }
    /**
     * Resets the hasSearched state flow variables when no search operation has been performed.
     */
    fun hasNotSearched(){
        _searchValue.value = ""
        _bookList.value = emptyList()
        _hasSearched.value = false
    }


    /**
     * Returns "unknown" if the provided date is null or empty; otherwise, returns the date.
     *Had to do this due to the api being inconsistent
     * @param date The date string to be checked.
     * @return Either "unknown" if date is null or empty, or the original date
     */
    fun nullDates(date:String): String {
        if (date.isNullOrEmpty()){
            return "unknown"
        }else{
            return date
        }
    }
    /**
     * Updates the search value with the value of the latest search value given by the user.
     *
     * @param newSearchValue The new search value to be set.
     */
    fun updateSearchValue(newSearchValue:String){
        _searchValue.value = newSearchValue
    }
    /**
     * Returns the background color based on whether a search operation has been performed.
     *
     * @return The background color represented as a Long
     */
    fun backgroundColor(): Long {
        if (_hasSearched.value){
            return 0xFFFFFFFF
        }else{
            return 0xFFE5DBD0
        }
    }
    /**
     * Resets the hasCovers and bookDetails state flow variables.
     */
    fun resetHasCovers(){
        _bookDetails.value = BookState()
        _hasCovers.value = true
    }
    /**
     * Initializes the functions so that the categories are loaded when the app is opened
     */
    init {
        searchBooksBySubject("fantasy",0)
        searchBooksBySubject("horror",1)
        searchBooksBySubject("love",2)
        searchBooksBySubject("dystopias",3)
        searchBooksBySubject("novel",4)
        searchBooksBySubject("mystery",5)
    }






}




