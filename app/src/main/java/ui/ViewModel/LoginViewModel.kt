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
import data.Models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception

/**
 * View model responsible for managing the data received from tha api and the information displayed in the ui
 */
class LoginViewModel:ViewModel(){
    // Firebase authentication instance
    private val auth:FirebaseAuth= Firebase.auth
    // Firebase Firestore instance
    private val firestore = Firebase.firestore

    //if the information is wrong its set to true
    private var  _wrongInfo = MutableStateFlow<Boolean>(false)
    var wrongInfo: StateFlow<Boolean> = _wrongInfo.asStateFlow()

    private var  _displayErrorMessage = MutableStateFlow<Boolean>(false)
    var displayErrorMessage: StateFlow<Boolean> = _displayErrorMessage.asStateFlow()

    private var  _errorMessage = MutableStateFlow<String>("")
    var errorMessage: StateFlow<String> = _errorMessage.asStateFlow()


    //stores the email
    var email by mutableStateOf("")
        private set
    //Stores the password
    var password by mutableStateOf("")
        private set
    var name by mutableStateOf("")
        private set
    var username by mutableStateOf("")
        private set
    var profilePicture by mutableStateOf("")
        private set
    var bio by mutableStateOf("")
        private set

    /**
     * Connects to firebase and authenticates the user
     * @param onSuccess lambda function to specific what happens if the authentication is successful
     */
    fun login(onSuccess: () -> Unit){
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            onSuccess()
                        } else {
                            _wrongInfo.value = true
                            _displayErrorMessage.value = true
                            _errorMessage.value = task.exception!!.localizedMessage.toString()
                            Log.d("ERROR EN FIREBASE",_displayErrorMessage.value.toString())
                        }
                    }
            } catch (e: Exception){
                Log.d("ERROR EN JETPACK", "ERROR: ${e.localizedMessage}")
            }
        }
    }

    /**
     * Connects to firebase and creates a user
     * @param onSuccess lambda function to specific what happens if the user creation is successful
     */
    fun createUser(onSuccess: () -> Unit){
        viewModelScope.launch {
                try {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                saveUser()
                                onSuccess()
                            } else {
                                Log.d("ERROR EN FIREBASE","Error al crear usuario")
                                _wrongInfo.value = true
                                _displayErrorMessage.value = true
                                _errorMessage.value = task.exception!!.localizedMessage.toString()
                            }
                        }
                } catch (e: Exception){
                    Log.d("ERROR CREAR USUARIO", "ERROR: ${e.localizedMessage}")
                }
        }
    }

    /**
     * Connects to firebase and creates a user
     * @param onSuccess lambda function to specific what happens if the user creation is successful
     */
    private fun saveUser(){
        val id = auth.currentUser?.uid
        val email = auth.currentUser?.email
        viewModelScope.launch(Dispatchers.IO) {
            val user = User(
                userId = id.toString(),
                email = email.toString(),
                username = username,
                fullname = name,
                savedBooks = mutableListOf(),
                addedFriends = mutableListOf(),
                friendRequests = mutableListOf(),
                profilePicture = "User.png",
                bio = bio
            )
            firestore.collection("Users")
                .add(user)
                .addOnSuccessListener { Log.d("GUARDAR OK", "Se guardó el usuario correctamente en Firestore") }
                .addOnFailureListener { Log.d("ERROR AL GUARDAR", "ERROR al guardar en Firestore") }
        }
    }
    /**
     * Checks if the given username is available.
     *
     * @param onSuccess A lambda expression that will be executed if the username is available.
    */
    fun checkUserName(onSuccess: () -> Unit){
        viewModelScope.launch(Dispatchers.IO) {
            /// Query the Firestore collection "Users" to find documents where the "username" field matches the given username
            firestore.collection("Users")
                .whereEqualTo("username",username)
                .get()
                .addOnSuccessListener {
                    Log.d("username",it.documents.toString())

                    /// If there are any matching documents, it means the username is already in use
                    if (it.documents.size > 0){
                        _wrongInfo.value = true
                        _displayErrorMessage.value = true
                        _errorMessage.value = "Username already in use"
                    }
                    /// Otherwise, the username is available, so create the user
                    else{
                        createUser { onSuccess() }
                    }

                }
                /// Ifthere is an error querying the Firestore collection, log the error
                .addOnFailureListener {

                }
        }
    }


    /**
     *Resets the value of the wrongInfo variable
     */
    fun changeError(){
        _wrongInfo.value = false
    }

    /**
     * Updates the user email variable
     */
    fun changeEmail(email: String) {
        this.email = email
    }

    /**
     * Updates the user password
     */
    fun changePassword(password: String) {
        this.password = password
    }

    /**
     * Changes the name of the user.
     *
     * @param name The new name for the user.
     */
    fun changeName(name: String) {
        this.name = name
    }

    /**
     * Changes the username of the user.
     *
     * @param username The new username for the user.
     */
    fun changeUsername(username: String) {
        this.username = username
    }

    /**
     * Resets all the fields of the user to empty strings.
     */
    fun reset(){
        name = ""
        username = ""
        email = ""
        password = ""
    }



    fun displayError(){
        _displayErrorMessage.value = true
    }

    fun DontdisplayError(){
        _displayErrorMessage.value = false
    }


}