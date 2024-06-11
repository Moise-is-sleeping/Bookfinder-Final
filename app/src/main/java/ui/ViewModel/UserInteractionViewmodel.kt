package ui.ViewModel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.getField
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File

class UserInteractionViewmodel : ViewModel(){
    // Firebase authentication instance
    private val auth: FirebaseAuth = Firebase.auth
    // Firebase Firestore instance
    private val firestore = Firebase.firestore

    private var myUsername = ""
    private var profilePicture = ""



    private var  _sentRequest = MutableStateFlow(false)
    var sentRequest: StateFlow<Boolean> = _sentRequest.asStateFlow()

    //the book being searched by the user
    private var  _searchValue = MutableStateFlow<String>("")
    var searchValue: StateFlow<String> = _searchValue.asStateFlow()
    //bool on whether the user has started searching or not
    private var  _hasSearched = MutableStateFlow(false)
    var hasSearched: StateFlow<Boolean> = _hasSearched.asStateFlow()

    private var  _resetPfp = MutableStateFlow(false)
    var resetPfp: StateFlow<Boolean> = _resetPfp.asStateFlow()

    private var  _currentFriendsButton = MutableStateFlow(1)
    var currentFriendsButton: StateFlow<Int> = _currentFriendsButton.asStateFlow()

    private var _userNamesList = mutableListOf<String>()

    private var  _matchingUserNamesList = MutableStateFlow(listOf<String>())
    var matchingUserNamesList: StateFlow<List<String>> = _matchingUserNamesList.asStateFlow()

    private var  _friendsList = MutableStateFlow(listOf<String>())
    var friendsList: StateFlow<List<String>> = _friendsList.asStateFlow()

    private var  _friendRequestsList = MutableStateFlow(listOf<String>())
    var friendRequestsList: StateFlow<List<String>> = _friendRequestsList.asStateFlow()


    private var  _currentSelectedAccount = MutableStateFlow("")
    var currentSelectedAccount: StateFlow<String> = _currentSelectedAccount.asStateFlow()

    private var newIMmgeUri: Uri? = null

    fun resetCurrentScreen(){
        _currentFriendsButton.value = 1
    }

    /**
     * Returns the color of the button based on whether it is the current selected button.
     *
     * @param button The ID ofthe button.
     * @return The color of the button as a Long.
     */
    fun buttonColor(button:Int): Long {
        return if (button!=currentFriendsButton.value){
            0xFFFFFFFF
        }else{
            0xDBDBDBD0
        }
    }/**
     * Sets the current selected button.
     *
     * @param button The ID of the button.
     */
    fun currentButton(button: Int){
        _currentFriendsButton.value = button
    }

    /**
     * Sets the current selected user.
     *
     * @param username The username of the selected user.
     */
    fun currentSelectedUser(username: String){
        _currentSelectedAccount.value = username
    }

    /**
     * Gets a list of usernames from the Firestore collection "Users", excluding the current user's username.
     */
    fun getUsernames() {
        /// Get the current user's information
        getUsersInfo()

        /// Query the Firestore collection "Users" to get all documents
        firestore.collection("Users")
            .get()
            .addOnSuccessListener {
                /// If the list of usernames is empty, iteratethrough the documents and add the usernames to the list, excluding the current user's username
                if (_userNamesList.isEmpty()){
                    for (doc in it.documents){
                        if (doc.getString("username").toString() != myUsername){
                            _userNamesList.add(doc.getString("username").toString())
                        }

                    }
                }

            }

    }

    /**
     * Gets the list of friends for the current user from the Firestore collection "Users".
     */
    fun getFriends() {/// Query the Firestore collection "Users" to find the document that matches the current user's email
        firestore.collection("Users")
            .whereEqualTo("email",auth.currentUser?.email)
            .get()
            .addOnSuccessListener {
                /// If there is a matching document, extract the list of added friends and convert it to a mutable list of strings
                if (!it.isEmpty){
                    val docs = it.documents[0].data!!.get("addedFriends") as List<*>
                    val tempList = mutableListOf<String>()
                    for (friend in docs){
                        tempList.add(friend.toString())
                    }
                    _friendsList.value = tempList
                }
                /// Otherwise, set the list of friends to an empty list
                else{
                    _friendsList.value = emptyList()
                }

            }

    }

