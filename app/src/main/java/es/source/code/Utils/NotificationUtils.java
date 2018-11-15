package es.source.code.Utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.RequiresApi;
import es.source.code.R;


public class NotificationUtils extends ContextWrapper {

    private NotificationManager manager;
    public static final String id = "channel_1";
    public static final String name = "channel_name_1";
    public  Notification notification;

    public NotificationUtils(Context context){
        super(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createNotificationChannel(){
        NotificationChannel channel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
        getManager().createNotificationChannel(channel);
    }
    private NotificationManager getManager(){
        if (manager == null){
            manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        return manager;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getChannelNotification(String title, String content){
        return new Notification.Builder(getApplicationContext(), id)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.notice)
                .setAutoCancel(false);
    }
    public Notification.Builder getNotification_25(String title, String content){

        return new Notification.Builder(getApplicationContext())
                .setTicker("通知")      //状态栏文字
                .setContentText(content)
                .setContentTitle( title )
                .setSmallIcon(R.drawable.notice).setLargeIcon( BitmapFactory.decodeResource(getResources(),R.drawable.notice));
    }

    /**
     *   得到一个默认的notification
     * @param title  通知标题
     * @param content  通知内容
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public Notification basicNotification(String title, String content){
        if (Build.VERSION.SDK_INT>=26){
            createNotificationChannel();
            this.notification = getChannelNotification
                    (title, content).build();

        }else{
            this.notification = getNotification_25(title, content).build();
        }
        return this.notification;
    }

    /**
     * 对Notification进行设置后传入
     * @param notification
     */
    public void setNotification(Notification notification){
        this.notification = notification;
    }


    /**
     * 发送通知
     */
    public  void sendNotification(){

        getManager().notify(1,notification);

    }


}


