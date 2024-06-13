package ui.Screens


import android.content.Context
import android.view.ViewTreeObserver
import android.view.WindowInsets
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Gif
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults.textFieldColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.test.isFocused
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import com.calculator.bookfinder.accountbuttons.lindenHill
import com.calculator.bookfinder.postheader.CreatePostProperty1Default
import com.calculator.bookfinder.postheader.CreatePostProperty1Variant2
import com.calculator.bookfinder.postheader.LogOutIconProperty1Variant2
import com.calculator.bookfinder.postheader.PostHeader
import com.calculator.bookfinder.postheader.Property1
import com.calculator.bookfinder.postheader.TopLevelProperty1Default
import com.calculator.bookfinder.postheader.TopLevelProperty1Variant2
import com.calculator.bookfinder.postheader.Vector11Property1Default
import com.calculator.bookfinder.postheader.Vector11Property1Variant2
import com.calculator.bookfinder.postheader.VectorProperty1Variant2
import com.google.firebase.Timestamp
import com.google.relay.compose.ColumnScopeInstanceImpl.weight
import com.google.relay.compose.RelayText
import kotlinx.coroutines.launch
import ui.ViewModel.PostsGroupsViewmodel
import ui.state.GroupState


@Composable
fun MessagesScreen(postsGroupsViewmodel:PostsGroupsViewmodel,navController: NavController){
    val group by postsGroupsViewmodel.currentGroupId.collectAsState()

    Column(modifier = Modifier
        .background(Color(0xFFE5DBD0))
        .fillMaxSize()) {

        GroupMessages(postsGroupsViewmodel,navController,group)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupMessages(groupsViewmodel: PostsGroupsViewmodel,navController: NavController,group:GroupState){
    var messages by remember { mutableStateOf(listOf<Map<String,Map<String,String>>>()) }
    groupsViewmodel.getMessages(group.groupID,messages={
        messages = it
    })
    var currentMessage by remember { mutableStateOf("") }
    var forcedUpdate by remember { mutableStateOf(0) }
    var columnSize by remember { mutableStateOf(0.915f) }
    val bool by keyboardAsState()
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

        Column {
            if (bool){
                Spacer(modifier = Modifier.height(220.dp))
                columnSize = 0.88f
            }else{
                columnSize = 0.915f
            }
            PostHeaderEdit(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.07f),
                backButton = {
                    navController.popBackStack()
                },
                text = group.groupName)
            Spacer(modifier = Modifier.height(10.dp))
            LazyColumn(
                state = scrollState,
                modifier = Modifier
                .fillMaxHeight(columnSize),
                verticalArrangement = Arrangement.Bottom){
                items(messages) {
                    val contentAlign: Arrangement.Horizontal
                    if(it.keys.first() == groupsViewmodel.userNamegroups()){
                        contentAlign = Arrangement.End
                    }else{
                        contentAlign = Arrangement.Start
                    }
                    Row (modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, end = 10.dp, start = 10.dp),
                        horizontalArrangement = contentAlign){
                        Row (modifier = Modifier
                            .widthIn(max = 280.dp)
                            .background(Color.White, RoundedCornerShape(5.dp))){
                            Column(modifier = Modifier.padding(5.dp)) {
                                Text(text = it.keys.first(), fontSize = 12.sp)
                                Text(text = it.values.first().values.elementAt(1).toString())
                            }
                        }

                    }

                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier
                .height(60.dp)
                .background(Color.White, RoundedCornerShape(5.dp))
                .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically){
                TextField(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                    ,
                    value = currentMessage,
                    onValueChange = {
                        currentMessage = it
                        coroutineScope.launch {
                            scrollState.scrollToItem(messages.size-1,0)
                        }
                                    },
                    colors = textFieldColors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        cursorColor = Color.Black,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    placeholder = {
                        Text(text = "Start typing...",fontFamily = lindenHill,modifier = Modifier)
                    }
                )
                IconButton(onClick = {
                    val time = Timestamp.now().toString()
                    if (currentMessage != ""){
                        groupsViewmodel.SendMessage(groupsViewmodel.userNamegroups(),ui.state.Message(time,currentMessage),group.groupID)
                        currentMessage = ""
                    }
                })
                {
                Icon(
                    modifier = Modifier
                        .size(24.dp),
                    imageVector = Icons.AutoMirrored.Filled.Send ,
                    contentDescription = "Localized description",
                    )
                }
            }
            if (bool){
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    forcedUpdate += 1
    forcedUpdate -= 1
    Text(text = forcedUpdate.toString(), modifier = Modifier.size(0.1.dp))
}




@Composable
fun keyboardAsState(): State<Boolean> {
    val view = LocalView.current
    var isImeVisible by remember { mutableStateOf(false) }

    DisposableEffect(LocalWindowInfo.current) {
        val listener = ViewTreeObserver.OnPreDrawListener {
            isImeVisible = ViewCompat.getRootWindowInsets(view)
                ?.isVisible(WindowInsetsCompat.Type.ime()) == true
            true
        }
        view.viewTreeObserver.addOnPreDrawListener(listener)
        onDispose {
            view.viewTreeObserver.removeOnPreDrawListener(listener)
        }
    }
    return rememberUpdatedState(isImeVisible)
}

@Composable
fun PostHeaderEdit(
    modifier: Modifier = Modifier,
    text: String = "",
    backButton: () -> Unit = {},

) {
    TopLevelProperty1Default(modifier = modifier) {
        CreatePostProperty1DefaultEdit(
            text = text,
            modifier = Modifier.boxAlign(
                alignment = Alignment.CenterStart,
                offset = DpOffset(
                    x = 52.6829833984375.dp,
                    y = 0.0.dp
                )
            )
        )
        Vector11Property1Default(
            backButton = backButton,
            modifier = Modifier.boxAlign(
                alignment = Alignment.CenterStart,
                offset = DpOffset(
                    x = 13.0.dp,
                    y = -2.0639820098876953.dp
                )
            )
        )
    }
}

@Composable
fun CreatePostProperty1DefaultEdit(
    text: String,
    modifier: Modifier = Modifier
) {
    RelayText(
        content = text,
        fontSize = 20.0.sp,
        fontFamily = com.calculator.bookfinder.postheader.lindenHill,
        height = 1.347900390625.em,
        textAlign = TextAlign.Left,
        maxLines = -1,
        modifier = modifier.wrapContentHeight(
            align = Alignment.CenterVertically,
            unbounded = true
        )
    )
}





