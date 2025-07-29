package com.example.beautyyapp;

import android.app.*;
import android.content.*;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;

public class ReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String waktu = intent.getStringExtra("waktu");
        if (waktu == null) return;

        Log.d("ReminderReceiver", "Alarm diterima untuk: " + waktu);

        // Tampilkan notifikasi
        tampilkanNotifikasi(context, waktu);

        // Jadwalkan ulang alarm untuk besok
        jadwalkanUlangAlarm(context, waktu);
    }

    private void tampilkanNotifikasi(Context context, String waktu) {
        String channelId = "skincare_reminder_channel";
        String channelName = "Skincare Reminder";
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Intent ke TimerLangkahActivity saat notifikasi diklik
        Intent openIntent = new Intent(context, TimerLangkahActivity.class);
        openIntent.putExtra("waktu", waktu);
        openIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        int requestCode = waktu.equals("pagi") ? 100 : 200;

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                requestCode,
                openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Buat channel notifikasi untuk Android O+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Pengingat skincare harian");
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setSound(soundUri, audioAttributes);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Waktunya Skincare!")
                .setContentText("Saatnya skincare " + waktu + " kamu 🌼")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setSound(soundUri) // Untuk Android < Oreo
                .setContentIntent(pendingIntent);

        manager.notify(requestCode, builder.build());
    }

    private void jadwalkanUlangAlarm(Context context, String waktu) {
        int jam = waktu.equals("pagi") ? 7 : 21;
        int menit = 0;

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR_OF_DAY, jam);
        calendar.set(Calendar.MINUTE, menit);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra("waktu", waktu);

        int requestCode = waktu.equals("pagi") ? 100 : 200;

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
            );
        } else {
            alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
            );
        }
    }
}

