package com.gauravbora.task_2.services

import android.app.Service
import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.gauravbora.task_2.fabricio.notifications.database.DataBase
import com.gauravbora.task_2.fabricio.notifications.models.TrackNotification
import java.lang.StringBuilder

class NotificationService: NotificationListenerService() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return Service.START_STICKY
    }

    override fun onNotificationPosted(statusBarNotification: StatusBarNotification?) {
        statusBarNotification?.let {

            val bundle = it.notification.extras
            val messageBuilder = StringBuilder()
            var title = bundle["android.title"]?.toString()
            val text = bundle.getCharSequence("android.text")
            val subText = bundle.getCharSequence("android.subText")
            val charSequenceArray = bundle.getCharSequenceArray("android.textLines")

            if(charSequenceArray!=null){
                for(item in charSequenceArray){
                    messageBuilder.append(item)
                    messageBuilder.append(" ")
                }
            }else{
                if(subText!=null){
                    messageBuilder.append(subText)
                }else{
                    messageBuilder.append(text)
                }
            }
            if(title==null){
                title = ""
            }

            TrackNotification(0,title,it.packageName,messageBuilder.toString(), bundle.toString()).apply {
                DataBase.instance(baseContext).insert(this)
            }
        }
    }
}