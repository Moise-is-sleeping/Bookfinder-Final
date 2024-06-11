package ui.Screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.calculator.bookfinder.accountbuttons.AccountButtons
import com.calculator.bookfinder.accountbuttons.lindenHill

import com.calculator.bookfinder.header.Header
import com.calculator.bookfinder.morebuttons.MoreButtons
import com.calculator.bookfinder.naviagtionbar.NaviagtionBar
import com.calculator.bookfinder.postheader.PostHeader
import com.calculator.bookfinder.postheader.Property1
import com.calculator.bookfinder.userpfp.AddProfilePictureButtonProperty1Variant2
import com.calculator.bookfinder.userpfp.BlankPfpProperty1Variant2
import com.calculator.bookfinder.userpfp.TopLevelProperty1Variant2
import com.calculator.bookfinder.userpfp.VectorProperty1Variant2
import com.google.relay.compose.BoxScopeInstance.columnWeight
import com.google.relay.compose.BoxScopeInstance.rowWeight
import data.Routes.Routes
import ui.ViewModel.BookDatabaseViewModel
import ui.ViewModel.BookViewModel
import ui.ViewModel.LoginViewModel
import ui.ViewModel.UserInteractionViewmodel
/**
 * Displays information for  the screen with the books that have been saved to favourites
 *
 * @param bookDatabaseViewModel View model containing information about saved books
 * @param bookViewModel view-model that contains the logic behind certain functions in the screen
 * @param navController Controller for navigation between screens
 */
@Composable
fun SettingsScreen(userInteractionViewmodel: UserInteractionViewmodel, loginViewModel: LoginViewModel, navController: NavController){
    var forcedRefresh by remember { mutableIntStateOf(0) }
    val userName by remember { mutableStateOf(userInteractionViewmodel.myUserName()) }
    var oldFullName by remember { mutableStateOf("") }
    var oldDescription by remember { mutableStateOf("") }
    var dialogOpen by remember { mutableStateOf(false) }
    var displayMessage by remember { mutableStateOf(false) }


    Text(text = forcedRefresh.toString())
    Column (modifier= Modifier
        .fillMaxSize()
        .background(color = Color(0xFFFFFFFF)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top){
        Column(
            modifier = Modifier
                .fillMaxSize()){
            PostHeader(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.07f),
                property1 = Property1.Variant2,
                backButton = {
                    navController.popBackStack()
                },
                text = "Settings",
                logOutButton = {
                    dialogOpen = true
                })
            Row(
                modifier = Modifier
                    .padding(top = 40.dp)
                    .fillMaxWidth(),horizontalArrangement = Arrangement.Center) {
                UserProfilePicture(modifier = Modifier
                    .height(147.dp)
                    .width(147.dp), changePfpButton = {}, userInteractionViewmodel = userInteractionViewmodel,userName)
            }
            Row(modifier = Modifier
                .padding(top = 30.dp)
                .fillMaxWidth(),
                horizontalArrangement = Arrangement.Start) {
                Spacer(modifier = Modifier.fillMaxWidth(0.05f))
                Column(horizontalAlignment = Alignment.CenterHorizontally){
                    Text(text = "Posts",fontFamily = lindenHill, fontSize = 23.sp)
                    Text(text = "0",fontFamily = lindenHill, fontSize = 23.sp)
                }
                Spacer(modifier = Modifier.fillMaxWidth(0.3f))
                Column(horizontalAlignment = Alignment.CenterHorizontally){
                    Text(text = "Friends",fontFamily = lindenHill, fontSize = 23.sp)
                    Text(text = "0",fontFamily = lindenHill, fontSize = 23.sp)
                }
                Spacer(modifier = Modifier.fillMaxWidth(0.47f))
                Column(horizontalAlignment = Alignment.CenterHorizontally){
                    Text(text = "Groups",fontFamily = lindenHill, fontSize = 23.sp)
                    Text(text = "0",fontFamily = lindenHill, fontSize = 23.sp)
                }
            }
            Row(modifier = Modifier.fillMaxWidth(),){
                Spacer(modifier = Modifier.fillMaxWidth(0.05f))
                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 30.dp),
                    horizontalAlignment = Alignment.Start){

                    Text(text = "Name",fontFamily = lindenHill, fontSize = 23.sp)
                    TextField(
                        modifier = Modifier.fillMaxWidth(0.94f),
                        value = oldFullName ,
                        onValueChange ={
                            oldFullName = it
                        } )
                }
            }
            Row(modifier = Modifier.fillMaxWidth(),){
                Spacer(modifier = Modifier.fillMaxWidth(0.05f))
                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 30.dp),
                    horizontalAlignment = Alignment.Start){

                    Text(text = "About",fontFamily = lindenHill, fontSize = 23.sp)
                    TextField(
                        modifier = Modifier.fillMaxWidth(0.94f).fillMaxHeight(0.4f),
                        value = oldDescription ,
                        onValueChange ={
                            oldDescription = it
                        } )
                }
            }
            Row(modifier = Modifier
                .padding(top = 60.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.Center){
                AccountButtons(
                    buttonPressed = {
                        userInteractionViewmodel.updateUserInfo(oldFullName,oldDescription,
                            succes = {
                                if (it){
                                    displayMessage = it
                                }
                            })
                    },
                    buttonName = "Sign out",
                    property1 = com.calculator.bookfinder.accountbuttons.Property1.Variant4,
                    addRemoveRequest = "Update Profile",
                    modifier = Modifier
                        .rowWeight(1.0f)
                        .columnWeight(1.0f)
                        .height(55.dp)
                        .fillMaxWidth(0.9f)
                )
            }


        }
        if (dialogOpen){
            SignOutDialog(
                dialogClose = {
                    dialogOpen = false
                },navController,loginViewModel
            )
        }
    }
    if (displayMessage){
        Message(text = "Profile Updated")
    }

    forcedRefresh += 1
    forcedRefresh -= 1
}

