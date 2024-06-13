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


    /**
     * Changes the currently selected group.
     *
     * @param group The new group to select.
     */
    fun changeCurrentGroup(group: GroupState) {
        _currentGroupId.value = group
    }

    /**
     * Sets the current star rating.
     *
     * @param num The new star rating (1-5).
     */
    fun starRatings(num: Int) {
        _rating.value = num
    }

    /**
     * Updates the colors of the star rating indicators based on the current rating.
     */
    fun starColorPicker() {
        _starColor.value = when (rating.value) {
            1 -> listOf(0xFFF7F772, 0xFFF8F8E3, 0xFFF8F8E3, 0xFFF8F8E3, 0xFFF8F8E3)
            2 -> listOf(0xFFF7F772, 0xFFF7F772, 0xFFF8F8E3, 0xFFF8F8E3, 0xFFF8F8E3)
            3 -> listOf(0xFFF7F772, 0xFFF7F772, 0xFFF7F772, 0xFFF8F8E3, 0xFFF8F8E3)
            4 -> listOf(0xFFF7F772, 0xFFF7F772, 0xFFF7F772, 0xFFF7F772, 0xFFF8F8E3)
            5 -> listOf(0xFFF7F772, 0xFFF7F772, 0xFFF7F772, 0xFFF7F772, 0xFFF7F772)
            else -> listOf(0xFFF8F8E3, 0xFFF8F8E3, 0xFFF8F8E3, 0xFFF8F8E3, 0xFFF8F8E3)
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



    /**
     * Sets the currently selected book.
     *
     * @param book The book to select.
     */
    fun setBook(book: Doc) {
        _book.value = book
    }


    /**
     * Truncates a book name to a maximum length of 18 characters, adding "..." if truncated.
     *
     * @param name The book name to truncate.
     * @return The truncated book name.
     */
    fun bookNameLength(name: String?): String {
        return if (!name.isNullOrBlank()) {
            if (name.length > 18) name.substring(0, 15) + "..." else name
        } else {
            ""
        }
    }

    /**
     * Uploads a post to the "Posts" collection in Firestore.
     *
     * @param title Thetitle of the post.
     * @param description The description of the post.
     * @param book The book associated with the post.
     * @param imgUri The URI of the image associated with the post.
     * @param succes A callback function to be executed on successful post upload.*/
    fun uploadPost(
        title: String,
        description: String,
        book: Doc,
        imgUri: String,
        succes: () -> Unit
    ) {
        var postID = ""
        getUserPostId {
            postID = it // Store the retrieved post ID
            if (title == "" || description == "" || _rating.value == 0 || book == Doc("", "", "")) {
                // Handle case where required fields are missing
            } else {
                viewModelScope.launch(Dispatchers.IO) {Log.d("postID", postID)
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
                        ) // Create Post object
                        firestore.collection("Posts")
                            .add(post)
                            .addOnSuccessListener {
                                succes() // Execute success callback
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
    }

    /**
     * Retrieves the number of posts made by the current user and provides it as a String through the callback.
     *
     * @param postID A callback function that receives the count of posts as a String.
     */
    fun getUserPostId(postID: (String) -> Unit) {
        var postCounter = 0 // Initialize post counter
        firestore.collection("Posts")
            .get()
            .addOnSuccessListener {for (item in it.documents) {
                if (item.getString("email") == auth.currentUser?.email) {
                    postCounter += 1 // Increment counter if post belongs to current user
                }
            }
                postID(postCounter.toString()) // Pass the post count to the callback
            }
    }


    /**
     * Retrieves posts from the "Posts" collection in Firestore, ordered by date in descending order,
     * and filters them to includeonly posts from users in the `_friendsList`.
     * The resulting list of posts is then assigned to the `_postsList` LiveData.
     */
    fun getPosts() {
        firestore.collection("Posts")
            .orderBy("date", Query.Direction.DESCENDING) // Order posts bydate (newest first)
            .get()
            .addOnSuccessListener {
                val tempList = mutableListOf<PostSate>()
                for (item in it.documents) {
                    if (_friendsList.contains(item.getString("userName"))) { // Filter posts by friends
                        var post = postState(item)
                        Log.d("posts", post.toString())
                        Log.d("posts", item.getString("userName").toString())
                        tempList.add(post) // Add post to temporary list
                    }
                }
                _postsList.value =tempList // Update LiveData with filtered posts

            }
            .addOnFailureListener {
                Log.d("Fire Store Error", "unable to get posts ")
            }
    }


    /**
     * Converts a Firestore DocumentSnapshot into a PostSate object.
     *
     * @param item The DocumentSnapshot representing a postfrom Firestore.
     * @return A PostSate object representing the post data.
     */
    fun postState(item: DocumentSnapshot): PostSate {
        var date = item.getTimestamp("date")
        val formattedDate =
            SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(date!!.toDate()) // Format date

        var post = PostSate(
            date = formattedDate.toString(),
            email = item.getString("email")!!,
            group = item.getString("group")!!,
            title = item.getString("title")!!,
            description = item.getString("description")!!,
            ratings = item.getLong("ratings")!!.toInt(),
            book = item.get("book")?.let { // Extract book object using Gson
                val gson = Gson()
                val json = gson.toJson(it)
                gson.fromJson(json, Doc::class.java)
            } ?: Doc(), // Use empty Doc if "book" is null
            imgUri = item.getString("imgUri")!!,
            userName = item.getString("userName")!!,
            id = item.getString("id")!!,
            likes = item.get("likes") as MutableList<String>,
            comments = item.get("comments") as MutableList<String>
        ) // Create PostSate object

        return post
    }


    /**
     * Retrieves book information for a given ID and provides it through a callback.
     *
     * @param id The ID of the bookto retrieve information for.
     * @param book A callback function that receives a BookState object containing the book information.
     */
    fun getBookInfo(id: String, book: (book: BookState) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val bookRepo = bookRepository.getBookState(id)
            bookRepo.bookID = id
            book(bookRepo) // Pass the BookState object to the callback
        }
    }


    /**
     * Extracts the ID portion from a string containing "works".
     *
     * @param value The string containing the "works" substring.
     * @return The extracted ID portion of the string.
     */
    fun extractId(value: String): String {
        val end = value.indexOf("works")
        return value.substring(end + 6)
        // Extract ID after "works"
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
                getAuthorID(extractAuthorId(book.authors.toString()), author = { author(it) }) // Fetch and provide author name
            } catch (e: Exception) {
                author("Uknown") // Handle cases where author information is unavailable
            }
        }
    }

    /**
     * Extracts the author ID from a string, handling potential inconsistencies in the input format.
     *
     * @param value The string containingthe author ID.
     * @return The extracted author ID or "unknown" if an exception occurs.
     */
    fun extractAuthorId(value:String): String {
        try {
            val end = value.indexOf(",")
            return value.substring(23,end-1) // Extractauthor ID
        }
        // the api was being very inconsisten with some books, so i had to add an exception
        catch (e:Exception){
            return "unknown" // Return "unknown" for inconsistent cases
        }

    }

    /**
     * Fetches the author's name based on their ID and provides it through a callback.
     *
     * @param id The ID of the author.
     * @param author A callback function that receives the author's name as a String.
     */
    fun getAuthorID(id: String, author: (String) -> Unit) {viewModelScope.launch {
        val result = bookRepository.getAuthor(id)
        author(result.name!!) // Pass the author's name to the callback
    }
    }

    /**
     * Likes or unlikes a post with the given ID, updating the "likes" field in Firestore and providing feedback through a callback.
     *
     * @param id The ID of the post to like or unlike.
     * @param animate A callback function that receives a Boolean indicating whether the post was liked (true) or unliked (false).
     */
    fun likeOrUnlikePost(id:String,animate:(Boolean)->Unit){val db = FirebaseFirestore.getInstance()
        /// Query the Firestore collection "Posts" to find the document that matches the given username
        firestore.collection("Posts")
            .whereEqualTo("id",id)
            .get()
            .addOnSuccessListener {
                val doc = it.documents[0]
                /// Get a reference to the sender's document
                var info = db.collection("Posts").document(doc.id)
                var likes = doc.get("likes") as List<String>

                if (likes.contains(_myUsername)){info.update("likes", FieldValue.arrayRemove(_myUsername)) // Unlike post
                    animate(false)
                }
                else{
                    info.update("likes", FieldValue.arrayUnion(_myUsername)) // Like post
                    animate(true)
                }

            }
    }


    /**
     * Adds a comment to a post with the given ID, updating the "comments" field in Firestore.
     *
     * @param comment The comment text to add.
     * @param postID The ID of the post to add the comment to.
     */
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
                    info.update("comments", FieldValue.arrayUnion(commentMap)) // Add comment
                }
            }
    }

    /**
     * Retrieves comments for a post with the given ID and provides them through a callback.
     *
     * @param id The ID of the post to retrieve comments for.
     * @param comments A callback function that receives a List of Maps representing the comments.
     */
    fun getComments(id:String,comments:(List<Map<String,String>>)->Unit){
        Log.d("id",id)
        /// Query the Firestore collection "Posts" to find the document that matches the given username
        firestore.collection("Posts")
            .whereEqualTo("id",id)
            .get()
            .addOnSuccessListener {
                val doc = it.documents[0]
                var commentsList = doc.get("comments") as List<Map<String,String>>
                comments(commentsList) // Pass the comments to the callback

            }
    }

    /**
     * Checks if the current user has liked a post with the given ID and provides the result through a callback.
     *
     * @param id The ID of the post to check.
     * @param liked A callback function that receives a Boolean indicating whether the post is liked (true) or not (false).
     */
    fun checkPostLike(id:String,liked:(Boolean)->Unit){
        /// Query the Firestore collection "Posts" to find the document that matches the given username
        firestore.collection("Posts")
            .whereEqualTo("id",id)
            .get()
            .addOnSuccessListener {
                val doc = it.documents[0]
                var likes = doc.get("likes") as List<String>
                if (likes.contains(_myUsername)){
                    liked(true) // Indicate post is liked
                }else{
                    liked(false) // Indicate post is not liked
                }
            }
    }

    /**
     * Provides the current user's username through a callback.
     *
     * @param username A callback function that receives the current user's username as a String.
     */
    fun myUsername(username:(String)->Unit){
        username(_myUsername) // Pass the username to the callback
    }



    /**
     * Returns the current user's username.
     *
     * @return The current user's username as a String.*/
    fun userNamegroups(): String {
        return _myUsername
    }


    /**
     * Creates a new group in Firestore with the provided information.
     *
     * @param groupName The name of the group.
     * @param description The description of the group.
     * @param groupMembers A list of usernames of the group members.
     * @param succes A callback function to be executed on successful group creation.
     * @param pfpName The name of the profile picture for the group.
     */

    fun createGroup(groupName:String,description:String,groupMembers:List<String>, succes:()->Unit,pfpName:String){
        viewModelScope.launch(Dispatchers.IO) {
            var groupID = ""
            val members = updateList(groupMembers,_myUsername,2).toMutableList() // Update member list
            getGroupId {
                groupID = it // Store the retrieved group ID

                if (groupName.isNotEmpty() && groupID != "") {
                    val group = GroupState(
                        groupName = groupName,
                        messages = mutableListOf(mapOf(Pair(_myUsername, Message(date = Timestamp.now().toString(), text = "")))), // Initialize messages
                        groupID = groupID,
                        groupDescription = description,
                        members = members,
                        pfpName = pfpName
                    ) // Create GroupState object
                    firestore.collection("Groups")
                        .add(group)
                        .addOnSuccessListener {
                            succes() // Execute success callback
                            Log.d(
                                "GUARDAR OK",
                                "Se guardó el usuario correctamente en Firestore"
                            )
                        }
                        .addOnFailureListener {
                            Log.d(
                                "ERROR AL GUARDAR","ERROR al guardar en Firestore"
                            )
                        }
                }
            }

            Log.d("groups",groupID)

        }
    }

    /**
     * Retrieves a unique group ID by counting existing groups in Firestore and providing it through a callback.
     *
     * @param groupID A callback function that receives the generated group ID as a String.
     */
    fun getGroupId(groupID: (String) -> Unit) {
        var postCounter = 0
        firestore.collection("Groups")
            .get()
            .addOnSuccessListener {
                for (item in it.documents) {
                    postCounter += 1 // Increment counter for each existing group
                }
                groupID(postCounter.toString()) // Pass the generated group ID to the callback

            }
    }

    /**
     * Retrieves groups from Firestore and filters them to include only those the current user is a member of.
     * The resulting list of groups is then assigned to the `_groupsList` LiveData.
     */
    fun getGroups(){
        firestore.collection("Groups")
            .get()
            .addOnSuccessListener {
                val tempList = mutableListOf<GroupState>()
                for (item in it.documents) {val members = item.get("members") as List<String>
                    if (members.contains(_myUsername)) { // Filter groups by membership
                        tempList.add(groupState(item)) // Add group to temporary list
                    }
                }
                _groupsList.value = tempList // Update LiveData with filtered groups
            }
    }

    /**
     * Converts a Firestore DocumentSnapshot into a GroupState object.
     *
     * @param item The DocumentSnapshot representing a group from Firestore.
     * @return A GroupState object representing the group data.
     */
    fun groupState(item: DocumentSnapshot): GroupState {
        val groupState = GroupState(
            groupName = item.getString("groupName")!!,
            messages = item.get("messages") as MutableList<Map<String,Message>>,
            groupID = item.getString("groupID")!!,
            groupDescription = item.getString("groupDescription")!!,
            members = item.get("members") as MutableList<String>,
            pfpName = item.getString("pfpName")!!
        ) // Create GroupState object
        return groupState
    }

    /**
     * Retrieves messages for a group with the given ID and provides them through a callback.
     *
     * @param id The ID of the group to retrieve messages for.
     * @param messages A callback function that receives a List of Maps representing the messages.
     */
    fun getMessages(id:String,messages:(List<Map<String,Map<String,String>>>)->Unit){
        firestore.collection("Groups")
            .whereEqualTo("groupID",id)
            .get()
            .addOnSuccessListener {
                val doc = it.documents[0]
                val messages = doc.get("messages")as List<Map<String,Map<String,String>>>
                messages(messages) // Pass the messages to the callback
                Log.d("messages",messages.toString())
            }
    }



    /**
     * Updates group members by either adding or removing a name based on the 'numb' parameter.
     *
     * @param list The original list of strings.
     * @param name The name to add or remove.
     * @param numb An integer indicating the operation: 1 for removal, 2 for addition.
     * @return The updated list of strings.
     */
    fun updateList(list: List<String>,name: String,numb:Int): List<String> {
        val templist = list.toMutableList()
        if (numb == 1){
            templist.remove(name) // Remove name from list
        }
        else{
            templist.add(name) // Add name to list
        }
        return templist.toList()
    }


    /**
     * Sends a message to a group with the given ID, updating the "messages" field in Firestore.
     *
     * @param username The username of the sender.
     * @param message The Message object to send.
     * @param groupId The ID of the group to send the message to.
     */
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
                info.update("messages", FieldValue.arrayUnion(messageMap)) // Send message

            }
    }




}

