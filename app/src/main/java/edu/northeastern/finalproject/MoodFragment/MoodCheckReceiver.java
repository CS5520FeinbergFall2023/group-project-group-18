package edu.northeastern.finalproject.MoodFragment;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import edu.northeastern.finalproject.R;

public class MoodCheckReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences preferences = context.getSharedPreferences("MoodPreferences", Context.MODE_PRIVATE);
        String lastMoodDate = preferences.getString("LastMoodDate", "");
        String currentDate = getCurrentDate();

        if (!currentDate.equals(lastMoodDate)) {
            showMoodReminderNotification(context);
        }
    }

    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(new Date());
    }
    private void showMoodReminderNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "mood_reminder_channel";
        CharSequence channelName = "Mood Reminder Notifications";

        // Create a notification channel for Android 8.0 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel( channelId,channelName, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "mood_reminder_channel")
                .setSmallIcon(R.drawable.ic_launcher_mindharbor_foreground)
                .setContentTitle("Mood Tracker Reminder")
                .setContentText("Don't forget to record your mood today!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify(1, builder.build());
    }
}