    /**
     * Gets the information of a selected user from the Firestore collection "Users".
     *
     * @param username The username of theselected user.
     * @param name A lambda expression that receives the full name of the selected user.
     * @param friends A lambda expression that receives a list of the selected user's friends.
     * @param pfp A lambda expression that receives the URL of the selected user's profile picture.
     */fun getSelectedUserInfo(
        username: String,
        name:(String)->Unit,
        friends:(List<String>)->Unit,
        pfp:(String)->Unit
    ) {
        /// Query the Firestore collection "Users" to find the document that matches the given username
        firestore.collection("Users")
            .whereEqualTo("username",username)
            .get()
            .addOnSuccessListener {
                /// Get the current user's information
                getUsersInfo()

                ///Extracts the user's information and passes it to the lambda expressionsif (!it.isEmpty){

                val doc = it.documents[0]
                val userName = doc.getString("fullname")
                val userFriends = doc.data!!.get("addedFriends") as List<*>
                val profileP = doc.getString("profilePicture")
                //Onlys sends the pfp uri if the field isnt empty
                if (profileP != null) {
                    pfp(profileP)
                }

                friends(userFriends as List<String>)
                name(userName.toString())
            }
    }



    /**
     * Gets the current user's information from the Firestore collection "Users".
     */
    fun getUsersInfo(){
        /// Querythe Firestore collection "Users" to find the document that matches the current user's email
        firestore.collection("Users")
            .whereEqualTo("email",auth.currentUser!!.email)
            .get()
            .addOnSuccessListener{
                /// Extract the username and profile picture from the matching document
                val doc = it.documents[0].getString("username")
                //Saves the username to a variable in the class
                myUsername = doc!!
                val doc2 = it.documents[0].getString("profilePicture")
                //Saves the pfp uri to a variable in the class
                profilePicture = doc2!!
            }
    }

    /**
     * Adds or removes a friend request for a given user.
     *
     * @param username The username of the user to add or removeas a friend.
     */
    fun addFriend(username: String){
        val db = FirebaseFirestore.getInstance()

        /// Query the Firestore collection "Users" to find the document that matches the given username
        firestore.collection("Users")
            .whereEqualTo("username",username)
            .get()
            .addOnSuccessListener {
                val doc = it.documents[0]

                /// If the _sentRequest flag is true, add the current user's username to the friend requests list of the given user
                if (_sentRequest.value){
                    var info = db.collection("Users").document(doc.id)
                    info.update("friendRequests", FieldValue.arrayUnion(myUsername))
                }
                /// Otherwise, remove the current user's username from the friend requests list of the given user
                else{
                    var info = db.collection("Users").document(doc.id)
                    info.update("friendRequests", FieldValue.arrayRemove(myUsername))
                }
            }
    }


    /**
     * Checks if the current user has sent a friend request to the given username.
     *
     * @param username The username of the userto check.
     */
    fun checkSentRequest(username: String){
        val db = FirebaseFirestore.getInstance()

        /// Query the Firestore collection "Users" to find the document that matches the given username
        firestore.collection("Users")
            .whereEqualTo("username",username).get()
            .addOnSuccessListener {
                val doc = it.documents[0]

                /// Get the list of friend requests for the given user
                val friendRequests = doc.data!!.get("friendRequests") as MutableList<String>

                /// Check if the current user's username is in the list of friend requests
                // and set the _sentRequest flag to true
                if (myUsername in friendRequests){
                    _sentRequest.value = true
                }
                /// Otherwise, set the _sentRequest flag to false
                else{
                    _sentRequest.value = false
                }
            }
    }

    /**
     * Changes the value of the _sentRequest flag.
     */
    fun changeButton(){
        _sentRequest.value = !_sentRequest.value
    }




    /**
     * Gets a list of friend requests for the current user from the Firestore collection "Users".
     */
    fun getFriendRequests(){
        /// Query the Firestore collection "Users" to find the document that matches the current user's email
        firestore.collection("Users")
            .whereEqualTo("email",auth.currentUser?.email)
            .get()
            .addOnSuccessListener {
                /// If there is a matching document, extract the list of friend requests and convert it to a mutable list of strings
                if (!it.isEmpty){
                    val docs = it.documents[0].data!!.get("friendRequests") as List<*>
                    val tempList = mutableListOf<String>()
                    for (friend in docs){
                        tempList.add(friend.toString())
                    }
                    _friendRequestsList.value = tempList
                }
                ///Otherwise, set the list of friend requests to an empty list
                else{
                    _friendRequestsList.value = emptyList()
                }

            }

    }

