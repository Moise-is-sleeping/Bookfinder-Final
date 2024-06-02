package ui.ViewModel


import android.net.Uri
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import data.Models.Doc
import data.Models.Post
import data.Models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PostsGroupsViewmodel : ViewModel(){
    // Firebase authentication instance
    private val auth: FirebaseAuth = Firebase.auth
    // Firebase Firestore instance
    private val firestore = Firebase.firestore

    private var  _rating = MutableStateFlow(0)
    var rating: StateFlow<Int> = _rating.asStateFlow()

    private var  _book = MutableStateFlow(Doc("","",""))
    var book: StateFlow<Doc> = _book.asStateFlow()

    private var  _starColor = MutableStateFlow(listOf(0xFFF8F8E3,0xFFF8F8E3,0xFFF8F8E3,0xFFF8F8E3,0xFFF8F8E3))
    var starColor: StateFlow<List<Long>> = _starColor.asStateFlow()


    fun starRatings(num: Int){
        _rating.value = num
    }

    fun starColorPicker() {
        when(rating.value){
            1 -> {
                _starColor.value = listOf(0xFFF7F772,0xFFF8F8E3,0xFFF8F8E3,0xFFF8F8E3,0xFFF8F8E3)
            }
            2 -> {
                _starColor.value = listOf(0xFFF7F772,0xFFF7F772,0xFFF8F8E3,0xFFF8F8E3,0xFFF8F8E3)
            }
            3 -> {
                _starColor.value = listOf(0xFFF7F772,0xFFF7F772,0xFFF7F772,0xFFF8F8E3,0xFFF8F8E3)
                }
            4 -> {
                _starColor.value = listOf(0xFFF7F772,0xFFF7F772,0xFFF7F772,0xFFF7F772,0xFFF8F8E3)
            }
            5 -> {
               _starColor.value = listOf(0xFFF7F772,0xFFF7F772,0xFFF7F772,0xFFF7F772,0xFFF7F772)
            }
        }
    }

    fun setBook(book: Doc){
        _book.value = book
    }

    fun bookNameLength(name:String):String{
        if (name.length > 18){
            return name.substring(0,15) + "..."
        }else{
            return name
        }
    }

    fun uploadPost(title:String, description: String,book:Doc, imgUri: Uri, succes:()->Unit) {
        if (title == "" || description == "" || _rating.value == 0 || book == Doc("", "", "")) {

        } else {
            viewModelScope.launch(Dispatchers.IO) {
                val post = Post(
                    email = auth.currentUser?.email.toString(),
                    group = "",
                    title = title,
                    description = description,
                    ratings = _rating.value,
                    book = book,
                    imgUri = imgUri
                )
                firestore.collection("Posts")
                    .add(post)
                    .addOnSuccessListener {
                        succes()
                        Log.d(
                            "GUARDAR OK",
                            "Se guardó el usuario correctamente en Firestore"
                        )
                    }
                    .addOnFailureListener {
                        Log.d(
                            "ERROR AL GUARDAR",
                            "ERROR al guardar en Firestore"
                        )
                    }
            }
        }

    }
}