package com.example.vertlechemin.ui.theme.screen.login.parameters

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun ParametersPopupDialog(onDismiss: () -> Unit) {
    var birthDate by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF8B6C6A))
                .padding(24.dp)
        ) {
            Column {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopEnd) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fermer", tint = Color.White)
                    }
                }

                Text(
                    text = "Informations",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Divider(color = Color.Black, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))

                TextField(
                    value = birthDate,
                    onValueChange = { birthDate = it },
                    label = { Text("Naissance : .. / .. / ..") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom :") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Sexe", color = Color.White, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf("H", "F", "X").forEach { gender ->
                        val isSelected = selectedGender == gender
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) Color(0xFFD9B26F) else Color.White)
                                .clickable { selectedGender = gender },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(gender, fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 20.sp)
                        }
                    }
                }
            }
        }
    }
}
