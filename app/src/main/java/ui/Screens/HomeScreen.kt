package ui.Screens



import android.content.Context
import android.util.Log
import android.content.res.Resources
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text2.BasicTextField2
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Gif
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults.textFieldColors
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.calculator.bookfinder.accountbuttons.lindenHill
import com.calculator.bookfinder.header.Header

import com.calculator.bookfinder.homepagebooks.BookTitle

import com.calculator.bookfinder.homepagebooks.Books
import com.calculator.bookfinder.homepagebooks.ByAuthor
import com.calculator.bookfinder.icons.Icons
import com.calculator.bookfinder.morebuttons.MoreButtons
import com.calculator.bookfinder.naviagtionbar.NaviagtionBar

import com.calculator.bookfinder.ratings.Property1
import com.calculator.bookfinder.ratings.Ratings
import com.calculator.bookfinder.reviewpost.HomepageBooks
import com.google.relay.compose.BoxScopeInstance.boxAlign

import com.google.relay.compose.BoxScopeInstance.columnWeight
import com.google.relay.compose.BoxScopeInstance.rowWeight
import data.Routes.Routes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ui.ViewModel.BookDatabaseViewModel
import ui.ViewModel.BookViewModel
import ui.ViewModel.PostsGroupsViewmodel
import ui.ViewModel.UserInteractionViewmodel


/**
 * Composable function that displays the main HomeScreen, providing navigation to different sections of the app.
 *
 * @param postsGroupsViewmodel ViewModel for handling post and group related actions.
 * @param bookViewModel ViewModel for handling book-related data and searches.
 * @param navController Navigation controller for navigating between screens.
 * @param bookDatabaseViewModel ViewModel for interacting with the book database.
 * @param userInteractionViewmodel ViewModelfor handling user interactions and data.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(postsGroupsViewmodel: PostsGroupsViewmodel,bookViewModel: BookViewModel,navController:NavController,bookDatabaseViewModel: BookDatabaseViewModel,userInteractionViewmodel: UserInteractionViewmodel){
    var moreButton by remember { mutableStateOf(false) } // State for toggling the "More" button overlay
    var counter by remember { mutableIntStateOf(0) } // Workaround for recomposition
    val currentScreen by userInteractionViewmodel.currentFriendsButton.collectAsState() // Current screen state (Your Feedor Discover)
    val scope = rememberCoroutineScope() // Coroutine scope for bottom sheet
    val scaffoldState = rememberBottomSheetScaffoldState() // Bottom sheet state for comments
    var typedComment by remember { mutableStateOf("") } // State for the current comment being typed
    var myUsername by remember { mutableStateOf("") } // Current username
    var currentPostID by remember {
        mutableStateOf("")
    } // ID of the post for which comments are being displayed
    var userCommentsList by remember {
        mutableStateOf((listOf<Map<String,String>>()))
    } // List of comments for the current post
    val feedRefresh by remember {
        mutableStateOf(0)
    }




    // Fetch initial book and post data
    LaunchedEffect(Unit){
        bookDatabaseViewModel.fetchBooks()
        postsGroupsViewmodel.getPosts()
    }
    // BottomSheetScaffold for displaying comments
    BottomSheetScaffold(
        modifier = Modifier.background(Color.White),
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetContent = {
            Column(
                Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {

                Text(text = "Comments", modifier = Modifier.padding(bottom = 20.dp))
                Row (modifier = Modifier
                    .fillMaxWidth(0.98f)
                    .height(1.dp)
                    .background(Color.LightGray)

                ){}
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(start = 10.dp, bottom = 10.dp),
                    verticalAlignment = Alignment.CenterVertically) {

                    HomepageBooks() {
                        if (myUsername.isNotEmpty()){
                            LoadPfp(userInteractionViewmodel , myUsername )
                        }
                    }
                    // Close keyboard when bottom sheet is closed
                    if (scaffoldState.bottomSheetState.currentValue.toString() == "PartiallyExpanded" ){
                        CloseKeyboard()
                    }


                    TextField(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                        ,
                        value = typedComment,
                        onValueChange = {typedComment = it},
                        colors = textFieldColors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            cursorColor = Color.Black,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        placeholder = {
                            Text(text = "Add a comment...",fontFamily = lindenHill,modifier = Modifier)
                        },
                        singleLine = true,
                    )

                    IconButton(onClick = {
                        postsGroupsViewmodel.addComments(typedComment,currentPostID)
                        typedComment = ""
                    }) {
                        if (typedComment != ""){
                            Icon(
                                modifier = Modifier
                                    .size(24.dp),
                                imageVector = Icons.AutoMirrored.Filled.Send ,
                                contentDescription = "Localized description",

                                )
                        }else{
                            Icon(
                                modifier = Modifier
                                    .size(48.dp),
                                imageVector = Icons.Filled.Gif ,
                                contentDescription = "Localized description")
                        }
                    }
                }
                // Fetch and display comments for the current post
                if (currentPostID != ""){
                    LaunchedEffect(key1 = feedRefresh) {
                        while (true) {
                            delay(2000)
                            postsGroupsViewmodel.getComments(currentPostID, comments = {
                                userCommentsList = it
                            })
                        }
                    }
                }
                if (userCommentsList.isNotEmpty()){
                    LazyColumn() {
                        items(userCommentsList) {
                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .padding(start = 10.dp, bottom = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start)  {
                                HomepageBooks() {
                                    LoadPfp(userInteractionViewmodel, userName = it.keys.first())
                                }
                                Column {
                                    Text(text = it.keys.first(), modifier = Modifier.padding(start = 10.dp),fontFamily = lindenHill, fontSize = 16.sp)
                                    Text(text = it.values.first(), modifier = Modifier.padding(start = 10.dp),fontFamily = lindenHill, fontSize = 19.sp)
                                }

                            }

                        }
                    }
                }

            }
        }) { innerPadding ->
        Text(text = counter.toString())
        Column (modifier= Modifier
            .fillMaxSize()
            .background(color = Color(0xFFE5DBD0)),
            horizontalAlignment = Alignment.CenterHorizontally){
            Header(modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
                userAccountButton = {
                    navController.navigate(Routes.SettingsScreen.route)
                })
            Row(
                Modifier
                    .padding(top = 15.dp)
                    .fillMaxWidth()
                    .height(62.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                Spacer(modifier = Modifier.fillMaxWidth(0.05f))
                FriendButtonEdit(
                    buttonName = "Your Feed",
                    modifier = Modifier
                        .rowWeight(1.0f)
                        .columnWeight(1.0f)
                        .fillMaxHeight(0.6f)
                        .weight(1f)
                        .clickable {
                            userInteractionViewmodel.currentButton(1)
                            counter += 1
                        }, color = userInteractionViewmodel.buttonColor(1)
                )
                Spacer(modifier = Modifier.fillMaxWidth(0.05f))
                FriendButtonEdit(
                    buttonName = "Discover",
                    modifier = Modifier
                        .rowWeight(1.0f)
                        .columnWeight(1.0f)
                        .fillMaxHeight(0.6f)
                        .weight(1f)
                        .clickable {
                            userInteractionViewmodel.currentButton(2)
                            counter += 1
                        }, color = userInteractionViewmodel.buttonColor(2)
                )
                Spacer(modifier = Modifier.fillMaxWidth(0.05f))
            }
            //if the list is empty, it shows the loading icon
            when(currentScreen){
                1->{
                    YourFeed(postsGroupsViewmodel,userInteractionViewmodel,bookDatabaseViewModel,bookViewModel,navController,
                        comments = {
                            scope.launch { scaffoldState.bottomSheetState.expand()  }
                            postsGroupsViewmodel.myUsername { myUsername = it }
                            currentPostID = it
                            postsGroupsViewmodel.getComments(it, comments = {
                                userCommentsList = it
                            } )

                        })
                }
                2->{

                    Discover(bookViewModel,navController,bookDatabaseViewModel)
                }

            }

            NaviagtionBar(
                homebutton = {},
                searchButton = { navController.navigate(Routes.SearchScreen.route)},
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




}

/**
 * Composable function that closes the keyboard.
 */
