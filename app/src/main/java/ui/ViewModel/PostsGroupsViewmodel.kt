package ui.ViewModel


import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import data.Models.Doc
import data.Models.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ui.state.PostSate
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Date
import java.util.Locale

class PostsGroupsViewmodel : ViewModel(){
    // Firebase authentication instance
    private val auth: FirebaseAuth = Firebase.auth
    // Firebase Firestore instance
    private val firestore = Firebase.firestore

    private var  _rating = MutableStateFlow(0)
    var rating: StateFlow<Int> = _rating.asStateFlow()

    private var _myUsername = ""
    private var _friendsList = mutableListOf<String>()


    private var  _book = MutableStateFlow(Doc("","",""))
    var book: StateFlow<Doc> = _book.asStateFlow()


    private var  _postsList = MutableStateFlow(listOf<PostSate>())
    var postsList: StateFlow<List<PostSate>> = _postsList.asStateFlow()


    private var  _starColor = MutableStateFlow(listOf(0xFFF8F8E3,0xFFF8F8E3,0xFFF8F8E3,0xFFF8F8E3,0xFFF8F8E3))
    var starColor: StateFlow<List<Long>> = _starColor.asStateFlow()


    init {
        getUsersInfo()
    }

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

    /**
     * Gets the current user's information from the Firestore collection "Users".
     */
    fun getUsersInfo(){
        /// Querythe Firestore collection "Users" to find the document that matches the current user's email
        Log.d("Currentuser",auth.currentUser!!.email.toString())
        firestore.collection("Users")
            .whereEqualTo("email",auth.currentUser!!.email)
            .get()
            .addOnSuccessListener{
                /// Extract the username and profile picture from the matching document
                val doc = it.documents[0].getString("username")
                //Saves the username to a variable in the class
                _myUsername = doc!!

                val doc2 = it.documents[0].data!!.get("addedFriends") as List<*>
                val tempList = mutableListOf<String>()
                for (friend in doc2){
                    tempList.add(friend.toString())
                }
                _friendsList = tempList
                Log.d("friendsss",_friendsList.toString())
            }
    }










    fun setBook(book: Doc){
        _book.value = book
    }

    fun bookNameLength(name:String?):String{
        if (!name.isNullOrBlank()){
            if (name.length > 18){
                return name.substring(0,15) + "..."
            }else{
                return name
            }
        }
        else{
            return ""
        }

    }

    fun uploadPost(title:String, description: String,book:Doc, imgUri: String, succes:()->Unit) {
        var postID = ""
        getUserPostId {
            postID = it
            if (title == "" || description == "" || _rating.value == 0 || book == Doc("", "", "")) {

            } else {
                viewModelScope.launch(Dispatchers.IO) {
                    Log.d("postID", postID)
                    if (postID.isNotEmpty()){
                        val post = Post(
                            date = Timestamp.now(),
                            email = auth.currentUser?.email.toString(),
                            group = "",
                            title = title,
                            description = description,
                            ratings = _rating.value,
                            book = book,
                            imgUri = imgUri,
                            id = _myUsername+it,
                            userName = _myUsername,
                            likes = mutableListOf(),
                            comments = mutableListOf()
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
        // Check if any of the fields are empty

    }

    fun getUserPostId(postID:(String)->Unit){
        var postCounter = 0
        firestore.collection("Posts")
            .get()
            .addOnSuccessListener {
                for(item in it.documents){
                    if (item.getString("email") == auth.currentUser?.email){
                        postCounter+= 1
                    }
                }
                postID(postCounter.toString())
            }
    }


    fun getPosts(){
        firestore.collection("Posts")
            .get()
            .addOnSuccessListener {
                val tempList = mutableListOf<PostSate>()
                for(item in it.documents){
                    if (_friendsList.contains(item.getString("userName")) ){
                        var post = postState(item)
                        Log.d("posts",post.toString())
                        Log.d("posts",item.getString("userName").toString())
                        tempList.add(post)
                    }
                }
                _postsList.value = tempList


            }
            .addOnFailureListener {
                Log.d("Fire Store Error", "unable to get posts ")
        }
    }

//Fix the error
    fun postState(item: DocumentSnapshot):PostSate{
        var date = item.getTimestamp("date")
        val formattedDate = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(date!!.toDate())


        var post = PostSate(
            date = formattedDate.toString(),
            email = item.getString("email")!!,
            group = item.getString("group")!!,
            title = item.getString("title")!!,
            description = item.getString("description")!!,
            ratings = item.getLong("ratings")!!.toInt(),
            book = item.toObject(Doc::class.java)!!,
            imgUri = item.getString("imgUri")!!,
            userName = item.getString("userName")!!,
            id = item.getString("id")!!,
            likes = item.get("likes") as MutableList<String> ,
            comments = item.get("comments") as MutableList<String>
        )
        return post

    }


    fun editDate(date:String){

    }
}