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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.calculator.bookfinder.R
import com.calculator.bookfinder.accountbuttons.lindenHill
import com.calculator.bookfinder.header.Header

import com.calculator.bookfinder.morebuttons.MoreButtons
import com.calculator.bookfinder.naviagtionbar.NaviagtionBar
import com.calculator.bookfinder.searchfield.TopLevel
import com.google.relay.compose.BorderAlignment
import com.google.relay.compose.BoxScopeInstance.columnWeight
import com.google.relay.compose.BoxScopeInstance.rowWeight
import com.google.relay.compose.RelayContainer
import com.google.relay.compose.RelayContainerScope
import com.google.relay.compose.RelayImage
import com.google.relay.compose.relayDropShadow
import com.google.relay.compose.tappable
import data.Models.Doc
import data.Routes.Routes
import ui.ViewModel.BookDatabaseViewModel
import ui.ViewModel.BookViewModel
import ui.ViewModel.PostsGroupsViewmodel
import ui.ViewModel.UserInteractionViewmodel

/**
 * Displays the information on the serach screen
 *
 * @param bookDatabaseViewModel view-model that contains the logic behind certain functions in the screen
 * @param bookViewModel view-model that contains the logic behind certain functions in the screen
 * @param navController Controller for navigation between screens
 */

@Composable
fun SearchScreen(postsGroupsViewmodel: PostsGroupsViewmodel,bookDatabaseViewModel: BookDatabaseViewModel,bookViewModel: BookViewModel,navController: NavController,userInteractionViewmodel:UserInteractionViewmodel)   {
    val searchValue by bookViewModel.searchValue.collectAsState()
    val hasSearched by bookViewModel.hasSearched.collectAsState()
    var moreButton by remember { mutableStateOf(false) }


    Column (modifier= Modifier
        .fillMaxSize()
        .background(color = Color(bookViewModel.backgroundColor())),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top){
        //if the user starts searching the header is removed
        if (!hasSearched){
            Header(modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
                userAccountButton = {
                    navController.navigate(Routes.SettingsScreen.route)
                })
        }
        Spacer(modifier = Modifier.fillMaxHeight(0.025f))
        SearchBar(modifier = Modifier
            .height(60.dp)
            .width(390.dp)
            .rowWeight(1.0f)
            .columnWeight(1.0f),
            searchButton = {
                bookViewModel.searchBooksByName(searchValue)
                bookViewModel.hasSearched()
            },
            bookViewModel)
        // once they search a different function is shown
        if (hasSearched){
            Searchresults(1, book = {},bookDatabaseViewModel,bookViewModel,navController)

        }
        // the navigation bar is also removed as well as the default categories
        else{
            SearchCategories(bookDatabaseViewModel,bookViewModel,navController)
            NaviagtionBar(
                homebutton = {navController.navigate(Routes.HomeScreen.route)},
                searchButton = {},
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
                        postsGroupsViewmodel.getUsersInfo()},
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


/**
 * Displays the search results.
 *
 * @param bookDatabaseViewModel view-model that contains the logic behind certain functions in the screen
 * @param bookViewModel view-model that contains the logic behind certain functions in the screen
 * @param navController Controller for navigation between screens
 */
@Composable
fun Searchresults(number:Int, book: (Doc)->Unit, bookDatabaseViewModel: BookDatabaseViewModel, bookViewModel: BookViewModel, navController: NavController) {
    val searchResults by bookViewModel.bookList.collectAsState()

    Column(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(0.915f)) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.65f)
                .padding(top = 5.dp)
            , horizontalAlignment = Alignment.Start
        ) {
            items(searchResults) {
                ListItem(
                    headlineContent = {
                        it!!.title?.let { it1 -> Text(text = it1) }
                    },
                    supportingContent = { Text(bookViewModel.nullDates(it?.first_publish_year ?:"")) },
                    modifier = Modifier.clickable {
                        if (number == 1){
                            bookViewModel.getBooks(it!!.key!!.substring(7))
                            navController.navigate(Routes.BookDescriptionScreen.route)
                            bookDatabaseViewModel.hasSavedDefaultValue(it.key!!.substring(7))
                        }
                        else{
                            if (it != null) {
                                book(it)
                            }
                        }

                    })
            }
        }
    }
}
/**
 * Displays the search categories.
 *
 * @param bookDatabaseViewModel view-model that contains the logic behind certain functions in the screen
 * @param bookViewModel view-model that contains the logic behind certain functions in the screen
 * @param navController Controller for navigation between screens
 */

