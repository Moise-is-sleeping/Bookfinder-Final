package ui.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.calculator.bookfinder.header.Header
import com.calculator.bookfinder.naviagtionbar.NaviagtionBar
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.calculator.bookfinder.books.Books
import com.calculator.bookfinder.header.lancelot
import com.calculator.bookfinder.morebuttons.MoreButtons
import com.google.relay.compose.BorderAlignment
import com.google.relay.compose.BoxScopeInstance.boxAlign
import com.google.relay.compose.BoxScopeInstance.columnWeight
import com.google.relay.compose.BoxScopeInstance.rowWeight
import com.google.relay.compose.RelayContainer
import com.google.relay.compose.RelayContainerScope
import com.google.relay.compose.relayDropShadow
import data.Routes.Routes
import ui.ViewModel.BookDatabaseViewModel
import ui.ViewModel.BookViewModel
import ui.ViewModel.PostsGroupsViewmodel
import ui.ViewModel.UserInteractionViewmodel

/**
 * Displays information for  the screen with the books that have been saved to favourites
 *
 * @param bookDatabaseViewModel View model containing information about saved books
 * @param bookViewModel view-model that contains the logic behind certain functions in the screen
 * @param navController Controller for navigation between screens
 */
@Composable
fun SavedScreen( postsGroupsViewmodel: PostsGroupsViewmodel, userInteractionViewmodel: UserInteractionViewmodel, bookDatabaseViewModel: BookDatabaseViewModel, bookViewModel: BookViewModel, navController: NavController){
    val bookDetailsList by bookDatabaseViewModel.bookDetailsList.collectAsState()
    var moreButton by remember { mutableStateOf(false) }
    Column (modifier= Modifier
        .fillMaxSize()
        .background(color = Color(bookViewModel.backgroundColor())),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top){
        Header(modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
            userAccountButton = {
                navController.navigate(Routes.SettingsScreen.route)
            })
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            Text(text = "Favourites", fontFamily = lancelot, textAlign = TextAlign.Start, fontSize = 25.sp,
                color = Color(
                    alpha = 255,
                    red = 0,
                    green = 0,
                    blue = 0
                ), modifier = Modifier.padding(15.dp))
        }


        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.915f)){
            items(bookDetailsList){
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(top = 7.dp, start = 15.dp, end = 15.dp)
                    .background(Color.White, RoundedCornerShape(3.dp))
                    .clickable {
                        bookViewModel.getBooks(it.bookID)
                        navController.navigate(Routes.BookDescriptionScreen.route)
                        bookDatabaseViewModel.hasSavedDefaultValue(it.bookID)
                    }){
                    Row(verticalAlignment = Alignment.CenterVertically
                    ) {
                        BooksEdit(
                            modifier = Modifier
                                .height(95.dp)
                                .width(70.dp)
                                .padding( start = 7.dp)
                                .offset(y = 7.dp)
                        ) {
                            //if the function returns empty, the place holder for the cover photo is left
                            if (bookDatabaseViewModel.gotCovers(it) != "empty"){
                                AsyncImage(
                                    model = "https://covers.openlibrary.org/b/id/${it.covers?.get(0)}-M.jpg",
                                    contentDescription = "test",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                        Text(text = it.title.toString(), modifier = Modifier.padding(start =15.dp))
                    }
                }
            }
        }


        NaviagtionBar(

            homebutton = {navController.navigate(Routes.HomeScreen.route)},
            searchButton = {navController.navigate(Routes.SearchScreen.route)},
            savedButton = {},
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
                    groupsButton = {
                        navController.navigate(Routes.GroupsScreen.route)
                    },
                    postsButton = {
                        navController.navigate(Routes.PostScreen.route)
                        postsGroupsViewmodel.getUsersInfo()
                    },
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



