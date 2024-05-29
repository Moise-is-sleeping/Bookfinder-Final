package ui.ViewModel


import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PostsGroupsViewmodel : ViewModel(){
    // Firebase authentication instance
    private val auth: FirebaseAuth = Firebase.auth
    // Firebase Firestore instance
    private val firestore = Firebase.firestore

    private var  _rating = MutableStateFlow(0)
    var rating: StateFlow<Int> = _rating.asStateFlow()

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

}