@Composable
fun SearchCategories(bookDatabaseViewModel: BookDatabaseViewModel, bookViewModel: BookViewModel,navController: NavController){
    val horror by bookViewModel.horrorBookList.collectAsState()
    val romance by bookViewModel.romanceBookList.collectAsState()
    val scienceFiction by bookViewModel.sciFiBookList.collectAsState()
    val novel by bookViewModel.novelBookList.collectAsState()
    val mystery by bookViewModel.mysteryBookList.collectAsState()
    val categoryList by remember { mutableStateOf(mutableListOf("Horror","Romance","Science Fiction","Novel","Mystery")) }
    LazyColumn(modifier= Modifier
        .fillMaxWidth()
        .fillMaxHeight(0.915f),horizontalAlignment = Alignment.CenterHorizontally){
        item{
            Column {
                Text(text = categoryList[0], fontFamily = lindenHill, fontSize = 23.sp,
                    color = Color(
                    alpha = 255,
                    red = 0,
                    green = 0,
                    blue = 0
                ), modifier = Modifier.padding(5.dp))
                LazyRow(){
                    items(horror){
                        Book(
                            imageButton = {
                                bookViewModel.getBooks(it.key.substring(7))
                                navController.navigate(Routes.BookDescriptionScreen.route)
                                bookDatabaseViewModel.hasSavedDefaultValue(it.key.substring(7))
                            },
                            it.cover_id.toString())
                    }

                }
            }
        }
        item{
            Column {
                Text(text = categoryList[1], fontFamily = lindenHill, fontSize = 23.sp,
                    color = Color(
                        alpha = 255,
                        red = 0,
                        green = 0,
                        blue = 0
                    ), modifier = Modifier.padding(5.dp))
                LazyRow(){
                    items(romance){
                        Book(
                            imageButton = {
                                bookViewModel.getBooks(it.key.substring(7))
                                navController.navigate(Routes.BookDescriptionScreen.route)
                                bookDatabaseViewModel.hasSavedDefaultValue(it.key.substring(7))
                            },it.cover_id.toString())
                    }

                }
            }
        }
        item{
            Column {
                Text(text = categoryList[2], fontFamily = lindenHill, fontSize = 23.sp,
                    color = Color(
                        alpha = 255,
                        red = 0,
                        green = 0,
                        blue = 0
                    ), modifier = Modifier.padding(5.dp))
                LazyRow(){
                    items(scienceFiction){
                        Book(
                            imageButton = {
                                bookViewModel.getBooks(it.key.substring(7))
                                navController.navigate(Routes.BookDescriptionScreen.route)
                                bookDatabaseViewModel.hasSavedDefaultValue(it.key.substring(7))
                            },it.cover_id.toString())
                    }

                }
            }
        }
        item{
            Column {
                Text(text = categoryList[3], fontFamily = lindenHill, fontSize = 23.sp,
                    color = Color(
                        alpha = 255,
                        red = 0,
                        green = 0,
                        blue = 0
                    ), modifier = Modifier.padding(5.dp))
                LazyRow(){
                    items(novel){
                        Book(
                            imageButton = {
                                bookViewModel.getBooks(it.key.substring(7))
                                navController.navigate(Routes.BookDescriptionScreen.route)
                                bookDatabaseViewModel.hasSavedDefaultValue(it.key.substring(7))
                        },it.cover_id.toString())
                    }

                }
            }
        }
        item{
            Column {
                Text(text = categoryList[4], fontFamily = lindenHill, fontSize = 23.sp,
                    color = Color(
                        alpha = 255,
                        red = 0,
                        green = 0,
                        blue = 0
                    ), modifier = Modifier.padding(5.dp))
                LazyRow(){
                    items(mystery){
                        Book(
                            imageButton = {
                                bookViewModel.getBooks(it.key.substring(7))
                                navController.navigate(Routes.BookDescriptionScreen.route)
                                bookDatabaseViewModel.hasSavedDefaultValue(it.key.substring(7))
                            }
                        ,it.cover_id.toString())
                    }

                }
            }
        }
    }
}







