package es.source.code.br;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;


public class DeviceStartedListener extends BroadcastReceiver {

    private static final String TAG = "DeviceStartedListener";
    private static final String ACTION_BOOT = "android.intent.action.BOOT_COMPLETED";
    private static final String CLOSE_NOTIFICATION = "es.source.code.intent.action.CLOSE_NOTIFICATION";
    private static final String NOTIFICATION_ID = "notification_id";

    @Override
    public void onReceive(Context context, Intent intent) {

        if(ACTION_BOOT.equals(intent.getAction())){

            Intent myIntent = new Intent("es.source.code.intent.action.updateService");
            myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName componentName = new ComponentName("es.source.code", "es.source.code.service.UpdateService");
            myIntent.setComponent(componentName);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(myIntent);
            } else {
                context.startService(myIntent);
            }

            Log.d(TAG, "onReceive: DeviceStartedListener");
        }
        else if (CLOSE_NOTIFICATION.equals(intent.getAction())) {
            NotificationManager notifyManager = (NotificationManager) context.getSystemService(Context
                    .NOTIFICATION_SERVICE);
            notifyManager.cancel(intent.getIntExtra(NOTIFICATION_ID, 0));
        }
    }
}
