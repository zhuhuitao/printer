package com.huitao.printerdemo.service

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.huitao.printerdemo.R

/**
 *author  : huitao
 *e-mail  : pig.huitao@gmail.com
 *date    : 2021/5/17 18:39
 *desc    :
 *version :
 */
class AncillaryService:Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(this)
        }
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
    }

    companion object{
        fun startForeground(ctx: Service) {
            try {
                val CHANNEL_ONE_ID = "CHANNEL_ONE_ID"
                val CHANNEL_ONE_NAME = "CHANNEL_ONE_ID"
                val SERVICE_ID = 802
                val notificationChannel: NotificationChannel
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notificationChannel = NotificationChannel(
                        CHANNEL_ONE_ID, CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH
                    )
                    notificationChannel.enableLights(true)
                    notificationChannel.lightColor = R.color.app_them
                    notificationChannel.setShowBadge(true)
                    notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                    val nm = ctx.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                    nm.createNotificationChannel(notificationChannel)
                }
                val intent = Intent()
                val className = Class.forName("com.huitao.printerdemo.printer.PrinterActivity")
                intent.setClassName(ctx, className.name)
                val pendingIntent = PendingIntent.getActivity(ctx, 0, intent, 0)
                val builder = NotificationCompat.Builder(ctx, CHANNEL_ONE_ID)
                builder.setContentTitle(ctx.getString(R.string.pending_title))
                    .setContentText(ctx.getString(R.string.pending_content))
                    .setWhen(System.currentTimeMillis())
                    .setPriority(Notification.PRIORITY_MIN)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                val notification = builder.build()
                ctx.startForeground(SERVICE_ID, notification)
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }
        }
    }
}