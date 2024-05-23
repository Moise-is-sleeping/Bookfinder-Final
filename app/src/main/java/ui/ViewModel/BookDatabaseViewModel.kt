package ui.ViewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import data.Util.BookApiService
import data.Util.BookRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ui.state.BookState

import kotlin.Exception
/**
 * View model responsible for managing the data received from tha api and the information displayed in the ui
 */
class B
class BookDatabaseViewModel(): ViewModel() {
    // Firebase authentication instance
    private val auth: FirebaseAuth = Firebase.auth

    // Firebase Firestore instance
    private val firestore = Firebase.firestore

    // Book API service
    private var bookApiService = BookApiService()

    // Book repository
    private var bookRepository = BookRepository(bookApiService)

    // State flow to track whether a book has been saved
    private var  _hasSaved = MutableStateFlow(false)
    var hasSaved: StateFlow<Boolean> = _hasSaved.asStateFlow()

    // Mutable state flow to hold the list of saved book IDs
    var bookIdList by mutableStateOf(mutableListOf(""))

    // State flow to emit the list of retrieved book IDs
    private var  _retrivedIDList = MutableStateFlow<List<String>>(emptyList())
    var retrivedIDList: StateFlow<List<String>> = _retrivedIDList.asStateFlow()

    // State flow to emit the list of book details
    private var _bookDetailsList = MutableStateFlow<List<BookState>>(emptyList())
    var bookDetailsList: StateFlow<List<BookState>> = _bookDetailsList.asStateFlow()

    // Mutable state to hold the current book ID
    var bookId by mutableStateOf("")
        private set



    /**
     * Saves the book in the firestore database
     *
     * @param onSuccess lambda function that's called if the book is saved successfully
     */
    fun SaveBooks(onSuccess:() -> Unit) {
        val email = auth.currentUser?.email
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val newBook = hashMapOf(
                    "BookId" to bookId,
                    "UserEmail" to email.toString(),
                )

                firestore.collection("SavedBooks")
                    .add(newBook)
                    .addOnSuccessListener {
                        onSuccess()
                    }
                    .addOnFailureListener {

                    }
            } catch (e: Exception){
                Log.d("ERROR GUARDAR NOTA","Error al guardar ${e.localizedMessage} ")
            }
        }
    }

    /**
     * Fetches all the books saved in the database and assigs them to the variables _retrivedIDList and bookIdList
     * although i could have just used one variable...
     */
    fun fetchBooks() {
        val email = auth.currentUser?.email
        firestore.collection("SavedBooks")
            .whereEqualTo("UserEmail", email.toString())
            .addSnapshotListener { querySnapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                val ids = mutableListOf<String>()
                if (querySnapshot != null) {

                    for (doc in querySnapshot.documents) {
                        val bookId = doc.getString("BookId")
                        if (bookId != null){
                            ids.add(bookId)
                            Log.d("retrived entry", "id"+ bookId)
                        }


                    }
                }
                _retrivedIDList.value = ids
                bookIdList = ids
                getBooks()
                Log.d("bookIdList", bookIdList.toString())
            }
    }
    /**
     * deletes the book from the database
     *
     * @param id The ID of the book to be deleted
     */
    fun deleteBooks(id:String){
        firestore.collection("SavedBooks")
            .whereEqualTo("BookId", id)
            .whereEqualTo("UserEmail",auth.currentUser?.email)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    if(document.getString("UserEmail")==auth.currentUser?.email){
                        Log.d("deleted doc", querySnapshot.documents.toString())
                        document.reference.delete()
                            .addOnSuccessListener {
                                Log.d("deleteDocumentByFieldValue", "Document successfully deleted")
                            }
                            .addOnFailureListener { e ->
                                Log.w("deleteDocumentByFieldValue", "Error deleting document", e)
                            }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.w("deleteDocumentByFieldValue", "Error getting documents", e)
            }
    }

    /**
     * Gets information on all the book id that have been retrieved form the fire store
     */
    fun getBooks(){
        viewModelScope.launch(Dispatchers.IO) {
            var templIst = mutableListOf<BookState>()
            for (id in bookIdList){
                val book = bookRepository.getBookState(id)
                book.bookID = id
                templIst.add(book)
            }
            _bookDetailsList.value = templIst
        }
    }

    /**
     * gets the cover of a book
     * @param book the boos whose cover we are going to get
     * @return returns the id of the cover for the book
     */
    fun gotCovers(book:BookState): String {
       return try {
                        book.covers!![0].toString()
                   }catch (e:Exception){
                        "empty"
                   }

    }

    /**
     * initializes the function fetch books
     */

    init {
        fetchBooks()
    }

    /**
     * toggles the value of hassaved from true and false
     */
    fun hasSaved(){
        _hasSaved.value = !_hasSaved.value
    }

    /**
     * Checks if the given id has already been saved or not in order to change the value of hassaved
     * @param Id the id to be checked
     */
    fun hasSavedDefaultValue(Id:String){
        if (Id in bookIdList){
            _hasSaved.value = true
        }else{
            _hasSaved.value = false
        }
        bookId = Id

    }

    /**
     * Checks whether to add a book to the database or remove it
     * @param value the value of whether the book has been saved or not
     */
    fun addIdOrRemove(value:Boolean){
        if (value && bookId !in bookIdList){
            SaveBooks {  }
            bookIdList.add(bookId)
        }else if (!value){
            bookIdList.remove(bookId)
            deleteBooks(bookId)
        }
        Log.d(" _hasSaved",bookIdList.toString() )
    }

}