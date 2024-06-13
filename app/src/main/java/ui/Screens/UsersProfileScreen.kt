package ui.Screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.calculator.bookfinder.R
import com.calculator.bookfinder.accountbuttons.AccountButtons
import com.calculator.bookfinder.accountbuttons.lindenHill
import com.calculator.bookfinder.addtofavourites.AddToFavourites
import com.calculator.bookfinder.addtofavourites.Property1
import com.calculator.bookfinder.morebuttons.MoreButtons
import com.calculator.bookfinder.naviagtionbar.NaviagtionBar
import com.calculator.bookfinder.user.User
import com.google.relay.compose.BoxScopeInstance.columnWeight
import com.google.relay.compose.BoxScopeInstance.rowWeight
import com.google.relay.compose.RelayContainer
import data.Routes.Routes
import ui.ViewModel.BookDatabaseViewModel
import ui.ViewModel.BookViewModel
import ui.ViewModel.UserInteractionViewmodel

/**
 * Composable function that displays the profile screen for a selected user.
 *
 * @param bookDatabaseViewModel ViewModel for interacting withthe book database.
 * @param bookViewModel ViewModel for handling book-related data.
 * @param navController Navigation controller for navigating between screens.
 * @param userInteractionViewmodel ViewModel for handling user interactions.
 */
@Composable
fun UsersProfileScreen(bookDatabaseViewModel: BookDatabaseViewModel, bookViewModel: BookViewModel, navController: NavController,userInteractionViewmodel:UserInteractionViewmodel) {
    var moreButton by remember { mutableStateOf(false) }
    val selectedUserName by userInteractionViewmodel.currentSelectedAccount.collectAsState()
    var name by remember { mutableStateOf("") }
    var numfriends by remember { mutableStateOf("") }
    var friends by remember { mutableStateOf(listOf<String>()) }
    val sentRequest by userInteractionViewmodel.sentRequest.collectAsState()
    var buttonName by remember { mutableStateOf("") }




    userInteractionViewmodel.getSelectedUserInfo(selectedUserName,name = {
        name = it
    }, friends = {
        numfriends = it.size.toString()
        friends = it
    },pfp={})
    Column (
        modifier = Modifier
            .background(color = Color(0xFFE5DBD0))
            .fillMaxSize(),){
        Row(
            modifier = Modifier.padding(top = 10.dp, bottom = 5.dp)) {
            Icon(
                Icons.Outlined.ArrowBack,
                contentDescription = "Localized description",
                modifier = Modifier
                    .offset(x = 7.dp, y = 0.dp)
                    .clickable {
                        navController.popBackStack()
                    }
                    .height(39.dp)
                    .width(39.dp)
            )
            Text(text = selectedUserName,fontFamily = lindenHill, fontSize = 20.sp, modifier = Modifier.padding(10.dp))
        }
        Column(modifier = Modifier
            .fillMaxHeight(0.915f)
            .fillMaxWidth()) {
            Row( modifier = Modifier
                .height(120.dp)
                .fillMaxWidth()
                .padding(top = 15.dp), horizontalArrangement = Arrangement.SpaceEvenly){
                Column( horizontalAlignment = Alignment.CenterHorizontally) {
                    RelayContainer (
                        radius = 105.0,
                        modifier = Modifier
                            .height(70.dp)
                            .width(70.dp)){
                        LoadPfp(userInteractionViewmodel,selectedUserName)
                    }


                    Text(text = name,fontFamily = lindenHill, fontSize = 20.sp, modifier = Modifier.padding(top=10.dp))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = numfriends,fontFamily = lindenHill, fontSize = 30.sp)
                    Text(text = "Friends",fontFamily = lindenHill, fontSize = 20.sp,color = Color(
                        0xB7000000))
                }
                Column (horizontalAlignment = Alignment.CenterHorizontally){
                    Text(text = "0",fontFamily = lindenHill, fontSize = 30.sp)
                    Text(text = "Groups",fontFamily = lindenHill, fontSize = 20.sp,color = Color(
                        0xB7000000))
                }
                Column (horizontalAlignment = Alignment.CenterHorizontally){
                    Text(text = "0",fontFamily = lindenHill, fontSize = 30.sp)
                    Text(text = "Posts",fontFamily = lindenHill, fontSize = 20.sp,color = Color(
                        0xB7000000))
                }

            }

            if (userInteractionViewmodel.myUserName() !in friends){
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, bottom = 15.dp),
                    horizontalArrangement = Arrangement.Center) {
                    if (!sentRequest){
                        buttonName = "Add Friend"
                    }else{
                        buttonName = "Requested"
                    }
                    AccountButtons(
                        buttonPressed = {
                            userInteractionViewmodel.addFriend(selectedUserName)
                            userInteractionViewmodel.changeButton()
                        },
                        buttonName = "Login",
                        property1 = com.calculator.bookfinder.accountbuttons.Property1.Variant4,
                        addRemoveRequest =buttonName,
                        modifier = Modifier
                            .rowWeight(1.0f)
                            .columnWeight(1.0f)
                            .width(372.dp)
                            .height(50.dp)
                    )
                }
            }

        }

        NaviagtionBar(

            homebutton = {navController.navigate(Routes.HomeScreen.route)},
            searchButton = {navController.navigate(Routes.SearchScreen.route)},
            savedButton = {
                bookDatabaseViewModel.fetchBooks()
                navController.navigate(Routes.SavedScreen.route)
            },
            moreButton = {moreButton=true},
            modifier = Modifier
                .rowWeight(1.0f)
                .columnWeight(1.0f)
                .fillMaxWidth()
        )
    }



    if (moreButton){
        Box(modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomEnd
        ){
            Box(
                modifier = Modifier
                    .width(205.dp)
                    .height(190.dp)

                    .background(
                        shape = RoundedCornerShape(topStart = 200.dp),
                        color = Color(0xFFE5DBD0)
                    ),
            ) {
                MoreButtons(
                    groupsButton = {},
                    postsButton = {},
                    friendsButton = {
                        navController.navigate(Routes.FriendsScreen.route)
                        userInteractionViewmodel.getUsernames()
                    },
                    closeButton = {moreButton=false},
                    modifier = Modifier
                        .rowWeight(1.0f)
                        .columnWeight(1.0f)
                        .height(193.dp)
                        .width(193.dp)

                )
            }
        }

    }
}