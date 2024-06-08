package ui.Screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.calculator.bookfinder.accountbuttons.AccountButtons
import com.calculator.bookfinder.accountbuttons.Property1
import com.calculator.bookfinder.accountbuttons.lindenHill
import com.calculator.bookfinder.icons.Name
import com.calculator.bookfinder.icons.UserFaceMaleStreamlinePlump1
import com.calculator.bookfinder.icons.UserInstance
import com.calculator.bookfinder.link.Link
import com.calculator.bookfinder.signinemail.EmailIcon
import com.calculator.bookfinder.signinemail.TopLevel
import com.calculator.bookfinder.signinpassword.Email
import com.calculator.bookfinder.signinpassword.PasswordIcon
import com.google.relay.compose.BoxScopeInstance.boxAlign
import com.google.relay.compose.BoxScopeInstance.columnWeight
import com.google.relay.compose.BoxScopeInstance.rowWeight
import data.Models.Link
import data.Routes.Routes
import ui.ViewModel.LoginViewModel

/**
 * Function that displays the information on the register screen
 *   @param loginViewModel view-model that contains the logic behind certain functions in the screen
 *   @param navController controller that allows navigation between screens
 */
@Composable
fun RegisterScreen(loginViewModel: LoginViewModel, navController: NavController){
    val errorMessage by loginViewModel.errorMessage.collectAsState()
    val errorMessageBool by loginViewModel.displayErrorMessage.collectAsState()
    Column(modifier= Modifier
        .fillMaxSize()
        .background(color = Color(0xFFE5DBD0)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top){
        Spacer(modifier = Modifier.fillMaxHeight(0.025f))
        Text(text = "Register", fontFamily = lindenHill, fontSize = 50.sp,
            color = Color(
                alpha = 255,
                red = 0,
                green = 0,
                blue = 0
            )
        )
        Spacer(modifier = Modifier.fillMaxHeight(0.02f))
        FullNameSection(modifier = Modifier
            .rowWeight(1.0f)
            .columnWeight(1.0f)
            .width(335.dp)
            .height(75.dp)
            ,loginViewModel,"Full Name")
        Spacer(modifier = Modifier.fillMaxHeight(0.035f))
        UserNameSection(modifier = Modifier
            .rowWeight(1.0f)
            .columnWeight(1.0f)
            .width(335.dp)
            .height(75.dp)
            ,loginViewModel,"Username")
        Spacer(modifier = Modifier.fillMaxHeight(0.035f))
        EmailSection(modifier = Modifier
            .rowWeight(1.0f)
            .columnWeight(1.0f)
            .width(335.dp)
            .height(75.dp)
            ,loginViewModel,"Email")
        Spacer(modifier = Modifier.fillMaxHeight(0.045f))
        RegisterPasswordSection(modifier = Modifier
            .rowWeight(1.0f)
            .columnWeight(1.0f)
            .width(335.dp)
            .height(75.dp)
            ,loginViewModel,"Password")
        Spacer(modifier = Modifier.fillMaxHeight(0.05f))
        AccountButtons(
            buttonPressed = {
                loginViewModel.checkUserName { navController.navigate(Routes.HomeScreen.route) }
            },
            buttonName = "Register",
            property1 = Property1.Default,
            modifier = Modifier
                .rowWeight(1.0f)
                .columnWeight(1.0f)
                .height(75.dp)
                .width(335.dp)
        )
        Spacer(modifier = Modifier.fillMaxHeight(0.02f))
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Spacer(modifier = Modifier.fillMaxWidth(0.1f))
            Link(
                registerButton = {
                    navController.navigate(Routes.LoginScreen.route)
                    loginViewModel.changeError()
                    loginViewModel.reset()
                                 },
                property1 = com.calculator.bookfinder.link.Property1.Variant2,
                modifier = Modifier
                    .rowWeight(1.0f)
                    .columnWeight(1.0f)
            )
        }



    }
    if (errorMessageBool){
        Toast.makeText(LocalContext.current,errorMessage,Toast.LENGTH_SHORT).show()
        loginViewModel.DontdisplayError()
    }
}



