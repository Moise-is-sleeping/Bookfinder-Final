package ui.Screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults.textFieldColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.calculator.bookfinder.addpicture.AddPicture
import com.calculator.bookfinder.postheader.PostHeader
import com.calculator.bookfinder.tagbook.TagBook

@OptIn(ExperimentalMaterial3Api::class)
@Preview()
@Composable
fun PostScreen(){
    var postTitle by remember {
        mutableStateOf("")
    }
    var postDescription by remember {
        mutableStateOf("")
    }


    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally){
        PostHeader(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.07f))
        Spacer(modifier = Modifier.height(30.dp))
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = postTitle ,
            onValueChange ={
                postTitle = it
            },
            placeholder ={
                Text(text = "Add a title")
            },
            colors = textFieldColors(
                focusedContainerColor = Color(0xFFE6E5E5),
                unfocusedContainerColor = Color.Transparent,
                cursorColor = Color.Black,
                focusedIndicatorColor = Color.Black,
                unfocusedIndicatorColor = Color.Black
            )
        )
        Spacer(modifier = Modifier.height(10.dp))
        TextField(
            modifier = Modifier.fillMaxWidth().height(300.dp),
            value = postDescription ,
            onValueChange ={
                postDescription = it
            },
            placeholder ={
                Text(text = "Add a Description")
            },
            colors = textFieldColors(
                focusedContainerColor = Color(0xFFE6E5E5),
                unfocusedContainerColor = Color.Transparent,
                cursorColor = Color.Black,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row (modifier = Modifier.fillMaxWidth()){
            TagBook(
                modifier = Modifier
                    .height(42.dp)
                    .width(167.dp))
            AddPicture(
                modifier = Modifier
                    .height(54.dp)
                    .width(54.dp)
            )
        }

    }
}