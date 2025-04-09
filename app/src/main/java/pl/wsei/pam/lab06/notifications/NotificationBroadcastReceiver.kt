package pl.wsei.pam.lab06.notifications

import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import pl.wsei.pam.lab06.Lab06Activity

class NotificationBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra(Lab06Activity.titleExtra) ?: ""
        val message = intent.getStringExtra(Lab06Activity.messageExtra) ?: ""

        val notification = NotificationCompat.Builder(context, Lab06Activity.channelID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(Lab06Activity.notificationID, notification)
    }
}
