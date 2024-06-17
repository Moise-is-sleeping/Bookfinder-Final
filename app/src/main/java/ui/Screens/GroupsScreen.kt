package ui.Screens

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults.textFieldColors
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.calculator.bookfinder.accountbuttons.AccountButtons
import com.calculator.bookfinder.accountbuttons.Property1
import com.calculator.bookfinder.header.Header
import com.calculator.bookfinder.morebuttons.MoreButtons
import com.calculator.bookfinder.naviagtionbar.NaviagtionBar
import com.calculator.bookfinder.postheader.PostHeader
import com.calculator.bookfinder.reviewpost.HomepageBooks
import com.calculator.bookfinder.tagbook.TopLevel
import com.calculator.bookfinder.tagbook.lindenHill
import com.calculator.bookfinder.userpfp.AddProfilePictureButtonProperty1Variant2
import com.calculator.bookfinder.userpfp.BlankPfpProperty1Variant2
import com.calculator.bookfinder.userpfp.TopLevelProperty1Variant2
import com.calculator.bookfinder.userpfp.VectorProperty1Variant2
import com.google.relay.compose.BoxScopeInstance.columnWeight
import com.google.relay.compose.BoxScopeInstance.rowWeight
import data.Routes.Routes
import ui.ViewModel.BookDatabaseViewModel
import ui.ViewModel.PostsGroupsViewmodel
import ui.ViewModel.UserInteractionViewmodel



/**
 * Composable function that displays the "Groups" screen, showing the user's groups and allowing them to create new groups.*
 * @param navController Navigation controller for navigating between screens.
 * @param userInteractionViewmodel ViewModel for handling user interactions and data.
 * @param postsGroupsViewmodel ViewModel for handling post and group related actions and data.
 * @param bookDatabaseViewModel ViewModel for interacting with the book database.*/
@Composable
fun GroupsScreen(navController: NavController,userInteractionViewmodel: UserInteractionViewmodel,postsGroupsViewmodel: PostsGroupsViewmodel,bookDatabaseViewModel: BookDatabaseViewModel){
    var moreButton by remember { mutableStateOf(false) } // State for toggling the "More" button overlay
    var createGroups by remember { mutableStateOf(false) } // State for toggling the "Create Group" screen

// Display "Your Groups" screen
    if(!createGroups){
        Column(modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFE5DBD0))) {

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
                    buttonName = "Your Groups",
                    modifier = Modifier
                        .rowWeight(1.0f)
                        .columnWeight(1.0f)
                        .fillMaxHeight(0.6f)
                        .weight(1f)
                    , color = 0xFFFFFFFF
                )
                Spacer(modifier = Modifier.fillMaxWidth(0.05f))
                Spacer(
                    modifier = Modifier
                        .rowWeight(1.0f)
                        .columnWeight(1.0f)
                        .fillMaxHeight(0.6f)
                        .weight(1f)

                )
                Spacer(modifier = Modifier.fillMaxWidth(0.05f))
            }

            // Fetch and display user's groups
            postsGroupsViewmodel.getGroups()
            MyGroups(postsGroupsViewmodel,userInteractionViewmodel,navController,createGroups = {
                createGroups = true
            })



            NaviagtionBar(
                homebutton = {navController.navigate(Routes.HomeScreen.route)},
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
                        groupsButton = {},
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
    }else{
        CreateGroups(navController,postsGroupsViewmodel,userInteractionViewmodel, back = {
            createGroups = false
        })
    }


}

/**
 * Composable function that displays a list of the user's groups, allowing navigation to group chats.
 *
 * @parampostsGroupsViewmodel ViewModel for handling post and group related actions and data.
 * @param userInteractionViewmodel ViewModel for handling user interactions and image retrieval.
 * @param navController Navigation controller for navigating between screens.
 * @param createGroups Callback function to be executed when the "Create Group" button isclicked.
 */
@Composable
fun MyGroups(postsGroupsViewmodel: PostsGroupsViewmodel,userInteractionViewmodel: UserInteractionViewmodel,navController: NavController,createGroups:()->Unit) {
    val list by postsGroupsViewmodel.groupsList.collectAsState()


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.915f),
        contentAlignment = Alignment.BottomEnd
    ) {
        // Display message if no groups are available
        if (list.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.915f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No groups to show",
                    fontFamily = com.calculator.bookfinder.accountbuttons.lindenHill,
                    fontSize = 28.sp
                )
            }
        } else {
            // Display group list
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.915f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(list) { group ->
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp)
                            .padding(top = 7.dp, start = 15.dp, end = 15.dp)
                            .background(Color.White, RoundedCornerShape(3.dp))
                            .clickable {
                                postsGroupsViewmodel.changeCurrentGroup(group)
                                navController.navigate(Routes.MessagesScreen.route)
                            }) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Spacer(modifier = Modifier.width(10.dp))
                                HomepageBooks() {
                                    LoadGroupPfp(userInteractionViewmodel, group.pfpName)
                                }
                                Text(
                                    text = group.groupName,
                                    modifier = Modifier.padding(start = 15.dp)
                                )
                            }
                        }
                    }
                }

            }
        }
        // Floating action button to create a new group
        FloatingActionButton(
            modifier = Modifier.padding(15.dp),
            onClick = {
                createGroups()
            },
            containerColor = Color(
                alpha = 255,
                red = 251,
                green = 242,
                blue = 192
            )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "",
            )
        }
    }
}