@Composable
fun CloseKeyboard() {
    val context = LocalContext.current
    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val view = LocalView.current
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}
/**
 * Composable function that displays a list of books in the "Discover" section.
 *
 * @param bookViewModel ViewModel for handling book-related data and searches.
 * @param navController Navigation controller for navigating between screens.
 * @param bookDatabaseViewModel ViewModel for interacting with the book database.
 */
@Composable
fun Discover(bookViewModel: BookViewModel,navController: NavController,bookDatabaseViewModel:BookDatabaseViewModel){
    val list by bookViewModel.homeBookList.collectAsState()
    val ratingList by bookViewModel.ratingList.collectAsState()

    if (list.isEmpty()){
        Column(modifier = Modifier
            .fillMaxWidth(0.9f)
            .fillMaxHeight(0.915f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center){
            Loading(120,90)
        }

    }else{
        LazyColumn(modifier= Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.915f),horizontalAlignment = Alignment.CenterHorizontally){
            items(list){book ->
                HomescreenBooks(title = book.title,
                    author = book.authors[0].name ,
                    picId = book.cover_id,
                    modifier = Modifier
                        .rowWeight(1.0f)
                        .columnWeight(1.0f)
                        .height(225.dp)
                        .width(390.dp)
                        .padding(top = 15.dp, bottom = 15.dp)
                        .clickable {
                            bookViewModel.getBooks(book.key.substring(7))
                            navController.navigate(Routes.BookDescriptionScreen.route)
                            bookDatabaseViewModel.hasSavedDefaultValue(book.key.substring(7))
                        },
                    rating = ratingloader(ratingList,list.indexOf(book)) )
            }
        }
    }

}