/**
 * Displays the search bar.
 *
 * @param modifier Modifier for styling and positioning
 * @param searchButton lambda function that allows the logic behind what happens when the button is pressed to be specified in a different function
 * @param bookViewModel View model containing book-related logic
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(modifier:Modifier,searchButton: () -> Unit = {},bookViewModel: BookViewModel){
    val searchValue by bookViewModel.searchValue.collectAsState()
    val hasSearched by bookViewModel.hasSearched.collectAsState()
    TopLevel(modifier = modifier) {
        Row(
            modifier = Modifier
                .height(55.dp)
                .width(320.dp)
        ) {
            //if the user starts typing, the back arrow appears
            if (hasSearched){
                Column(modifier = Modifier
                    .fillMaxHeight()
                    .width(50.dp)
                    .clickable {
                        bookViewModel.hasNotSearched()
                    }){
                    Icon(
                        Icons.Outlined.ArrowBack,
                        contentDescription = "Localized description",
                        modifier = Modifier
                            .offset(x = 15.dp, y = 15.dp) // Adjust the offset values as needed
                    )
                }
            }
            TextField(
            modifier= Modifier
                .fillMaxWidth()
                .background(color = Color.Transparent)
                .fillMaxHeight(),
            value = searchValue,
            onValueChange = {
                bookViewModel.updateSearchValue(it)

                bookViewModel.hasSearched()
            },
            placeholder = {
                Text(text = "Search titles...",fontFamily = lindenHill, fontSize = 20.sp, modifier = Modifier)
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                cursorColor = Color.Black,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            singleLine = true,
        )}
        SearchButtonEdit(
            searchButton = searchButton,
            modifier = Modifier
                .boxAlign(
                    alignment = Alignment.TopStart,
                    offset = DpOffset(
                        x = 330.dp,
                        y = 15.dp
                    )
                )
                .height(35.dp)
                .width(38.dp)
        )
    }
}

/**
 * Displays the book cover photo
 *
 * @param imageButton Callback function for image button action
 * @param picId The ID of the book image
 */
@Composable
fun Book(
        imageButton :()->Unit,
          picId:String){
    BooksEdit(
        modifier = Modifier
            .height(230.dp)
            .width(158.dp)
            .padding(10.dp)
        //if it conatains the +simbol then a plachoder us put instead of the cover image
    ) {
        if (picId.indexOf("+") == -1 ){
            AsyncImage(model ="https://covers.openlibrary.org/b/id/$picId-M.jpg" , contentDescription ="test" ,modifier= Modifier
                .fillMaxSize()
                .clickable { imageButton() })
        }else{
            Text(text = picId.substring(2), fontSize = 25.sp, textAlign = TextAlign.Center)
        }

    }
}

/**
 * An edited version of the relay book function
 *
 * @param modifier Modifier for styling and positioning
 * @param content Composable content of the container
 */
@Composable
fun BooksEdit(
    modifier: Modifier = Modifier,
    content: @Composable RelayContainerScope.() -> Unit
) {
    RelayContainer(
        backgroundColor = Color(red = 255, green = 255, blue = 255, alpha = 255),
        isStructured = false,
        radius = 5.0,
        borderAlignment = BorderAlignment.Outside,
        content = content,
        modifier = modifier.relayDropShadow(
            color = Color(
                alpha = 63,
                red = 0,
                green = 0,
                blue = 0
            ),
            borderRadius = 0.dp,
            blur = 10.0.dp,
            offsetX = 0.0.dp,
            offsetY = 0.0.dp,
            spread = 4.0.dp
        )
    )
}


/**
 * An edited version of the relay serach button function
 *
 * @param modifier Modifier for styling and positioning
 * @param searchButton Callback function for search button action
 */
@Composable
fun SearchButtonEdit(modifier: Modifier,searchButton: () -> Unit){
    RelayImage(
        image = painterResource(R.drawable.search_field_search_button),
        contentScale = ContentScale.Fit,
        modifier = modifier.tappable(onTap = searchButton)
    )
}


