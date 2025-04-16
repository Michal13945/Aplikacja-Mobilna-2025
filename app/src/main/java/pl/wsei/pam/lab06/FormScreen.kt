package pl.wsei.pam.lab06

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import pl.wsei.pam.lab06.viewmodel.TodoViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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

            Text("Priorytet", style = MaterialTheme.typography.bodyMedium)
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
                onClick = onClick@{
                    if (date.isEmpty()) {
                        Toast.makeText(context, "Wybierz datę!", Toast.LENGTH_SHORT).show()
                        return@onClick
                    }

                    val formatter = DateTimeFormatter.ofPattern("d/M/yyyy")
                    val selectedDate = LocalDate.parse(date, formatter)
                    val today = LocalDate.now()

                    if (selectedDate.isBefore(today)) {
                        Toast.makeText(context, "Nie można wybrać daty z przeszłości!", Toast.LENGTH_LONG).show()
                        return@onClick
                    }

                    val task = TodoTask(
                        id = 0,
                        title = taskTitle,
                        content = content,
                        deadline = date,
                        isDone = isDone,
                        priority = priority
                    )
                    viewModel.addTask(task)
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Zapisz")
            }
        }
    }
}

fun showDatePicker(context: android.content.Context, onDateSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
            val formattedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            onDateSelected(formattedDate)
        },
        year, month, day
    )

    datePickerDialog.datePicker.minDate = System.currentTimeMillis()

    datePickerDialog.show()
}
