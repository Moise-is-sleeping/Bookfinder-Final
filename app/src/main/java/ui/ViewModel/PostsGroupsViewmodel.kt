package ui.ViewModel


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
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.gson.Gson
import data.Util.BookApiService
import data.Util.BookRepository
import ui.state.BookState
import ui.state.GroupState
import ui.state.Message
import java.text.SimpleDateFormat
import java.util.Locale

class PostsGroupsViewmodel : ViewModel() {
    // Firebase authentication instance
    private val auth: FirebaseAuth = Firebase.auth

    // Firebase Firestore instance
    private val firestore = Firebase.firestore

    private var _rating = MutableStateFlow(0)
    var rating: StateFlow<Int> = _rating.asStateFlow()

    private var _myUsername = ""
    private var _friendsList = mutableListOf<String>()


    private var _book = MutableStateFlow(Doc("", "", ""))
    var book: StateFlow<Doc> = _book.asStateFlow()

    private var _currentGroupId = MutableStateFlow(GroupState("", "", "", mutableListOf(), mutableListOf(), ""))
    var currentGroupId: StateFlow<GroupState> = _currentGroupId.asStateFlow()

    private var _postsList = MutableStateFlow(listOf<PostSate>())
    var postsList: StateFlow<List<PostSate>> = _postsList.asStateFlow()

    private var _groupsList = MutableStateFlow(listOf<GroupState>())
    var groupsList: StateFlow<List<GroupState>> = _groupsList.asStateFlow()


    private var _starColor =
        MutableStateFlow(listOf(0xFFF8F8E3, 0xFFF8F8E3, 0xFFF8F8E3, 0xFFF8F8E3, 0xFFF8F8E3))
    var starColor: StateFlow<List<Long>> = _starColor.asStateFlow()


    // Book API service
    private var bookApiService = BookApiService()

    // Book repository
    private var bookRepository = BookRepository(bookApiService)

    init {
        if (auth.currentUser != null){
            getUsersInfo()
        }

    }


    fun changeCurrentGroup(group: GroupState){
        _currentGroupId.value = group
    }

    fun starRatings(num: Int) {
        _rating.value = num
    }

    fun starColorPicker() {
        when (rating.value) {
            1 -> {
                _starColor.value =
                    listOf(0xFFF7F772, 0xFFF8F8E3, 0xFFF8F8E3, 0xFFF8F8E3, 0xFFF8F8E3)
            }

            2 -> {
                _starColor.value =
                    listOf(0xFFF7F772, 0xFFF7F772, 0xFFF8F8E3, 0xFFF8F8E3, 0xFFF8F8E3)
            }

            3 -> {
                _starColor.value =
                    listOf(0xFFF7F772, 0xFFF7F772, 0xFFF7F772, 0xFFF8F8E3, 0xFFF8F8E3)
            }

            4 -> {
                _starColor.value =
                    listOf(0xFFF7F772, 0xFFF7F772, 0xFFF7F772, 0xFFF7F772, 0xFFF8F8E3)
            }

            5 -> {
                _starColor.value =
                    listOf(0xFFF7F772, 0xFFF7F772, 0xFFF7F772, 0xFFF7F772, 0xFFF7F772)
            }
        }
    }

    /**
     * Gets the current user's information from the Firestore collection "Users".
     */
    fun getUsersInfo() {
        /// Querythe Firestore collection "Users" to find the document that matches the current user's email
        firestore.collection("Users")
            .whereEqualTo("email", auth.currentUser!!.email)
            .get()
            .addOnSuccessListener {
                /// Extract the username and profile picture from the matching document
                val doc = it.documents[0].getString("username")
                //Saves the username to a variable in the class
                _myUsername = doc!!

                val doc2 = it.documents[0].data!!.get("addedFriends") as List<*>
                val tempList = mutableListOf<String>()
                for (friend in doc2) {
                    tempList.add(friend.toString())
                }
                _friendsList = tempList
            }
    }


    fun setBook(book: Doc) {
        _book.value = book
    }

    fun bookNameLength(name: String?): String {
        if (!name.isNullOrBlank()) {
            if (name.length > 18) {
                return name.substring(0, 15) + "..."
            } else {
                return name
            }
        } else {
            return ""
        }

    }

