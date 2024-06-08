package ui.state

import android.net.Uri
import com.google.type.Date
import data.Models.Doc
import ui.ViewModel.PostsGroupsViewmodel

data class PostSate (
    val date: String,
    val email:String,
    val group:String,
    val title:String,
    val description:String,
    val ratings:Int,
    val book: Doc,
    val imgUri: String,
    val id:String = "",
    val userName:String = "",
    val likes : MutableList<String>,
    val comments:MutableList<String>
)