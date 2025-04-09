package pl.wsei.pam.lab06

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import pl.wsei.pam.lab06.viewmodel.TodoViewModel
import java.util.*

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FormScreen(navController: NavController, viewModel: TodoViewModel) {
    var taskTitle by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("Medium") }
    var isDone by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            AppTopBar(
                navController = navController,
                title = "Form",
                showBackIcon = true,
                route = "list"
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = taskTitle,
                onValueChange = { taskTitle = it },
                label = { Text("Tytuł taska") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Treść zadania") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { showDatePicker(context) { selectedDate -> date = selectedDate } },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (date.isEmpty()) "Wybierz datę" else "Data: $date")
            }

            Text("Priority", style = MaterialTheme.typography.bodyMedium)
            Row {
                listOf("Low", "Medium", "High").forEach { option ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = priority == option,
                            onClick = { priority = option }
                        )
                        Text(text = option)
                    }
                }
            }

            Button(
                onClick = {
                    val task = TodoTask(
                        id = 0,
                        title = taskTitle,
                        content = content,
                        deadline = date,
                        isDone = isDone,
                        priority = priority
                    )

                    // Przekazujemy callback do addTask
                    viewModel.addTask(task) {
                        // Po dodaniu zadania wywołujemy powiadomienie
                        (context as? Lab06Activity)?.scheduleNotificationForNewTask(task)
                    }
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Zapisz")
            }
        }
    }
}