    /**
     * Filters through the list of usernames based on the given username.
     *
     * @param username The username to filter by.
     */fun matchingUsernames(username :String){
        viewModelScope.launch {
            var temlist = mutableListOf<String>()

            /// If the username is not null or empty, iterate through the list of usernames and add those that contain the given username to a temporary list
            if (!username.isNullOrEmpty()){
                for (name in _userNamesList){
                    if (name.contains(username)){
                        temlist.add(name)
                    }
                }
            }

            /// Set the value of _matchingUserNamesList to the temporary list
            _matchingUserNamesList.value = temlist
        }
    }


    /**
     * Accepts a friend request from a given username.
     *
     * @param username The username of the user who sent the friendrequest.
     */
    fun acceptFriendRequest(username: String){
        val db = FirebaseFirestore.getInstance()

        /// Query the Firestore collection "Users" to find the document that matches the current user's username
        firestore.collection("Users")
            .whereEqualTo("username",myUsername)
            .get()
            .addOnSuccessListener {
                val doc = it.documents[0]

                /// Get a reference to the current user's document
                var info = db.collection("Users").document(doc.id)

                /// Remove the sender's username from the current user's friend requests list
                info.update("friendRequests", FieldValue.arrayRemove(username))

                /// Add the sender's username to the current user's added friends list
                info.update("addedFriends", FieldValue.arrayUnion(username))

                /// Add thecurrent user's username to the sender's added friends list
                addUserNameToSendersFriendList(username)

            }
    }

    /**
     * Adds the current user's username to the added friends list of the user who sent the request.
     *
     * @param username The username of the user to add the current user's username to.
     */
    fun addUserNameToSendersFriendList(username: String){
        val db = FirebaseFirestore.getInstance()

        /// Query the Firestore collection "Users" to find the document that matches the given username
        firestore.collection("Users")
            .whereEqualTo("username",username)
            .get()
            .addOnSuccessListener {
                val doc = it.documents[0]

                /// Get a reference to the sender's document
                var info = db.collection("Users").document(doc.id)

                /// Add the current user's username to the sender's added friends list
                info.update("addedFriends", FieldValue.arrayUnion(myUsername))

            }
    }


    /**
     * Deletes a friend request .
     * @param username The username of the user to delete the friend request.
     */
    fun deleteRequest(username: String){
        val db = FirebaseFirestore.getInstance()
        /// Query the Firestore collection "Users" to find the document that matches the current user's username
        firestore.collection("Users")
            .whereEqualTo("username",myUsername).get()
            .addOnSuccessListener {
                val doc = it.documents[0]
                /// Get a reference to the current user's document
                var info = db.collection("Users").document(doc.id)
                /// Remove the given username from the current user's friend requests list
                info.update("friendRequests", FieldValue.arrayRemove(username))
            }
    }

    /**
     * Deletes a friend from the current user's added friends list.
     *
     * @param username The username of the friend todelete.
     */
    fun deleteFriendFromCurrentUser(username: String){
        val db = FirebaseFirestore.getInstance()

        /// Query the Firestore collection "Users" to find the document that matches the current user's username
        firestore.collection("Users")
            .whereEqualTo("username",myUsername)
            .get()
            .addOnSuccessListener {
                val doc = it.documents[0]

                /// Get a reference to the current user's document
                var info = db.collection("Users").document(doc.id)

                /// Remove the given username from the current user's added friends list
                info.update("addedFriends", FieldValue.arrayRemove(username))

            }
    }

    /**
     * Removes a friend from both the current user's and the given username's added friends lists.
     * @paramusername The username of the friend to remove.
     */
    fun removeFriendFromBothUsers(username: String){
        val db = FirebaseFirestore.getInstance()

        /// Query the Firestore collection "Users" to find the document that matches the given username
        firestore.collection("Users")
            .whereEqualTo("username",username)
            .get()
            .addOnSuccessListener {
                val doc = it.documents[0]
                /// Get a reference to the given user's document
                var info = db.collection("Users").document(doc.id)
                /// Remove the current user's username from the given user's added friends list
                info.update("addedFriends", FieldValue.arrayRemove(myUsername))
                /// Delete the given user from the current user's added friends list
                deleteFriendFromCurrentUser(username)
            }
    }



