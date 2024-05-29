package ui.Screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults.textFieldColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.calculator.bookfinder.accountbuttons.AccountButtons
import com.calculator.bookfinder.accountbuttons.Property1
import com.calculator.bookfinder.addpicture.AddPicture
import com.calculator.bookfinder.header.lancelot
import com.calculator.bookfinder.postheader.PostHeader
import com.calculator.bookfinder.tagbook.TagBook
import com.google.relay.compose.BoxScopeInstance.columnWeight
import com.google.relay.compose.BoxScopeInstance.rowWeight
import ui.ViewModel.PostsGroupsViewmodel

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun PostScreen(navController: NavController,postsGroupsViewmodel: PostsGroupsViewmodel){
    var postTitle by remember {
        mutableStateOf("")
    }
    var postDescription by remember {
        mutableStateOf("")
    }
    val starColors by postsGroupsViewmodel.starColor.collectAsState()


    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally){
        PostHeader(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.07f),
            backButton = {
                navController.popBackStack()
            },
            property1 = com.calculator.bookfinder.postheader.Property1.Default,
            text = "Posts")
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
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
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
        Column(modifier = Modifier) {
            Text(text = "Rating", fontFamily = lancelot, fontSize = 23.sp,
                color = Color(
                    alpha = 255,
                    red = 0,
                    green = 0,
                    blue = 0
                ), modifier = Modifier.padding(start = 10.dp)
            )
            Row(modifier = Modifier.fillMaxWidth()) {
                for (i in 1..5){
                    IconToggleButton(checked = false, onCheckedChange = {
                        postsGroupsViewmodel.starRatings(i)
                        postsGroupsViewmodel.starColorPicker()
                    }) {
                        Icon(
                            modifier = Modifier
                                .size(48.dp),
                            imageVector = Icons.Outlined.Star,
                            contentDescription = "Localized description",
                            tint = Color(starColors[i-1])
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row (modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp)
        ,horizontalArrangement = Arrangement.Center){

            TagBook(
                tagButton = {},
                modifier = Modifier
                    .rowWeight(1.0f)
                    .columnWeight(1.0f)
                    .height(42.dp)
                    .width(167.dp)
            )
            Spacer(modifier = Modifier.fillMaxWidth(0.7f))
            AddPicture(
                modifier = Modifier
                    .height(42.dp)
                    .width(42.dp)
            )

        }
        Spacer(modifier = Modifier.fillMaxHeight(0.15f))
        AccountButtons(
            modifier = Modifier.fillMaxWidth(0.9f).height(42.dp),
            property1 = Property1.Variant5,
            buttonName = "Create Post"
        )

    }
}

@Preview(showBackground = true)
@Composable
fun test(){
    Row(modifier = Modifier.fillMaxWidth()) {
        IconToggleButton(checked = false, onCheckedChange = { /* doSomething() */ }) {
            Icon(
                modifier = Modifier
                    .size(48.dp),
                imageVector = Icons.Outlined.Star,
                contentDescription = "Localized description",
                tint = Color(0xFFF7F772)
            )
        }
    }
}