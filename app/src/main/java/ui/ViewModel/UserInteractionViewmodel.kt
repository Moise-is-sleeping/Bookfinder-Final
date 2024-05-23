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
    fun buttonColor(button:Int): Long {
        return if (button!=currentFriendsButton.value){
            0xFFFFFFFF
        }else{
            0xDBDBDBD0
        }
    }
    fun currentButton(button: Int){
        _currentFriendsButton.value = button
    }

    fun currentSelectedUser(username: String){
        _currentSelectedAccount.value = username
    }

    fun getUsernames() {
        getUsersInfo()
        firestore.collection("Users")
            .get()
            .addOnSuccessListener {
                if (_userNamesList.isEmpty()){
                    for (doc in it.documents){
                        if (doc.getString("username").toString() != myUsername){
                            _userNamesList.add(doc.getString("username").toString())
                        }

                    }
                }

            }

    }

    fun getFriends() {
        firestore.collection("Users")
            .whereEqualTo("email",auth.currentUser?.email)
            .get()
            .addOnSuccessListener {
                if (!it.isEmpty){
                    val docs = it.documents[0].data!!.get("addedFriends") as List<*>
                    val tempList = mutableListOf<String>()
                    for (friend in docs){
                        tempList.add(friend.toString())
                    }
                    _friendsList.value = tempList
                }else{
                    _friendsList.value = emptyList()
                }

            }

    }

    fun getSelectedUserInfo(username: String, name:(String)->Unit, friends:(List<String>)->Unit,pfp:(String)->Unit) {
        firestore.collection("Users")
            .whereEqualTo("username",username)
            .get()
            .addOnSuccessListener {
                getUsersInfo()
                if (!it.isEmpty){
                    val doc = it.documents[0]
                    val userName = doc.getString("fullname")
                    val userFriends = doc.data!!.get("addedFriends") as List<*>
                    val profileP = doc.getString("profilePicture")
                    if (profileP != null) {
                        pfp(profileP)
                    }
                    friends(userFriends as List<String>)
                    name(userName.toString())
                }
            }

    }



    fun getUsersInfo(){
        firestore.collection("Users")
            .whereEqualTo("email",auth.currentUser!!.email)
            .get()
            .addOnSuccessListener{
                val doc = it.documents[0].getString("username")
                myUsername = doc!!
                val doc2 = it.documents[0].getString("profilePicture")
                profilePicture = doc2!!
                Log.d("pfp",profilePicture)
            }
    }

    fun addFriend(username: String){
        val db = FirebaseFirestore.getInstance()
        firestore.collection("Users")
            .whereEqualTo("username",username)
            .get()
            .addOnSuccessListener {
                val doc = it.documents[0]
                if (_sentRequest.value){
                    var info = db.collection("Users").document(doc.id)
                    info.update("friendRequests", FieldValue.arrayUnion(myUsername))
                }else{
                    var info = db.collection("Users").document(doc.id)
                    info.update("friendRequests", FieldValue.arrayRemove(myUsername))
                }
            }
    }
    fun checkSentRequest(username: String){
        val db = FirebaseFirestore.getInstance()
        firestore.collection("Users")
            .whereEqualTo("username",username)
            .get()
            .addOnSuccessListener {
                val doc = it.documents[0]
                Log.d("usernametest",username)
                val friendRequests = doc.data!!.get("friendRequests") as MutableList<String>
                if (myUsername in friendRequests){
                    _sentRequest.value = true
                }else{
                    _sentRequest.value = false
                }
            }
    }

    fun changeButton(){
        _sentRequest.value = !_sentRequest.value
    }




    fun getFriendRequests(){
        firestore.collection("Users")
            .whereEqualTo("email",auth.currentUser?.email)
            .get()
            .addOnSuccessListener {
                if (!it.isEmpty){
                    val docs = it.documents[0].data!!.get("friendRequests") as List<*>
                    val tempList = mutableListOf<String>()
                    for (friend in docs){
                        tempList.add(friend.toString())
                    }
                    _friendRequestsList.value = tempList
                }else{
                    _friendRequestsList.value = emptyList()
                }

            }
    }

    fun matchingUsernames(username :String){
        viewModelScope.launch {
            var temlist = mutableListOf<String>()
            if (!username.isNullOrEmpty()){
                for (name in _userNamesList){
                    if (name.contains(username)){
                        temlist.add(name)
                    }
                }
            }
            _matchingUserNamesList.value = temlist
        }
    }


    fun acceptFriendRequest(username: String){
        val db = FirebaseFirestore.getInstance()
        firestore.collection("Users")
            .whereEqualTo("username",myUsername)
            .get()
            .addOnSuccessListener {
                val doc = it.documents[0]
                var info = db.collection("Users").document(doc.id)
                info.update("friendRequests", FieldValue.arrayRemove(username))
                info.update("addedFriends", FieldValue.arrayUnion(username))
                addUserNameToSendersFriendList(username)

            }
    }
    fun addUserNameToSendersFriendList(username: String){
        val db = FirebaseFirestore.getInstance()
        firestore.collection("Users")
            .whereEqualTo("username",username)
            .get()
            .addOnSuccessListener {
                val doc = it.documents[0]
                var info = db.collection("Users").document(doc.id)
                info.update("addedFriends", FieldValue.arrayUnion(myUsername))

            }
    }
    fun deleteRequest(username: String){
        val db = FirebaseFirestore.getInstance()
        firestore.collection("Users")
            .whereEqualTo("username",myUsername)
            .get()
            .addOnSuccessListener {
                val doc = it.documents[0]
                var info = db.collection("Users").document(doc.id)
                info.update("friendRequests", FieldValue.arrayRemove(username))


            }
    }

    fun deleteFriendFromCurrentUser(username: String){
        val db = FirebaseFirestore.getInstance()
        firestore.collection("Users")
            .whereEqualTo("username",myUsername)
            .get()
            .addOnSuccessListener {
                val doc = it.documents[0]
                var info = db.collection("Users").document(doc.id)
                info.update("addedFriends", FieldValue.arrayRemove(username))

            }
    }

    fun removeFriendFromBothUsers(username: String){
        val db = FirebaseFirestore.getInstance()
        firestore.collection("Users")
            .whereEqualTo("username",username)
            .get()
            .addOnSuccessListener {
                val doc = it.documents[0]
                var info = db.collection("Users").document(doc.id)
                info.update("addedFriends", FieldValue.arrayRemove(myUsername))
                deleteFriendFromCurrentUser(username)

            }
    }



    fun getImageFromFirebase(imageUri:(Uri)->Unit, username:String){
        var pfpName = ""
        getSelectedUserInfo(username,name={},friends={},pfp={
            pfpName = it
            val storageRef = FirebaseStorage.getInstance().reference
            val imageRef = storageRef.child("images/$pfpName")
            imageRef.downloadUrl.addOnSuccessListener {
                imageUri(it)
                Log.d("image",it.toString())
            }

        })
    }

    fun uploadImageToFirebase(imageUri: Uri?){
        if(imageUri!=null){
            val fileName = File(imageUri!!.path).name
            Log.d("UserProfilePicture", "UserProfilePicture: $fileName")
            val storageRef = FirebaseStorage.getInstance().reference
            val imageRef = storageRef.child("images/$fileName")
            imageRef.putFile(imageUri!!)
                .addOnSuccessListener {
                    updatePfpName(fileName)
                    Log.d("upload","uploaded")
                }
                .addOnFailureListener {
                    Log.d("error",it.toString())
                }
        }


    }

    fun updatePfpName(pfpName:String){
        val db = FirebaseFirestore.getInstance()
        firestore.collection("Users")
            .whereEqualTo("username",myUsername)
            .get()
            .addOnSuccessListener {
                val doc = it.documents[0]
                var info = db.collection("Users").document(doc.id)
                info.update("profilePicture",pfpName)
            }.addOnFailureListener {
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
}