@Composable
fun LoadPfp(userInteractionViewmodel: UserInteractionViewmodel, userName:String){
    var oldImageUri by remember { mutableStateOf<Uri?>(null) }
    userInteractionViewmodel.getImageFromFirebase(imageUri = { oldImageUri = it },userName)
    if (oldImageUri == null){
        Column (modifier= Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center){
            Loading(height = 50, width = 50)
        }

    }
    AsyncImage(model =oldImageUri , contentDescription ="test" ,modifier= Modifier.fillMaxSize(),contentScale = ContentScale.Crop)

}
@Composable
fun Message(text:String){
    Toast.makeText(LocalContext.current,text, Toast.LENGTH_SHORT).show()
}

@Composable
fun UserProfilePicture(modifier: Modifier, changePfpButton:()->Unit, userInteractionViewmodel: UserInteractionViewmodel, userName:String){
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {
            userInteractionViewmodel.uploadImageToFirebase(it)
        })
    TopLevelProperty1Variant2(modifier = modifier) {
        BlankPfpProperty1Variant2(
            modifier = Modifier
                .rowWeight(1.0f)
                .columnWeight(1.0f)
                .background(
                    Color(0x0AFFFFFF)
                )
        ) {
            LoadPfp(userInteractionViewmodel, userName)
        }
        AddProfilePictureButtonProperty1Variant2(
            changePfpButton = {

                changePfpButton()
                launcher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            modifier = Modifier
                .rowWeight(1.0f)
                .columnWeight(1.0f)
        ) {
            VectorProperty1Variant2(
                modifier = Modifier
                    .rowWeight(1.0f)
                    .columnWeight(1.0f)
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignOutDialog(dialogClose:()->Unit,navController: NavController,loginViewModel: LoginViewModel){

    AlertDialog(
        onDismissRequest = { dialogClose() },
        modifier = Modifier
            .background(Color(0xC2C1BF))

    ) {
        com.calculator.bookfinder.removefrienddialog.RemoveFriendDialog(
            textButton = "Are you sure you want to sign out?",
            removeButton = {
                dialogClose()
                navController.navigate(Routes.LoginScreen.route)

            },
            cancelButton = {
                dialogClose()
            },
            modifier = Modifier.height(184.dp).width(330.dp),
            text = "Sign Out"
        )
    }


}


@Composable
fun CompressImage(uri: Uri) {
    /*
        val context = LocalContext.current
        val inputfile = File(context.cacheDir,"uncompressedImage.jpg")
        val imageName = File(uri.path).name
        val outputfile = File(context.cacheDir,imageName)

        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(inputfile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        val jpegTurboCompressor = JpegTurboCompressor()
        val quality = 75
        jpegTurboCompressor.compress(inputFile.absolutePath, outputFile.absolutePath, quality)


    */

}