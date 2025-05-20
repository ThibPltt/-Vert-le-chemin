package com.example.vertlechemin.ui.theme.screen.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ForgotPasswordDialog(
    onDismiss: () -> Unit,
    onResetPassword: (String, String) -> Unit
) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Réinitialiser le mot de passe") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .width(350.dp)
                    .padding(vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("Nouveau mot de passe") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirmer le mot de passe") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (error != null) {
                    Text(
                        text = error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (newPassword.isBlank() || confirmPassword.isBlank()) {
                    error = "Veuillez remplir les deux champs."
                } else if (newPassword != confirmPassword) {
                    error = "Les mots de passe ne correspondent pas."
                } else {
                    error = null
                    onResetPassword(newPassword, confirmPassword)
                }
            }) {
                Text("Réinitialiser")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}