/**
 * Composable function that displays the user's feed of posts.
 *
 * @param postsGroupsViewmodel ViewModel for handling post and group related actions.
 * @param userInteractionViewmodel ViewModel for handling user interactions and image retrieval.
 * @param bookDatabaseViewModel ViewModel for interacting with the book database.
 * @param bookViewModel ViewModel for handling book-related data.
 * @param navController Navigation controller for navigating between screens.
 * @param comments Callback function to be executed when the comment icon is clicked, passing the post ID.
 */
@Composable
fun YourFeed(postsGroupsViewmodel: PostsGroupsViewmodel,userInteractionViewmodel: UserInteractionViewmodel,bookDatabaseViewModel: BookDatabaseViewModel,bookViewModel: BookViewModel,navController: NavController,comments:(String)->Unit){
    val postlist by postsGroupsViewmodel.postsList.collectAsState()

    var feedRefresh by remember {
        mutableStateOf(0)
    }
    // Periodically refresh user info and posts
    LaunchedEffect(key1 = feedRefresh) {
        while (true) {
            delay(2000)
            postsGroupsViewmodel.getUsersInfo()
            postsGroupsViewmodel.getPosts()
        }
    }

    Text(text = feedRefresh.toString(),Modifier.size(0.1.dp))
    if (postlist.isEmpty()){
        Column(modifier = Modifier
            .fillMaxWidth(0.9f)
            .fillMaxHeight(0.915f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center){
            Text(text = "No posts to show",fontFamily = lindenHill, fontSize = 28.sp)
        }

    }
    else{
        LazyColumn(modifier= Modifier
            .fillMaxWidth(0.9f)
            .fillMaxHeight(0.915f)
            ,horizontalAlignment = Alignment.CenterHorizontally){
            items(postlist){post ->
                UserPost(post, userInteractionViewmodel, bookDatabaseViewModel ,postsGroupsViewmodel,bookViewModel, navController, comments = {comments(it)} )
                Spacer(modifier = Modifier.height(25.dp))
            }

        }
    }

}

/**
 * Function that prevents the app from crashing by insuring that a result is given even if it hasnt loaded from the api...
 * yeah, should have probably done this differently and put it in the viewmodel, but i have a headache and its late
 *
 * @param list the list with the ratings
 * @param index the current index
 * @return returns 3f if the index is higher then the list size
 */
fun ratingloader(list : List<Float>,index:Int): Float {
    if(index < list.size){
        return list[index]
    }else{
        return 3f
    }
}

/**
 * Function that displays the books in the home screen
 * @param modifier enables visual editing of the function
 * @param title the book title
 * @param author the books author
 * @param picId the id in order to load the book cover
 * @param rating the ratings of the book
 */
@Composable
fun HomescreenBooks(modifier: Modifier = Modifier, title: String = "", author: String = "", picId: Int,rating: Float){
    com.calculator.bookfinder.homepagebooks.TopLevel(modifier = modifier) {
        Books(
            modifier = Modifier.boxAlign(
                alignment = Alignment.TopStart,
                offset = DpOffset(
                    x = 10.0.dp,
                    y = 11.0.dp
                )
            )
        ) {
            AsyncImage(model ="https://covers.openlibrary.org/b/id/$picId-M.jpg" , contentDescription ="test" ,modifier=Modifier.fillMaxSize())
        }
        BookTitle(
            title = title,
            modifier = Modifier.boxAlign(
                alignment = Alignment.TopStart,
                offset = DpOffset(
                    x = 133.0.dp,
                    y = 21.0.dp
                )
            )
        )
        ByAuthor(
            author = author,
            modifier = Modifier.boxAlign(
                alignment = Alignment.TopStart,
                offset = DpOffset(
                    x = 137.0.dp,
                    y = 89.0.dp
                )
            )
        )

        Ratings(
            property1 = RatingsDecider(rating),
            modifier = Modifier.boxAlign(
                alignment = Alignment.TopStart,
                offset = DpOffset(
                    x = 133.0.dp,
                    y = 141.0.dp
                )
            )
        )
    }
}

/**
 * Function that chooses what variant to display based on the ratring of the book
 * @param rating the raing of the book
 * @return the chosen variant based on the rating
 */
fun RatingsDecider(rating :Float): Property1 {
    // if the rating is less the 2
    if (rating > 0 && rating < 2f){
        return Property1.Variant5
    }
    // if the rating is bewteen 2 and 3
    else if(rating > 2 && rating < 3f){
        return Property1.Variant4
    }
    // if its between 3 and 4
    else if(rating > 3 && rating < 4f){
        return Property1.Variant3
    }
    // if its between 4 and 4.5
    else if(rating > 4 && rating < 4.5f){
        return Property1.Variant2
    }
    else{
        return Property1.Default
    }
}

/**
 * Function that displays the loading icon
 * @param height the height
 * @param width the width
 */
@Composable
fun Loading(height:Int,width :Int){
    Row (modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center){
        CircularProgressIndicator(modifier = Modifier
            .height(height.dp)
            .width(width.dp))
    }

}