    fun uploadPost(
        title: String,
        description: String,
        book: Doc,
        imgUri: String,
        succes: () -> Unit
    ) {
        var postID = ""
        getUserPostId {
            postID = it
            if (title == "" || description == "" || _rating.value == 0 || book == Doc("", "", "")) {

            } else {
                viewModelScope.launch(Dispatchers.IO) {
                    Log.d("postID", postID)
                    if (postID.isNotEmpty()) {
                        val post = Post(
                            date = Timestamp.now(),
                            email = auth.currentUser?.email.toString(),
                            group = "",
                            title = title,
                            description = description,
                            ratings = _rating.value,
                            book = book,
                            imgUri = imgUri,
                            id = _myUsername + it,
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

    fun getUserPostId(postID: (String) -> Unit) {
        var postCounter = 0
        firestore.collection("Posts")
            .get()
            .addOnSuccessListener {
                for (item in it.documents) {
                    if (item.getString("email") == auth.currentUser?.email) {
                        postCounter += 1
                    }
                }
                postID(postCounter.toString())
            }
    }


    fun getPosts() {
        firestore.collection("Posts")
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener {
                val tempList = mutableListOf<PostSate>()
                for (item in it.documents) {
                    if (_friendsList.contains(item.getString("userName"))) {
                        var post = postState(item)
                        Log.d("posts", post.toString())
                        Log.d("posts", item.getString("userName").toString())
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
    fun postState(item: DocumentSnapshot): PostSate {
        var date = item.getTimestamp("date")
        val formattedDate =
            SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(date!!.toDate())


        var post = PostSate(
            date = formattedDate.toString(),
            email = item.getString("email")!!,
            group = item.getString("group")!!,
            title = item.getString("title")!!,
            description = item.getString("description")!!,
            ratings = item.getLong("ratings")!!.toInt(),
            book = item.get("book")?.let {
                val gson = Gson()
                val json = gson.toJson(it)
                gson.fromJson(json, Doc::class.java)
            } ?: Doc(),
            imgUri = item.getString("imgUri")!!,
            userName = item.getString("userName")!!,
            id = item.getString("id")!!,
            likes = item.get("likes") as MutableList<String>,
            comments = item.get("comments") as MutableList<String>
        )

        return post

    }


    fun getBookInfo(id: String, book: (book: BookState) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val bookRepo = bookRepository.getBookState(id)
            bookRepo.bookID = id
            book(bookRepo)
        }
    }

    fun extractId(value: String): String {
        val end = value.indexOf("works")
        return value.substring(end + 6)

    }

    /**
     * Fetches name of the author asynchronously from the id.
     * @param id The ID of the book to fetch the author.
     */
    fun getAuthor(id: String, author: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val book = bookRepository.getBookState(id)

            // If author details are not null or blank
            try {
                getAuthorID(extractAuthorId(book.authors.toString()), author = { author(it) })

            } catch (e: Exception) {
                author("Uknown")
            }
        }
    }

    fun extractAuthorId(value:String): String {
        try {
            val end = value.indexOf(",")
            return value.substring(23,end-1)
        }
        // the api was being very inconsisten with some books, so i had to add an exception
        catch (e:Exception){
            return "unknown"
        }

    }

    fun getAuthorID(id: String, author: (String) -> Unit) {
        viewModelScope.launch {
            val result = bookRepository.getAuthor(id)
            author(result.name!!)

        }
    }

    fun likeOrUnlikePost(id:String,animate:(Boolean)->Unit){
        val db = FirebaseFirestore.getInstance()
        /// Query the Firestore collection "Posts" to find the document that matches the given username
        firestore.collection("Posts")
            .whereEqualTo("id",id)
            .get()
            .addOnSuccessListener {
                val doc = it.documents[0]
                /// Get a reference to the sender's document
                var info = db.collection("Posts").document(doc.id)
                var likes = doc.get("likes") as List<String>

                if (likes.contains(_myUsername)){
                    info.update("likes", FieldValue.arrayRemove(_myUsername))
                    animate(false)
                }
                else{
                    info.update("likes", FieldValue.arrayUnion(_myUsername))
                    animate(true)
                }

            }
    }

    fun addComments(comment:String,postID:String){
        val db = FirebaseFirestore.getInstance()
        /// Query the Firestore collection "Posts" to find the document that matches the given username
        firestore.collection("Posts")
            .whereEqualTo("id",postID)
            .get()
            .addOnSuccessListener {
                val doc = it.documents[0]
                /// Get a reference to the sender's document
                var info = db.collection("Posts").document(doc.id)
                if (comment != ""){
                    var commentMap = mapOf(Pair(_myUsername,comment))
                    info.update("comments", FieldValue.arrayUnion(commentMap))
                }
            }
    }

    fun getComments(id:String,comments:(List<Map<String,String>>)->Unit){
        Log.d("id",id)
        /// Query the Firestore collection "Posts" to find the document that matches the given username
        firestore.collection("Posts")
            .whereEqualTo("id",id)
            .get()
            .addOnSuccessListener {
                val doc = it.documents[0]
                var commentsList = doc.get("comments") as List<Map<String,String>>
                comments(commentsList)

            }
    }

    fun checkPostLike(id:String,liked:(Boolean)->Unit){
        /// Query the Firestore collection "Posts" to find the document that matches the given username
        firestore.collection("Posts")
            .whereEqualTo("id",id)
            .get()
            .addOnSuccessListener {
                val doc = it.documents[0]
                var likes = doc.get("likes") as List<String>
                if (likes.contains(_myUsername)){
                    liked(true)
                }else{
                    liked(false)
                }
            }
    }

    fun myUsername(username:(String)->Unit){
        username(_myUsername)
    }
    fun userNamegroups(): String {
        return _myUsername
    }

    //Working on this
    fun createGroup(groupName:String,description:String,groupMembers:List<String>, succes:()->Unit,pfpName:String){
        viewModelScope.launch(Dispatchers.IO) {
            var groupID = ""
            val members = updateList(groupMembers,_myUsername,2).toMutableList()
            getGroupId {
                groupID = it

                if (groupName.isNotEmpty() && groupID != "") {
                    val group = GroupState(
                        groupName = groupName,
                        messages = mutableListOf(mapOf(Pair(_myUsername, Message(date = Timestamp.now().toString(), text = "")))),
                        groupID = groupID,
                        groupDescription = description,
                        members = members,
                        pfpName = pfpName
                    )
                    firestore.collection("Groups")
                        .add(group)
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

            Log.d("groups",groupID)

        }
    }

    fun getGroupId(groupID: (String) -> Unit) {
        var postCounter = 0
        firestore.collection("Groups")
            .get()
            .addOnSuccessListener {
                for (item in it.documents) {
                        postCounter += 1
                }
                groupID(postCounter.toString())

            }
    }

    fun getGroups(){
        firestore.collection("Groups")
            .get()
            .addOnSuccessListener {
                val tempList = mutableListOf<GroupState>()
                for (item in it.documents) {
                    val members = item.get("members") as List<String>
                    if (members.contains(_myUsername)) {
                        tempList.add(groupState(item))
                    }
                }
                _groupsList.value = tempList
            }
    }

    fun groupState(item: DocumentSnapshot): GroupState {
        val groupState = GroupState(
            groupName = item.getString("groupName")!!,
            messages = item.get("messages") as MutableList<Map<String,Message>>,
            groupID = item.getString("groupID")!!,
            groupDescription = item.getString("groupDescription")!!,
            members = item.get("members") as MutableList<String>,
            pfpName = item.getString("pfpName")!!)
        return groupState
    }

    fun getMessages(id:String,messages:(List<Map<String,Map<String,String>>>)->Unit){
        firestore.collection("Groups")
            .whereEqualTo("groupID",id)
            .get()
            .addOnSuccessListener {
                val doc = it.documents[0]
                val messages = doc.get("messages") as List<Map<String,Map<String,String>>>
                messages(messages)
                Log.d("messages",messages.toString())
            }
    }



    fun updateList(list: List<String>,name: String,numb:Int): List<String> {
        val templist = list.toMutableList()
        if (numb == 1){
            templist.remove(name)
        }
        else{
            templist.add(name)
        }
        return templist.toList()
    }


    fun SendMessage(username:String,message:Message,groupId:String){
        val db = FirebaseFirestore.getInstance()
        /// Query the Firestore collection "Posts" to find the document that matches the given username
        firestore.collection("Groups")
            .whereEqualTo("groupID",groupId)
            .get()
            .addOnSuccessListener {
                val doc = it.documents[0]
                /// Get a reference to the sender's document
                var info = db.collection("Groups").document(doc.id)

                var messageMap = mapOf(Pair(username,message))
                info.update("messages", FieldValue.arrayUnion(messageMap))

            }
    }




}

