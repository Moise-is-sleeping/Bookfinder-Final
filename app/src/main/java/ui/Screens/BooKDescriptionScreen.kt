package ui.Screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.calculator.bookfinder.addtofavourites.AddToFavourites
import com.calculator.bookfinder.addtofavourites.Property1
import com.calculator.bookfinder.frame8.BookTitle
import com.calculator.bookfinder.frame8.ByAuthor
import com.calculator.bookfinder.frame8.Class1999
import com.calculator.bookfinder.frame8.Frame9
import com.calculator.bookfinder.frame8.Rating
import com.calculator.bookfinder.frame8.TopLevel
import com.calculator.bookfinder.frame8.lindenHill
import com.calculator.bookfinder.naviagtionbar.NaviagtionBar
import com.calculator.bookfinder.ratings.Ratings
import com.calculator.bookfinder.text.baskervville
import com.google.relay.compose.BoxScopeInstance.boxAlign
import com.google.relay.compose.BoxScopeInstance.columnWeight
import com.google.relay.compose.BoxScopeInstance.rowWeight
import data.Routes.Routes
import ui.ViewModel.BookDatabaseViewModel
import ui.ViewModel.BookViewModel


/**
 * Function that displays the information about a book in a more detailed way
 * @param bookDatabaseViewModel view-model that contains the logic behind certain functions in the screen
 * @param bookViewModel view-model that contains the logic behind certain functions in the screen
 * @param navController controller that allows navigation between screens
 */
@Composable
fun BooKDescriptionScreen(bookDatabaseViewModel: BookDatabaseViewModel, bookViewModel: BookViewModel, navController: NavController) {
    val bookDetails by bookViewModel.bookDetails.collectAsState()
    val hasSaved by bookDatabaseViewModel.hasSaved.collectAsState()
    //if the books title is not the default value it displays the information
    if (bookDetails.title!! != "none"){
        Column (
            modifier = Modifier
                .background(color = Color(0xFFFFFFFF))
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
                            bookViewModel.resetHasCovers()
                            bookDatabaseViewModel.addIdOrRemove(hasSaved)
                        }
                        .height(39.dp)
                        .width(39.dp)
                )
            }
            BookDetails( modifier = Modifier.height(400.dp), bookViewModel)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 15.dp),
                horizontalArrangement = Arrangement.Center) {
                AddToFavourites(
                    addToFavourites = {
                        bookDatabaseViewModel.hasSaved()
                    },
                    //the button variant is shown based on whether the user has saved the book or not
                    property1 = if (hasSaved) Property1.Variant2 else Property1.Default ,
                    modifier = Modifier
                        .rowWeight(1.0f)
                        .height(60.dp)
                        .width(385.dp)
                )
            }
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp)){
                BookDescription(bookViewModel)
            }
        }
    }
    else{
        Column (modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center){
            Loading(120,90)
        }

    }
}


/**
 * function that displays details about the books
 * @param modifier allows the visual modification of the function
 * @param bookViewModel view-model that contains the logic behind certain functions in the screen
 */
@Composable
fun BookDetails(
    modifier: Modifier = Modifier,
    bookViewModel: BookViewModel
) {
    val bookDetails by bookViewModel.bookDetails.collectAsState()
    val hasCovers by bookViewModel.hasCovers.collectAsState()



    Row(modifier = Modifier.height(230.dp)) {
        //depending on whether a cover id is received from the api, its show or a place holder is put
        if (hasCovers){
            Book(
                imageButton = {},
                bookDetails.covers!![0].toString()
            )
        }else{
            Book(
                imageButton = {},
               "++"+bookDetails.title.toString()
            )

        }
        Column {
            Column {
                Text(
                    text = bookDetails.title.toString() ,
                    fontSize = 30.0.sp,
                    fontFamily = lindenHill,
                    modifier = Modifier.padding(start = 10.dp, top = 5.dp)
                )
                AuthorName(bookViewModel)
                
            }
        }
    }
}

/**
 * Function that displays the name of the author
 * @param bookViewModel view-model that contains the logic behind certain functions in the screen
 */
@Composable
fun AuthorName(bookViewModel: BookViewModel){
    val author by bookViewModel.author.collectAsState()
    return Text(
            text = "by "+author.name,
            fontSize = 20.0.sp,
            fontFamily = lindenHill,
            modifier = Modifier.padding(start = 10.dp, top = 10.dp),
            color = Color(
                alpha = 183,
                red = 0,
                green = 0,
                blue = 0
            )
        )
}

/**
 * Function that displays the book description
 * @param bookViewModel view-model that contains the logic behind certain functions in the screen
 */
@Composable
fun BookDescription( bookViewModel: BookViewModel){
    val bookDetails by bookViewModel.bookDetails.collectAsState()
    Column {
        Text(
            text = "Description",
            fontSize = 30.0.sp,
            fontFamily = lindenHill,
            modifier = Modifier.padding(top = 15.dp, bottom = 15.dp))
        LazyColumn(){
            item {
                Text(text = bookViewModel.extractDescription(bookDetails.description.toString()),
                    fontSize = 20.0.sp,
                    fontFamily = baskervville,)
            }

        }
    }
}

