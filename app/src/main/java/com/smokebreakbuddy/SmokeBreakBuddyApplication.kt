package com.smokebreakbuddy

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class SmokeBreakBuddyApplication : Application(), Configuration.Provider {
    
    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    
    companion object {
        const val BREAK_NOTIFICATIONS_CHANNEL_ID = "break_notifications"
        const val GENERAL_NOTIFICATIONS_CHANNEL_ID = "general_notifications"
    }
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        
        // Create notification channels
        createNotificationChannels()
        
    }
    
    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }
    
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            // Break notifications channel
            val breakChannel = NotificationChannel(
                BREAK_NOTIFICATIONS_CHANNEL_ID,
                "Break Invitations",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for smoke break invitations"
                enableVibration(true)
                setShowBadge(true)
            }
            
            // General notifications channel
            val generalChannel = NotificationChannel(
                GENERAL_NOTIFICATIONS_CHANNEL_ID,
                "General Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "General app notifications"
                enableVibration(false)
                setShowBadge(false)
            }
            
            notificationManager.createNotificationChannel(breakChannel)
            notificationManager.createNotificationChannel(generalChannel)
        }
    }
}
