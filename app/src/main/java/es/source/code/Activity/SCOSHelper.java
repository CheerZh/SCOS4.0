package es.source.code.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.source.code.R;
import es.source.code.Utils.Const;
import es.source.code.Utils.MailSender;
import es.source.code.Utils.MailUtils;
import es.source.code.model.User;

public class SCOSHelper extends Activity {
    User user = null;
    private List<Map<String, Object>> dataList;
    private SimpleAdapter adapter;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scos_helper);
        initData();

        View view= this.getLayoutInflater().inflate((R.layout.scos_helper), null);
        GridView gridView = (GridView) findViewById(R.id.grid_view_helper);
        String[] from={"img","text"};//键
        int[] to={R.id.img, R.id.text};//值
        adapter = new SimpleAdapter(this, dataList, R.layout.gridview_item, from, to);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                if(dataList.get(arg2).get("text")=="人工帮助"){
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (ActivityCompat.checkSelfPermission(SCOSHelper.this, Manifest.permission.CALL_PHONE)
                                    != PackageManager.PERMISSION_GRANTED) {
                                Toast.makeText(SCOSHelper.this, "请授权！", Toast.LENGTH_LONG).show();
                                ActivityCompat.requestPermissions(SCOSHelper.this, new String[]
                                        {Manifest.permission.CALL_PHONE}, 1);
                            }
                            else call();
                        }
                        else{
                            call();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else if(dataList.get(arg2).get("text")=="短信帮助") {
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (ActivityCompat.checkSelfPermission(SCOSHelper.this, Manifest.permission.SEND_SMS)
                                    != PackageManager.PERMISSION_GRANTED) {
                                Toast.makeText(SCOSHelper.this, "请授权！", Toast.LENGTH_LONG).show();
                                ActivityCompat.requestPermissions(SCOSHelper.this, new String[]
                                        {Manifest.permission.SEND_SMS}, 1);
                            } else sendMessage();
                        } else {
                            sendMessage();
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else if(dataList.get(arg2).get("text")=="邮件帮助") {

                    sendEmail(0, "断章\n\n"+
                            "你在桥上看风景\n" +
                            "看风景的人在楼上看你\n" +
                            "明月装点了你的窗\n" +
                            "你装点了别人的梦\n\n" +
                            "从前的日子慢\n" +
                            "一生只够爱一个人\n\n" +
                            "我们站着不说话\n" +
                            "就十分美好\n\n" +
                            "别说话，泪水你别带走\n" +
                            "我不是归人，只是个过客\n" +
                            "别回眸，末班车要开了\n" +
                            "你有你的，我有我的，方向\n\n"+
                            "如果是有人要问\n" +
                            "就说没有那个人\n" +
                            "像春风里一出梦\n" +
                            "像梦里的一声钟" );
                }
                else{
                    AlertDialog.Builder builder= new AlertDialog.Builder(SCOSHelper.this);
                    builder.setTitle("提示").setMessage("抱歉，"+
                            dataList.get(arg2).get("text").toString()+"功能尚未实现").create().show();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                switch (permissions[0]){
                    case Manifest.permission.CALL_PHONE://拨打电话权限
                        if (grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                            call();
                        } else {
                            Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case Manifest.permission.SEND_SMS:   //发送信息权限
                        if (grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                            sendMessage();
                        }else {
                            Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                        }
                        break;

                    default:
                }
                break;
            default:
        }
    }

    void sendMessage(){
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage("5554", null,
                    "test scos helper", null, null);
        Toast.makeText(SCOSHelper.this, "求助短信发送成功", Toast.LENGTH_LONG).show();
        }

    void call(){
        Intent intent=  new Intent(Intent.ACTION_DIAL,Uri.parse("tel:5554"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    private void sendEmail(final int type, final String msg) {

        @SuppressLint("HandlerLeak")
        Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                if(msg.getData().getInt(Const.EMAILRESULT) == 1){
                    Toast.makeText(SCOSHelper.this, "求助邮件已发送成功", Toast.LENGTH_LONG).show();
                }
                else if (msg.getData().getInt(Const.EMAILRESULT) == 0){
                    Toast.makeText(SCOSHelper.this, "发送失败", Toast.LENGTH_LONG).show();
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {

                MailSender mailSender = new MailSender(
                        "smtp.qq.com",     //服务器地址
                        "25",               //服务器端口
                        "1282122032@qq.com",   //你的邮箱
                        "nmdslmmrfcnzjcdf",           //邮箱密码或者授权码
                        "1282122032@qq.com",   //地址
                        "m17623087468@163.com",    //目的地址
                        true,               //是否身份认证
                        "邮件",              //邮件标题
                        msg,                //邮件正文
                        null);              //附件

                boolean isSuccess = false;
                if (type == 0) {
                    isSuccess = MailUtils.sendTextMail(mailSender);// 发送文体格式
                } else if (type == 1) {
                    isSuccess = MailUtils.sendHtmlMail(mailSender);// 发送html格式
                }

                Message message = handler.obtainMessage();
                Bundle bundle = new Bundle();
                if (isSuccess) {
                    Log.i("TAG--", "发送成功");
                    bundle.putInt(Const.EMAILRESULT,1);
                } else {
                    bundle.putInt(Const.EMAILRESULT,0);
                    Log.i("TAG--", "发送失败");
                }
                message.setData(bundle);
                handler.sendMessage(message);
            }
        }).start();
    }

    void initData() {
        int icon[] = {R.drawable.agreement, R.drawable.about, R.drawable.telphone,
                R.drawable.message,R.drawable.email};//图标
        String name[] = {"用户协议", "关于系统","人工帮助", "短信帮助","邮件帮助"};//图标下的文字
        dataList = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < icon.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("img", icon[i]);
            map.put("text", name[i]); //一个map中存放img与text两组键值对
            dataList.add(map); //存入各view信息
        }
    }
}