/**
 * Composable function that displays the screen for creating new groups, including adding members and setting group details.
 *
 * @param navController Navigation controller for navigating between screens.
 * @param postsGroupsViewmodel ViewModel for handling post and group related actions.
 * @param userInteractionViewmodel ViewModel for handling user interactions and data.
 * @param back Callback function to be executed when the back button is clicked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGroups(navController: NavController,postsGroupsViewmodel: PostsGroupsViewmodel,userInteractionViewmodel: UserInteractionViewmodel,back:()->Unit){
    var groupName by remember { mutableStateOf("") } // State for storing the group name
    var groupDescription by remember { mutableStateOf("") } // State for storing the group description
    var message by remember { mutableStateOf(false) } // State for displaying group creation success message
    var groupMembers by remember { mutableStateOf(listOf<String>()) } // State forstoring the list of group members
    var forcedRefresh by remember { mutableStateOf(0) }
    var addFriend by remember { mutableStateOf(false) } // State for toggling the "Add Friend" section
    var pfpName by remember { mutableStateOf("groups.png") } // State for storing the group profile picture name

    if (!addFriend){
        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally){
            PostHeader(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.07f),
                backButton = {
                    back()
                },
                property1 = com.calculator.bookfinder.postheader.Property1.Default,
                text = "Create Group")
            Spacer(modifier = Modifier.height(30.dp))
            GroupProfilePicture(modifier = Modifier
                .height(147.dp)
                .width(147.dp), changePfpButton = {}, userInteractionViewmodel = userInteractionViewmodel,pfpName ,navController,getNewPfpName = { pfpName = it})

            Spacer(modifier = Modifier.height(30.dp))
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = groupName ,
                onValueChange ={
                    groupName = it
                },
                placeholder ={
                    Text(text = "Group Name")
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
                value = groupDescription ,
                onValueChange ={
                    groupDescription = it
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
            Row (modifier = Modifier
                .padding(start = 15.dp)
                .fillMaxWidth()){
                AddMember(
                    tagButton = {
                        addFriend = true // Show "Add Friend" section
                        userInteractionViewmodel.getUsernames()
                    },
                    modifier = Modifier
                        .rowWeight(1.0f)
                        .columnWeight(1.0f)
                        .height(38.dp)
                        .width(170.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            // Display selected group members as chips
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start) {
                items(groupMembers){member ->
                    Spacer(modifier = Modifier.width(15.dp))
                    InputChip(
                        selected = false,
                        onClick = {

                        },
                        label = {
                            Text(text = member)
                        },
                        trailingIcon = {
                            IconButton(
                                modifier = Modifier.size(28.dp),
                                onClick = {
                                    // Remove member from the list
                                    groupMembers = postsGroupsViewmodel.updateList(groupMembers,member,1)
                                    Log.d("groupMembers",groupMembers.toString())
                                    forcedRefresh+=1
                                }) {
                                Icon(
                                    Icons.Filled.Cancel,
                                    contentDescription = "Localized description",
                                    Modifier.size(InputChipDefaults.IconSize)
                                )
                            }

                        }
                    )
                }
            }
            Spacer(modifier = Modifier.fillMaxHeight(0.15f))
            AccountButtons(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(42.dp),
                property1 = Property1.Variant5,
                buttonName = "Create Group",
                buttonPressed = {
                    postsGroupsViewmodel.createGroup(groupName, groupDescription,groupMembers, succes = {
                        message = true
                        navController.popBackStack()},
                        pfpName = pfpName)
                }
            )

            if (message){
                back()
                Message("Group Created")
                message = false
            }

        }
    }else{
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.915f),
            horizontalAlignment = Alignment.CenterHorizontally){
            PostHeader(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.07f),
                backButton = {
                    addFriend = false
                },
                property1 = com.calculator.bookfinder.postheader.Property1.Default,
                text = "Add Friends")
            Spacer(modifier = Modifier.height(30.dp))
            FindFriendsScreen(userInteractionViewmodel)
            AddfriendsResults(userInteractionViewmodel,addFriend = {
                groupMembers = postsGroupsViewmodel.updateList(groupMembers,it,2)
                addFriend = false
            })
        }

    }

    Text(text = forcedRefresh.toString(), fontSize = 0.1.sp)
}


/**
 * Composable function that displays an "Add Member" button.
 *
 * @param modifier Modifier for the button layout.* @param tagButton Callback function to be executed when the button is clicked.
 */
