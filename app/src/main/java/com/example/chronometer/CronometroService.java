package com.example.chronometer;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.widget.RemoteViews;


import androidx.core.app.NotificationCompat;

public class CronometroService extends Service {

    private Handler handler;
    private long startTime = 0L;
    private long elapsedTime = 0L;

    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "cronometro_channel";

    public static final String START = "START";
    public static final String STOP = "STOP";

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        startForeground(NOTIFICATION_ID, buildNotification());

        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case START:
                        startCronometro();
                        break;
                    case STOP:
                        stopCronometro();
                        break;
                }
            }
        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateTimerRunnable);
    }

    private Runnable updateTimerRunnable = new Runnable() {
        @Override
        public void run() {
            elapsedTime = SystemClock.elapsedRealtime() - startTime;
            updateNotification(elapsedTime);
            handler.postDelayed(this, 1000);
        }
    };

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Cronometro Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private Notification buildNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, 0);

        Intent startIntent = new Intent(this, NotificationActionReceiver.class);
        startIntent.setAction("start");
        PendingIntent startPendingIntent = PendingIntent.getBroadcast(this, 0, startIntent, 0);

        Intent stopIntent = new Intent(this, NotificationActionReceiver.class);
        stopIntent.setAction("stop");
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(this, 0, stopIntent, 0);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Cronometro en ejecución")
                .setContentText("Tiempo transcurrido: 00:00:00")
                .setSmallIcon(R.drawable.notify)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.play, "Start", startPendingIntent)
                .addAction(R.drawable.stop, "Stop", stopPendingIntent)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle()) // Esto reemplaza el contenido de la notificación.
                .setCustomContentView(createNotificationView())
                .build();
    }

    private RemoteViews createNotificationView() {
        RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.xml.foreground_notification);

        // Configura el contenido de la vista remota según tus necesidades.
        return notificationLayout;
    }

    private void updateNotification(long timeInMillis) {
        int seconds = (int) (timeInMillis / 1000);
        int minutes = seconds / 60;
        int hours = minutes / 60;

        seconds = seconds % 60;
        minutes = minutes % 60;

        String time = String.format("%02d:%02d:%02d", hours, minutes, seconds);

        NotificationManager manager = getSystemService(NotificationManager.class);
        Notification notification = buildNotification();
        notification.contentView.setTextViewText(android.R.id.text1, "Tiempo transcurrido: " + time);
        manager.notify(NOTIFICATION_ID, notification);
    }

    private void startCronometro() {
        startTime = SystemClock.elapsedRealtime();
        handler.postDelayed(updateTimerRunnable, 1000);
    }

    private void stopCronometro() {
        handler.removeCallbacks(updateTimerRunnable);
    }
}