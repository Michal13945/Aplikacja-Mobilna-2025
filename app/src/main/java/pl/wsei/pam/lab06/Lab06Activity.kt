package pl.wsei.pam.lab06

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import pl.wsei.pam.lab06.data.TodoDatabase
import pl.wsei.pam.lab06.notifications.NotificationBroadcastReceiver
import pl.wsei.pam.lab06.viewmodel.TodoViewModel
import pl.wsei.pam.lab06.viewmodel.TodoViewModelFactory
import pl.wsei.pam.lab06.ui.theme.Lab06Theme

class Lab06Activity : ComponentActivity() {

    companion object {
        const val notificationID = 121
        const val channelID = "Lab06 channel"
        const val titleExtra = "title"
        const val messageExtra = "message"
    }

    private lateinit var todoViewModel: TodoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dao = TodoDatabase.getDatabase(applicationContext).taskDao()
        val factory = TodoViewModelFactory(dao)
        todoViewModel = ViewModelProvider(this, factory).get(TodoViewModel::class.java)

        setContent {
            Lab06Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(todoViewModel)
                }
            }
        }
    }

    // Metoda do ustawiania powiadomienia po 30 sekundach od utworzenia zadania
    fun scheduleNotificationForNewTask(task: TodoTask) {
        val triggerTime = System.currentTimeMillis() + 30000  // 30 sekund od teraz
        val intent = Intent(applicationContext, NotificationBroadcastReceiver::class.java).apply {
            putExtra(titleExtra, task.title)
            putExtra(messageExtra, task.content)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            pendingIntent
        )
    }
}
