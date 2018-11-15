package es.source.code.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Toast;

import es.source.code.R;


public class SCOSEntry extends AppCompatActivity {

    float x1 = 0;
    float x2 = 0;
    float y1 = 0;
    float y2 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry);
        ImageView logo = findViewById(R.id.logo);
        getSupportActionBar().hide();
    }

    @SuppressLint("NewApi")
    @Override
    protected void onStart() {
        super.onStart();
        //获取Actionbar
        Toast.makeText(this, "欢迎使用SCOS,向左滑动进入",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //继承了Activity的onTouchEvent方法，直接监听点击事件
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            //当手指按下的时候
            x1 = event.getX();
            y1 = event.getY();
        }
        if(event.getAction() == MotionEvent.ACTION_UP) {
            //当手指离开的时候
            x2 = event.getX();
            y2 = event.getY();
            if(x1 - x2 > 50) {
                Intent intent = new Intent();
//                intent.putExtra("one","FromEntry");
                //显式的Intend
                intent.setClass(SCOSEntry.this,MainScreen.class);
                SCOSEntry.this.startActivity(intent);
            }
            else if(x2 - x1 > 50) {
                Toast.makeText(SCOSEntry.this, "请向左滑动进入", Toast.LENGTH_LONG).show();
            }
        }
        return super.onTouchEvent(event);

    }
}
