package ui.Screens

import android.net.Uri
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardControlKey
import androidx.compose.material.icons.filled.ModeComment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.ModeComment
import androidx.compose.material.icons.outlined.Star
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults.textFieldColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.calculator.bookfinder.R
import com.calculator.bookfinder.accountbuttons.AccountButtons
import com.calculator.bookfinder.accountbuttons.Property1
import com.calculator.bookfinder.accountbuttons.lindenHill
import com.calculator.bookfinder.addpicture.AddPicture
import com.calculator.bookfinder.header.lancelot
import com.calculator.bookfinder.postheader.PostHeader
import com.calculator.bookfinder.reviewpost.Class10Jan2024
import com.calculator.bookfinder.reviewpost.Deadpool1
import com.calculator.bookfinder.reviewpost.Frame19
import com.calculator.bookfinder.reviewpost.HomepageBooks
import com.calculator.bookfinder.reviewpost.Line3
import com.calculator.bookfinder.reviewpost.Moisebrenes
import com.calculator.bookfinder.reviewpost.More
import com.calculator.bookfinder.reviewpost.UserInstance
import com.calculator.bookfinder.reviewpost.Vector1
import com.calculator.bookfinder.reviewpostvariant2.Date
import com.calculator.bookfinder.reviewpostvariant2.Description
import com.calculator.bookfinder.reviewpostvariant2.Title
import com.calculator.bookfinder.reviewpostvariant2.Username
import com.calculator.bookfinder.tagbook.TagBook
import com.google.relay.compose.BoxScopeInstance.boxAlign
import com.google.relay.compose.BoxScopeInstance.columnWeight
import com.google.relay.compose.BoxScopeInstance.rowWeight
import com.google.relay.compose.RelayContainer
import com.google.relay.compose.RelayContainerScope
import data.Routes.Routes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType
import ui.ViewModel.BookDatabaseViewModel
import ui.ViewModel.BookViewModel
import ui.ViewModel.PostsGroupsViewmodel
import ui.ViewModel.UserInteractionViewmodel
import ui.state.BookState
import ui.state.PostSate

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun PostScreen(bookDatabaseViewModel: BookDatabaseViewModel, navController: NavController, postsGroupsViewmodel: PostsGroupsViewmodel, bookViewModel: BookViewModel){
    var postTitle by remember {
        mutableStateOf("")
    }
    var postDescription by remember {
        mutableStateOf("")
    }
    val starColors by postsGroupsViewmodel.starColor.collectAsState()
    var tagBook by remember { mutableStateOf(false)}
    val searchValue by bookViewModel.searchValue.collectAsState()
    val hasSearched by bookViewModel.hasSearched.collectAsState()
    val book by postsGroupsViewmodel.book.collectAsState()
    var message by remember {
        mutableStateOf(false)
    }




    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally){
        if (!tagBook){
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
            Column(modifier = Modifier.padding(start = 15.dp)) {
                Text(text = "Rating", fontFamily = lancelot, fontSize = 23.sp,
                    color = Color(
                        alpha = 255,
                        red = 0,
                        green = 0,
                        blue = 0
                    )
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
                .padding()
                ,horizontalArrangement = Arrangement.Center){
                if(book.title == ""){
                    TagBook(
                        tagButton = {
                            tagBook = true
                        },
                        modifier = Modifier
                            .rowWeight(1.0f)
                            .columnWeight(1.0f)
                            .height(42.dp)
                            .width(167.dp)
                    )
                }else{
                    InputChip(
                        selected = tagBook,
                        onClick = {
                            tagBook = !tagBook
                                  },
                        label = {
                            Text(postsGroupsViewmodel.bookNameLength(book.title), fontFamily = lancelot, fontSize = 23.sp)
                                },
                        trailingIcon = {
                            Icon(
                                Icons.Filled.Cancel,
                                contentDescription = "Localized description",
                                Modifier.size(InputChipDefaults.IconSize)
                            )
                        }
                    )
                }
                Spacer(modifier = Modifier.fillMaxWidth(0.6f))
                AddPicture(
                    addPicture = {},
                    modifier = Modifier
                        .rowWeight(1.0f)
                        .columnWeight(1.0f)
                        .height(42.dp)
                        .width(48.dp)
                )

            }
            Spacer(modifier = Modifier.fillMaxHeight(0.15f))
            AccountButtons(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(42.dp),
                property1 = Property1.Variant5,
                buttonName = "Create Post",
                buttonPressed = {
                    postsGroupsViewmodel.uploadPost(postTitle,postDescription,book, "", succes = {
                        message = true
                        navController.popBackStack()
                    })
                }
            )
        }
        else{
            PostHeader(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.07f),
                backButton = {
                   tagBook = false
                },
                property1 = com.calculator.bookfinder.postheader.Property1.Default,
                text = "Tag Book")
            Spacer(modifier = Modifier.padding(top = 30.dp))
            SearchBar(modifier = Modifier
                .height(60.dp)
                .width(390.dp)
                .rowWeight(1.0f)
                .columnWeight(1.0f)
                ,
                searchButton = {
                    bookViewModel.searchBooksByName(searchValue)
                    bookViewModel.hasSearched()
            },
                bookViewModel )
            if (hasSearched){
                Searchresults(2, book = {
                    postsGroupsViewmodel.setBook(it)
                    tagBook = false
                },bookDatabaseViewModel,bookViewModel,navController)

            }
        }


    }
    if (message){
        Message("Post Created")
        message = false
    }

}


