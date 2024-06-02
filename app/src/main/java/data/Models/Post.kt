package data.Models

import android.net.Uri

data class Post (
    val email:String,
    val group:String,
    val title:String,
    val description:String,
    val ratings:Int,
    val book:Doc,
    val imgUri: Uri
)