package pl.wsei.pam.lab06

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import pl.wsei.pam.lab06.data.TodoDatabase
import pl.wsei.pam.lab06.data.TodoRepository
import pl.wsei.pam.lab06.ui.theme.Lab06Theme
import pl.wsei.pam.lab06.viewmodel.TodoViewModel
import pl.wsei.pam.lab06.viewmodel.TodoViewModelFactory

class Lab06Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dao = TodoDatabase.getDatabase(application).taskDao()

        val todoViewModel: TodoViewModel =
            ViewModelProvider(this, TodoViewModelFactory(dao))[TodoViewModel::class.java]

        setContent {
            Lab06Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(todoViewModel = todoViewModel)
                }
            }
        }
    }
}