@Composable
fun UserPost(post: PostSate,userInteractionViewmodel: UserInteractionViewmodel,bookDatabaseViewModel: BookDatabaseViewModel,postsGroupsViewmodel: PostsGroupsViewmodel,bookViewModel: BookViewModel,navController: NavController,comments:(String)->Unit){
    var icontype by remember {
        mutableStateOf(false)
    }
    var book by remember {
        mutableStateOf(BookState())
    }
    var author by remember {
        mutableStateOf("")
    }
    var animation by remember {
        mutableStateOf(false)
    }

    postsGroupsViewmodel.getAuthor(postsGroupsViewmodel.extractId(post.book.key!!),author = {author = it

    })
    postsGroupsViewmodel.getBookInfo(postsGroupsViewmodel.extractId(post.book.key!!)){
        book = it
    }

    Box(Modifier.fillMaxSize(),contentAlignment = Alignment.Center) {
        Column(modifier = Modifier
            .background(Color.White)
            .padding(start = 10.dp, end = 10.dp)
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        postsGroupsViewmodel.likeOrUnlikePost(post.id, animate = {
                            if (it) {
                                animation = true
                            }
                        })

                    }
                )
            }){
            UserPostHeader(post.userName,post.date, userInteractionViewmodel,navController )
            Row (modifier = Modifier
                .background(Color.Black)
                .height(1.dp)
                .fillMaxWidth()){
            }
            Text(text = post.title, fontFamily = lindenHill, fontSize = 25.sp,
                color = Color(
                    alpha = 255,
                    red = 0,
                    green = 0,
                    blue = 0
                ),modifier = Modifier.padding(top = 15.dp,bottom = 15.dp, start = 5.dp))
            Text(text = post.description, fontFamily = lindenHill, fontSize = 18.sp,
                color = Color(
                    alpha = 255,
                    red = 0,
                    green = 0,
                    blue = 0
                ),modifier = Modifier.padding(top = 5.dp,bottom = 15.dp, start = 5.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Rating", fontFamily = lindenHill, fontSize = 20.sp,
                    color = Color(
                        alpha = 255,
                        red = 0,
                        green = 0,
                        blue = 0
                    ),modifier = Modifier.padding(start = 5.dp,top = 7.dp, end = 10.dp))
                Row(modifier = Modifier.fillMaxWidth(0.8f)) {
                    for (i in 1..post.ratings){
                        Icon(
                            modifier = Modifier
                                .size(40.dp),
                            imageVector = Icons.Outlined.Star,
                            contentDescription = "Localized description",
                            tint = Color(0xFFF7F772)
                        )
                    }
                }
                DropDownInfo( buttonPressed = {
                    icontype = !icontype
                })
            }
            if (icontype){
                Row (modifier = Modifier
                    .padding(bottom = 27.dp)
                    .clickable {
                        bookViewModel.getBooks(postsGroupsViewmodel.extractId(post.book.key!!))
                        navController.navigate(Routes.BookDescriptionScreen.route)
                        bookDatabaseViewModel.hasSavedDefaultValue(postsGroupsViewmodel.extractId(post.book.key!!))
                    }){
                    BooksEdit(
                        modifier = Modifier
                            .height(95.dp)
                            .width(70.dp)
                            .padding(start = 7.dp)
                            .offset(y = 7.dp)
                    ) {
                        //if the function returns empty, the place holder for the cover photo is left
                        if (bookDatabaseViewModel.gotCovers(book) != "empty"){
                            AsyncImage(
                                model = "https://covers.openlibrary.org/b/id/${book.covers?.get(0)}-M.jpg",
                                contentDescription = "test",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                    Column {
                        Text(text = post.book.title.toString()+" ("+post.book.first_publish_year.toString()+")", modifier = Modifier.padding(start =15.dp),fontSize = 20.0.sp,
                            fontFamily = com.calculator.bookfinder.frame8.lindenHill,)
                        Text(text =author, modifier = Modifier
                            .padding(start = 15.dp), fontSize = 15.0.sp,
                            fontFamily = com.calculator.bookfinder.frame8.lindenHill,)
                    }
                }
            }
            Row (modifier = Modifier
                .background(Color.Black)
                .height(1.dp)
                .fillMaxWidth()){
            }
            Row {
                Column(modifier = Modifier
                    .padding(end = 5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top) {
                    LikeIcon(postsGroupsViewmodel,post.id)
                    Text(text = post.likes.size.toString(),fontSize = 12.sp,modifier = Modifier.boxAlign(alignment = Alignment.TopCenter, offset = DpOffset(x = 0.dp, y = (-9).dp)))
                }
                Column (modifier = Modifier
                    .padding(start = 5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top){
                    IconButton(onClick = {
                        comments(post.id)
                    }) {
                        Image(modifier = Modifier.size(34.dp), painter = painterResource(id = R.drawable.postcomments), contentDescription ="" )
                    }
                    Text(text = post.comments.size.toString(), fontSize = 12.sp, modifier = Modifier.boxAlign(alignment = Alignment.TopCenter, offset = DpOffset(x = 0.dp, y = (-9).dp)))
                }
            }

        }
        if (animation){
            AnimatedVisibility(
                visible = animation,
                enter = fadeIn(animationSpec = tween(150))+ expandIn(expandFrom = Alignment.Center, animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(150))
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        Icons.Filled.Favorite,
                        contentDescription = "Localized description",
                        Modifier.size(150.dp),
                        tint = Color(Color.Red.value)
                    )
                    LaunchedEffect(key1 = animation) {
                        while (animation) {
                            delay(300)
                            animation = false

                        }
                    }
                }
            }

        }
    }





}
@Composable
fun LikeIcon(postsGroupsViewmodel: PostsGroupsViewmodel,id:String){
    var icon by remember {
        mutableStateOf( Icons.Outlined.FavoriteBorder)
    }
    var color by remember {
        mutableStateOf(Color(Color.Black.value))
    }


    postsGroupsViewmodel.checkPostLike(id,liked = {
        if (it){
            icon = Icons.Filled.Favorite
            color = Color(Color.Red.value)
        }else{
            icon = Icons.Outlined.FavoriteBorder
            color = Color(Color.Black.value)
        }
        Log.d("liked", it.toString())
    })


    IconButton(onClick = {

        postsGroupsViewmodel.likeOrUnlikePost(id, animate = {
            if (!it) {
                icon =  Icons.Outlined.FavoriteBorder
                color = Color(Color.Black.value)
            }else{
                icon = Icons.Filled.Favorite
                color = Color(Color.Red.value)
            }
        })
    }) {
        Icon(
            icon,
            contentDescription = "Localized description",
            Modifier.size(42.dp),
            tint = color
        )
    }
}



@Composable
fun UserPostHeader(name:String, date: String,userInteractionViewmodel: UserInteractionViewmodel,navController: NavController){
    Row(modifier = Modifier
        .fillMaxWidth()
        ) {
        HeaderContainer(
            modifier = Modifier
                .rowWeight(1.0f)
                .columnWeight(1.0f)
                .height(65.dp)
        ) {
            Username(
                username = name,
                modifier = Modifier.boxAlign(
                    alignment = Alignment.TopStart,
                    offset = DpOffset(
                        x = 56.0.dp,
                        y = 13.0.dp
                    )
                )
            )
            com.calculator.bookfinder.reviewpostvariant2.More(
                moreButton2 = { },
                modifier = Modifier.boxAlign(
                    alignment = Alignment.TopEnd,
                    offset = DpOffset(
                        x = -6.0.dp,
                        y = 21.0.dp
                    )
                )
            ) {
                com.calculator.bookfinder.reviewpostvariant2.Vector1(
                    modifier = Modifier
                        .rowWeight(
                            1.0f
                        )
                        .columnWeight(1.0f)
                )
            }
            Date(
                date = date,
                modifier = Modifier.boxAlign(
                    alignment = Alignment.TopStart,
                    offset = DpOffset(
                        x = 57.0.dp,
                        y = 32.0.dp
                    )
                )
            )
            Log.d("dates",date)
            com.calculator.bookfinder.reviewpostvariant2.Line3(
                modifier = Modifier.boxAlign(
                    alignment = Alignment.BottomEnd,
                    offset = DpOffset(
                        x = -6.5.dp,
                        y = -2.5.dp
                    )
                )
            )
            com.calculator.bookfinder.reviewpostvariant2.HomepageBooks(
                modifier = Modifier.boxAlign(
                    alignment = Alignment.TopStart,
                    offset = DpOffset(
                        x = 7.0.dp,
                        y = 11.0.dp
                    )
                )
            ) {
                LoadPfp(userInteractionViewmodel,name )
            }
        }
    }
}

@Composable
fun HeaderContainer(
    modifier: Modifier = Modifier,
    content: @Composable RelayContainerScope.() -> Unit
) {
    RelayContainer(
        isStructured = false,
        content = content,
        modifier = modifier
            .padding(
                paddingValues = PaddingValues(
                    start = 0.0.dp,
                    top = 0.0.dp,
                    end = 0.0.dp,
                    bottom = 0.0.dp
                )
            )
            .fillMaxWidth(1.0f)
            .fillMaxHeight(1.0f)
    )
}


@Composable
fun DropDownInfo(buttonPressed:()->Unit){
    var icon by remember {
        mutableStateOf(Icons.Filled.KeyboardArrowDown)
    }
    IconButton(onClick = {
        if (icon == Icons.Filled.KeyboardArrowDown){
            icon = Icons.Filled.KeyboardControlKey
        }else{
            icon = Icons.Filled.KeyboardArrowDown
        }
        buttonPressed()
    }) {
        Icon(
            icon,
            contentDescription = "Localized description",
            Modifier.size(38.dp)
        )
    }
}

@Preview
@Composable
fun HeartAnimation(){
    var dialog by remember {
        mutableStateOf(false)

    }

    Column(modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectTapGestures(
                onDoubleTap = {
                    dialog = true
                }
            )
        }


    ){
        Text(text = "hello")
    }

    if (dialog){
        AnimatedVisibility(
            visible = dialog,
            enter = fadeIn(animationSpec = tween(150))+ expandIn(expandFrom = Alignment.Center, animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(150))
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Icon(
                    Icons.Filled.Favorite,
                    contentDescription = "Localized description",
                    Modifier.size(150.dp),
                    tint = Color(Color.Red.value)
                )
                LaunchedEffect(key1 = dialog) {
                    while (dialog) {
                        delay(300)
                        dialog = false

                    }
                }
            }
        }

    }
}



/*@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GifInput() {
    var gifUri by remember { mutableStateOf<Uri?>(null) }

    BasicTextField(
        value = "", // You might not need to store text if only handling GIFs
        onValueChange = {},
        modifier = Modifier
            .fillMaxWidth()
            .receiveContent(setOf(MediaType.Image)) { content ->
                gifUri = content.platformTransferableContent?.linkUri
            }
    )

    gifUri?.let {
        AsyncImage(
            model = it,
            contentDescription = "User GIF",
            modifier = Modifier.fillMaxWidth()
        )
    }
}*/