@Composable
fun AddMember(
    modifier: Modifier = Modifier,
    tagButton: () -> Unit = {}
) {
    TopLevel(
        tagButton = tagButton,
        modifier = modifier,
    ) {
        Icon(
            Icons.Filled.AddCircle,
            contentDescription = "Localized description",
            modifier = Modifier.boxAlign(
                alignment = Alignment.CenterEnd,
                offset = DpOffset(
                    x = -21.0.dp,
                    y = 0.5.dp
                )
            )
        )
        Text(text = "Add Member", fontSize = 20.0.sp, fontFamily = lindenHill,
            modifier = Modifier.boxAlign(
                alignment = Alignment.CenterStart,
                offset = DpOffset(
                    x = 15.dp,
                    y = 0.dp
                )
            ))
    }
}


/**
 *  displays a list of usernames matching a search query, allowing the user to add them as friends.
 ** @param userInteractionViewmodel ViewModel for handling user interactions and data.
 * @param addFriend Callback function to be executed when a username is clicked, passing the username as a parameter.
 */
@Composable
fun AddfriendsResults(userInteractionViewmodel: UserInteractionViewmodel,addFriend:(String)->Unit){
    val matchingUsernames by userInteractionViewmodel.matchingUserNamesList.collectAsState()
    LazyColumn(modifier = Modifier.padding(top = 4.dp)){
        items(matchingUsernames){
            Row(modifier = Modifier
                .background(Color.White)
                .height(40.dp)
                .width(330.dp)
                .clickable {
                    addFriend(it)
                },
                verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.padding(start = 10.dp))
                Text(text = it,fontFamily = com.calculator.bookfinder.accountbuttons.lindenHill, fontSize = 20.sp, modifier = Modifier)
            }
            Spacer(modifier = Modifier.padding(2.dp))

        }
    }
}


/**
 * Composable function that displays a group's profile picture with an option to change it.
 *
 * @param modifier Modifierfor the overall layout.
 * @param changePfpButton Callback function to be executed when the "Change Profile Picture" button is clicked.
 * @param userInteractionViewmodel ViewModel for handling user interactions and image retrieval.
 * @param pfpName The name of the group's profile picture file inFirebase Storage.
 * @param navController Navigation controller
 * @param getNewPfpName Callback function to update the group's profile picture name after a new image is selected.
 */
@Composable
fun GroupProfilePicture(modifier: Modifier, changePfpButton:()->Unit, userInteractionViewmodel: UserInteractionViewmodel, pfpName:String,navController: NavController,getNewPfpName:(String)->Unit){
    var uri by remember { mutableStateOf(Uri.EMPTY) }
    // Launcher for picking an image from the device
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {
            getNewPfpName(userInteractionViewmodel.getImageNameFromUri(it.toString())) // Update profile picture name
            uri = it // Update URI state
            userInteractionViewmodel.uploadImageToFirebase(it,2) // Upload image to Firebase Storage
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
            Log.d("uri",uri.toString())
            if (uri == Uri.EMPTY){
                LoadGroupPfp(userInteractionViewmodel,pfpName)
            }else{
                AsyncImage(model = ImageRequest.Builder(LocalContext.current)
                    .data(uri)
                    .crossfade(true)
                    .build() , contentDescription ="test" ,modifier= Modifier.fillMaxSize(),contentScale = ContentScale.Crop)
            }

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


/**
 * Composable function that loads and displays a group's profile picture from Firebase Storage.
 *
 * @param userInteractionViewmodel ViewModel for handling user interactions and image retrieval.
 * @param pfpName The name of the group's profile picture file in Firebase Storage.
 */
@Composable
fun LoadGroupPfp(userInteractionViewmodel: UserInteractionViewmodel, pfpName:String){
    var oldImageUri by remember { mutableStateOf<Uri?>(null) }
    userInteractionViewmodel.getGroupImageFromFirebase(pfpName,imageUri = {
        oldImageUri = it
    })
    if (oldImageUri == null){
        Column (modifier= Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center){
            Loading(height = 50, width = 50)
        }

    }else{
        AsyncImage(model = oldImageUri , contentDescription ="test" ,modifier= Modifier.fillMaxSize(),contentScale = ContentScale.Crop)
    }

}






