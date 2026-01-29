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
fun SignUpScreen(
    onSignUpSuccess: (Int) -> Unit,
    onNavigateBack: () -> Unit
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
        Text("Create Account", style = MaterialTheme.typography.headlineMedium)

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

        Button(
            onClick = {
                scope.launch {
                    if (username.isBlank() || password.isBlank()) {
                        errorMessage = "Please fill all fields"
                        return@launch
                    }
                    try {
                        val newUser = User(username = username, password = password)
                        val newId = db.appDao().insertUser(newUser)
                        Toast.makeText(context, "Account Created!", Toast.LENGTH_SHORT).show()
                        onSignUpSuccess(newId.toInt())
                    } catch (e: Exception) {
                        errorMessage = "Username already exists"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign Up")
        }

        TextButton(onClick = onNavigateBack) {
            Text("Already have an account? Log In")
        }
    }
}