    /**
     * Gets the profile picture of a user from Firebase Storage.
     *
     * @param imageUri A lambda expression that receives the Uriof the profile picture.
     * @param username The username of the user to get the profile picture for.
     */
    fun getImageFromFirebase(imageUri:(Uri)->Unit, username:String){
        var pfpName = ""

        /// Get the user's profile picture name
        getSelectedUserInfo(username,name={},friends={},pfp={
            pfpName = it

            /// Get a reference to the profile picture in Firebase Storage
            val storageRef = FirebaseStorage.getInstance().reference
            val imageRef = storageRef.child("images/$pfpName")

            /// Download the profile picture and pass it to the imageUri lambda expression
            imageRef.downloadUrl.addOnSuccessListener {

                imageUri(it)

                Log.d("image",it.toString())
            }


        })
    }
    fun checkUri(currntUri:Uri?){



        if (newIMmgeUri != null && currntUri != null){
            val new = newIMmgeUri.toString().substring(newIMmgeUri.toString().indexOf("photopicker")+18)
            val old = currntUri.toString().substring(84,currntUri.toString().indexOf("?alt"))
            if (new == old){
                resetPfpValue()
                newIMmgeUri = null
            }
        }



    }


    fun resetPfpValue(){
        _resetPfp.value = !_resetPfp.value
    }

    /**
     * Uploads an image to Firebase Storage.
     *
     * @param imageUri The Uri of the image to upload.
     */fun uploadImageToFirebase(imageUri: Uri?){
        /// Check if the imageUri is not null
        if(imageUri!=null){

            /// Get the name of the image file
            val fileName = File(imageUri!!.path).name

            newIMmgeUri = imageUri
            /// Get a reference to the Firebase Storage bucket
            val storageRef = FirebaseStorage.getInstance().reference

            /// Create a reference to the image file in the Firebase Storage bucket
            val imageRef = storageRef.child("images/$fileName")

            /// Upload the image file to Firebase Storage
            imageRef.putFile(imageUri!!)
                .addOnProgressListener { taskSnapshot ->
                    val progress = (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount

                    Log.d("Upload Progress", "$progress%")
                    // Update UI withprogress if needed
                }
                .addOnSuccessListener {
                    /// Update the user's profile picture name in Firestore
                    updatePfpName(fileName)

                }
                .addOnFailureListener {
                    Log.d("error",it.toString())

                }
        }
    }



    /**
     * Updates the user's profile picture name in Firestore.
     *
     * @param pfpName The name of the newprofile picture.
     */
    fun updatePfpName(pfpName:String){
        val db = FirebaseFirestore.getInstance()

        /// Query the Firestore collection "Users" to find the document that matches the current user's username
        firestore.collection("Users")
            .whereEqualTo("username",myUsername)
            .get()
            .addOnSuccessListener {
                val doc = it.documents[0]

                /// Get a reference to the current user's document
                var info = db.collection("Users").document(doc.id)

                /// Update the user's profile picture name
                info.update("profilePicture",pfpName)
            }
            .addOnFailureListener {
                Log.d("error",it.toString())
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

        _hasSearched.value = false
    }

    fun clear(){
        _matchingUserNamesList.value = emptyList()
    }

    fun myUserName(): String {
        return myUsername
    }


    fun updateUserInfo(fullname: String,about: String, succes:(Boolean)->Unit){
        val db = FirebaseFirestore.getInstance()
        /// Query the Firestore collection "Users" to find the document that matches the current user's username
        firestore.collection("Users")
            .whereEqualTo("username",myUsername)
            .get()
            .addOnSuccessListener {
                val doc = it.documents[0]

                /// Get a reference to the current user's document
                val info = db.collection("Users").document(doc.id)
                if (fullname.isNotEmpty()){
                    info.update("fullname", fullname)
                }
                if (about.isNotEmpty()){
                    info.update("bio",about)
                }
                if (fullname.isNotEmpty() || about.isNotEmpty()){
                    succes(true)
                }
            }
            .addOnFailureListener{
                Log.d("error",it.toString())
            }
    }

    fun getUserStats(friends: (Int) -> Unit, posts: (Int) -> Unit){
        getPostNumber {
            posts(it)
        }
        getFriendNumber {
            friends(it)
        }
    }

    fun getPostNumber(posts: (Int) -> Unit){
        firestore.collection("Posts")
            .whereEqualTo("userName",myUsername)
            .get()
            .addOnSuccessListener {
                var number = it.documents.size
                posts(number)

            }
    }

    fun getFriendNumber(friends: (Int) -> Unit){
        firestore.collection("Users")
            .whereEqualTo("username",myUsername)
            .get()
            .addOnSuccessListener {
                val doc = it.documents[0]
                val list = doc.data!!.get("addedFriends") as List<*>
                val number = list.size
                friends(number)
            }
    }
}