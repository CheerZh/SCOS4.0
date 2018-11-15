package es.source.code.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import es.source.code.Activity.FoodDetailed;
import es.source.code.Activity.MainScreen;
import es.source.code.R;
import es.source.code.Utils.Const;
import es.source.code.Utils.HttpUtil;
import es.source.code.Utils.NotificationUtils;


import static java.lang.Thread.sleep;

public class UpdateService extends IntentService {

    private final String TAG = "updateService";
    private final int NOTIFYID = 0x123;            //通知的ID
    private static final int    DEFAULT_ID = 1001;
    private static final String channel_id = "channel_1";
    private static final String channel_name = "channel_name_1";
    private static final String TITLE      = "新品上架!";
    private NotificationUtils notificationUtils;
    Notification serviceNotification;

    public UpdateService (){
        super("updateService");
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: ");

        try{
            System.out.print("onCreate:UpdateService");
            notificationUtils = new NotificationUtils(this);
            serviceNotification = notificationUtils.basicNotification("SCOS","UpdateService is on");
//            notificationUtils.sendNotification();
            startForeground(1, serviceNotification);
        }catch (Exception e){
            e.printStackTrace();
        }

        super.onCreate();

    }

    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        Log.d(TAG, "onStart: ");
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {

        
        Log.d(TAG, "onStartCommand: ");
//        return super.onStartCommand(intent, flags, startId);
        onStart(intent, startId);
        boolean mStartCompatibility = getApplicationInfo().targetSdkVersion  < Build.VERSION_CODES.ECLAIR;
        return mStartCompatibility ? START_STICKY_COMPATIBILITY : START_STICKY;
    }

    @Override
    public void onDestroy() {

        Log.d(TAG, "onDestroy: ");
//        stopForeground(true);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        Log.d(TAG, "onHandleIntent: begin");

        try{

            String resp = HttpUtil.getRequest(Const.baseUrl+"/FoodUpdateService");  //请求服务器数据

            Log.d(TAG, "onHandleIntent: "+resp);

            JSONObject jsonObject = JSONObject.fromObject(resp);
            int count = jsonObject.getInt("count");
            JSONArray jsonArray = jsonObject.getJSONArray("jsonArray");
            String message = "新推出"+count+"道美食：\n";
            for(int i=0;i<count;i++){
                JSONObject food = jsonArray.getJSONObject(i);
//                message +=food.getString("name")+":"+food.getString("price")+"\n";
                message +=food.getString("name")+",\n";

            }

            Log.d(TAG, "onHandleIntent: 通知");

            Notification.Builder myBuilder = null;
            final NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT>=26) {  //版本26以上的需要设置channel
                NotificationChannel channel = new NotificationChannel(channel_id, channel_name, NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
                myBuilder = new Notification.Builder(this, channel_id);  // NotificationBuilder
            }
            else {  //版本26以下
                myBuilder = new Notification.Builder(this); // NotificationBuilder
            }

            Intent mIntent = new Intent(UpdateService.this, MainScreen.class);
            PendingIntent pi = PendingIntent.getActivity(
                    UpdateService.this, 0, mIntent, 0);

            myBuilder.setContentTitle(TITLE)// 设置通知内容的标题
                    .setContentText(message)// 设置通知内容
                    .setSmallIcon(R.drawable.notice) // 设置通知的图标
                    .setAutoCancel(true)// 设置打开该通知，该通知自动消失
                    .setWhen(System.currentTimeMillis())//设置发送时间
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)//设置使用系统默认的声音、默认震动
                    .setContentIntent(pi);  //设置通知栏点击跳转

            Notification notification = myBuilder.build();
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify(1,notification);

            //发送提示音
//            playNotification();


            sleep(500000);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void playNotification() {

        Log.d(TAG, "playNotification: ");

        Uri ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        MediaPlayer mediaPlayer = MediaPlayer.create(this, ringtone);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
    }
}
