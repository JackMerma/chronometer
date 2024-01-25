package com.example.chronometer;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null) {
            Intent serviceIntent = new Intent(context, CronometroService.class);
            switch (action) {
                case "start":
                    // Enviar la acción de inicio al servicio
                    serviceIntent.setAction(CronometroService.START);
                    context.startService(serviceIntent);
                    break;
                case "stop":
                    // Enviar la acción de detener al servicio
                    serviceIntent.setAction(CronometroService.STOP);
                    context.startService(serviceIntent);
                    break;
            }
        }
    }
}