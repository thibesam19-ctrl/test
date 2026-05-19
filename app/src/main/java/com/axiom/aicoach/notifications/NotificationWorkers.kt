package com.axiom.aicoach.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

const val CHANNEL_REMINDERS = "axiom_reminders"
const val CHANNEL_ACHIEVEMENTS = "axiom_achievements"
const val CHANNEL_COACH = "axiom_coach"

fun createNotificationChannels(context: Context) {
    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    listOf(
        NotificationChannel(CHANNEL_REMINDERS, "Reminders", NotificationManager.IMPORTANCE_DEFAULT).apply {
            description = "Workout, meal, and water reminders"
        },
        NotificationChannel(CHANNEL_ACHIEVEMENTS, "Achievements", NotificationManager.IMPORTANCE_DEFAULT).apply {
            description = "Badges, streaks, and personal records"
        },
        NotificationChannel(CHANNEL_COACH, "Coach Messages", NotificationManager.IMPORTANCE_LOW).apply {
            description = "AI coach nudges and tips"
        },
    ).forEach { manager.createNotificationChannel(it) }
}

@HiltWorker
class WaterReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        showNotification(
            applicationContext,
            CHANNEL_REMINDERS,
            1001,
            "💧 Time to hydrate!",
            "You're at 60% of your water goal. Add a glass of water.",
        )
        return Result.success()
    }
}

@HiltWorker
class WorkoutReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        showNotification(
            applicationContext,
            CHANNEL_REMINDERS,
            1002,
            "🏋️ Workout time!",
            "Your Upper Body Power session is scheduled for today. Let's go!",
        )
        return Result.success()
    }
}

@HiltWorker
class WeeklyProgressWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        showNotification(
            applicationContext,
            CHANNEL_COACH,
            1003,
            "📊 Weekly Progress Report",
            "You completed 4 workouts and hit your calorie goal 5/7 days. Great week!",
        )
        return Result.success()
    }
}

private fun showNotification(context: Context, channelId: String, id: Int, title: String, message: String) {
    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val notification = NotificationCompat.Builder(context, channelId)
        .setContentTitle(title)
        .setContentText(message)
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .build()
    manager.notify(id, notification)
}

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            scheduleReminders(context)
        }
    }
}

class AxiomNotificationService : android.app.Service() {
    override fun onBind(intent: Intent?) = null
}

fun scheduleReminders(context: Context) {
    val workManager = WorkManager.getInstance(context)

    // Water reminder every 2 hours
    val waterWork = PeriodicWorkRequestBuilder<WaterReminderWorker>(2, TimeUnit.HOURS)
        .setConstraints(Constraints.Builder().build())
        .build()
    workManager.enqueueUniquePeriodicWork("water_reminder", ExistingPeriodicWorkPolicy.KEEP, waterWork)

    // Weekly progress every 7 days
    val weeklyWork = PeriodicWorkRequestBuilder<WeeklyProgressWorker>(7, TimeUnit.DAYS)
        .build()
    workManager.enqueueUniquePeriodicWork("weekly_progress", ExistingPeriodicWorkPolicy.KEEP, weeklyWork)
}