/**
 * Displays the register password section of the login screen.
 *
 * @param modifier Modifier for the register password section
 * @param loginViewModel view-model that contains the logic behind certain functions in the screen
 * @param placeholder Placeholder text for the password field
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterPasswordSection(modifier: Modifier = Modifier, loginViewModel: LoginViewModel, placeholder:String) {
    val wrongInfoBool by loginViewModel.wrongInfo.collectAsState()

    var passwordHidden by rememberSaveable { mutableStateOf(true) }
    TopLevel(modifier = modifier) {
        PasswordIcon(
            modifier = Modifier.boxAlign(
                alignment = Alignment.TopStart,
                offset = DpOffset(
                    x = 15.0.dp,
                    y = 13.0.dp
                )
            )
        )
        Email(
            modifier = Modifier.boxAlign(
                alignment = Alignment.TopStart,
                offset = DpOffset(
                    x = 60.0.dp,
                    y = 5.dp
                )
            )
        ) {
            TextField(
                value = loginViewModel.password,
                onValueChange = {
                    loginViewModel.changePassword(it)
                    loginViewModel.changeError()
                },
                singleLine = true,
                placeholder = { Text(text = placeholder,fontFamily = lindenHill) },
                colors = ExposedDropdownMenuDefaults.textFieldColors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = Color.Black,
                    focusedIndicatorColor = Color.Black,
                    unfocusedIndicatorColor = Color.Black
                ),
                visualTransformation =
                if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { passwordHidden = !passwordHidden }) {
                        val visibilityIcon =
                            if (passwordHidden) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        val description = if (passwordHidden) "Show password" else "Hide password"
                        Icon(imageVector = visibilityIcon, contentDescription = description)
                    }

                },
                isError = wrongInfoBool

            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullNameSection(modifier: Modifier = Modifier,loginViewModel: LoginViewModel,placeHolder:String) {
    val wrongInfoBool by loginViewModel.wrongInfo.collectAsState()
    var email by rememberSaveable { mutableStateOf("") }
    TopLevel(modifier = modifier) {
        Name(
            modifier = Modifier.boxAlign(
                alignment = Alignment.TopStart,
                offset = DpOffset(
                    x = 15.0.dp,
                    y = 13.0.dp
                )
            )
        ){
            UserFaceMaleStreamlinePlump1(modifier = Modifier.rowWeight(1.0f).columnWeight(1.0f))
        }

        com.calculator.bookfinder.signinemail.Email(
            modifier = Modifier.boxAlign(
                alignment = Alignment.TopStart,
                offset = DpOffset(
                    x = 60.0.dp,
                    y = 5.dp
                )
            )
        ) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.Transparent),
                value = loginViewModel.name,
                onValueChange = {
                    loginViewModel.changeName(it)
                    loginViewModel.changeError()
                },
                placeholder = {
                    Text(text = placeHolder, fontFamily = lindenHill, modifier = Modifier)
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = Color.Black,
                    focusedIndicatorColor = Color.Black,
                    unfocusedIndicatorColor = Color.Black
                ),
                singleLine = true,
                isError = wrongInfoBool
            )
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserNameSection(modifier: Modifier = Modifier,loginViewModel: LoginViewModel,placeHolder:String) {
    val wrongInfoBool by loginViewModel.wrongInfo.collectAsState()
    var email by rememberSaveable { mutableStateOf("") }
    TopLevel(modifier = modifier) {
        UserInstance(
            modifier = Modifier.boxAlign(
                alignment = Alignment.TopStart,
                offset = DpOffset(
                    x = 15.0.dp,
                    y = 13.0.dp
                )
            )
        )

        com.calculator.bookfinder.signinemail.Email(
            modifier = Modifier.boxAlign(
                alignment = Alignment.TopStart,
                offset = DpOffset(
                    x = 60.0.dp,
                    y = 5.dp
                )
            )
        ) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.Transparent),
                value = loginViewModel.username,
                onValueChange = {
                    loginViewModel.changeUsername(it)
                    loginViewModel.changeError()
                },
                placeholder = {
                    Text(text = placeHolder, fontFamily = lindenHill, modifier = Modifier)
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = Color.Black,
                    focusedIndicatorColor = Color.Black,
                    unfocusedIndicatorColor = Color.Black
                ),
                singleLine = true,
                isError = wrongInfoBool
            )
        }
    }
}