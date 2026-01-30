package np.ict.mad.mad_ca

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import np.ict.mad.mad_ca.data.AppDatabase
import np.ict.mad.mad_ca.data.User

@Composable
fun LoginScreen(
    onLoginSuccess: (Int) -> Unit
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val scope = rememberCoroutineScope()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Wack-a-Mole", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        if (errorMessage.isNotEmpty()) {
            Text(errorMessage, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // BUTTON 1: LOGIN
        Button(
            onClick = {
                scope.launch {
                    val user = db.appDao().login(username, password)
                    if (user != null) {
                        onLoginSuccess(user.userId)
                    } else {
                        errorMessage = "Invalid Credentials"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // BUTTON 2: SIGN UP
        Button(
            onClick = {
                scope.launch {
                    if (username.isBlank() || password.isBlank()) {
                        errorMessage = "Please fill all fields"
                        return@launch
                    }

                    val isTaken = db.appDao().isUsernameTaken(username)

                    if (isTaken) {
                        errorMessage = "Username '$username' already exists!"
                        username = ""
                        password = ""
                        return@launch
                    } else {
                        val newUser = User(username = username, password = password)
                        val newId = db.appDao().insertUser(newUser)
                        onLoginSuccess(newId.toInt())
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign Up")
        }
    }
}