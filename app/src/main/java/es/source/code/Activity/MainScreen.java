package es.source.code.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import es.source.code.Utils.UserLocalInfo;
import es.source.code.model.OrderItem;
import es.source.code.model.User;

public class MainScreen extends AppCompatActivity {

    User user = null;
    public OrderItem order_item =null;

    private GridView gridView;
    private List<Map<String, Object>> dataList;
    private SimpleAdapter adapter;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainscreen_gridview);
        getSupportActionBar().setTitle(R.string.homepage);
        //使用application全局变量来维持订单
        order_item =(OrderItem)getApplication();

        if(UserLocalInfo.login_state == 0){
            hideData();
        }else{
            initData();
        }
        Intent intent =getIntent();
//        try {
//            String first = intent.getStringExtra("one");
//            if(first.equals("FromEntry") ){
//                hideData(); }
//            else initData();
//        }catch(Exception e){ e.printStackTrace(); }
        try {
            String sign = intent.getStringExtra("sign");
            if (sign.equals("LoginSuccess")){
                initData();
                user = (User)getIntent().getSerializableExtra(Const.USER_KEY);
                order_item.steUser(user);     //将用户信息填入订单中
            }
            else if (sign.equals("RegisterSuccess")){
                Toast.makeText(MainScreen.this,"欢迎您成为新用户！",Toast.LENGTH_LONG).show();
                initData();
                user = (User)getIntent().getSerializableExtra(Const.USER_KEY);
                order_item.steUser(user);
            }
            else{ hideData(); }
        }catch(Exception e){
            e.printStackTrace();
        }

        View view= this.getLayoutInflater().inflate((R.layout.mainscreen_gridview), null);
        gridView = (GridView) findViewById(R.id.gridview);
        String[] from={"img","text"};//键
        int[] to={R.id.img,R.id.text};//值
        adapter = new SimpleAdapter(this, dataList, R.layout.gridview_item, from, to);
        gridView.setAdapter(adapter);

        //第一个参数：上下文对象
        //第二个参数：数据源是含有Map的一个集合
        //第三个参数：每一个item的布局文件
        //第四个参数：new String[]{}数组，数组的里面的每一项要与第二个参数中的存入map集合的的key值一样，一一对应
        //第五个参数：new int[]{}数组，数组里面的第三个参数中的item里面的控件id。

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                //登录注册按钮
                if(dataList.get(arg2).get("text")=="登录/注册"){
                    Intent intent = new Intent();
                    intent.setClass(MainScreen.this,LoginOrRegister.class);
                    MainScreen.this.startActivity(intent);
                }
                //点菜按钮
                else if(dataList.get(arg2).get("text")=="点菜"){
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();    //消息包
                    bundle.putString("FROM","main_screen");
                    intent.putExtras(bundle);
                    intent.setClass(MainScreen.this,FoodView.class);
                    MainScreen.this.startActivity(intent);

                }
                //查看订单按钮
                else if(dataList.get(arg2).get("text")=="查看订单"){
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("FROM","main_screen");
                    intent.putExtras(bundle);
                    intent.setClass(MainScreen.this,FoodOrderView.class);
                    MainScreen.this.startActivity(intent);
                }
                else if(dataList.get(arg2).get("text")=="系统帮助"){
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
//                    bundle.putString("FROM","main_screen");
                    intent.putExtras(bundle);
                    intent.setClass(MainScreen.this,SCOSHelper.class);
                    MainScreen.this.startActivity(intent);
                }
            }
        });

}


    void initData() {
        int icno[] = {R.drawable.order1, R.drawable.check1, R.drawable.sign1,
                R.drawable.help1};//图标
        String name[] = {"点菜", "查看订单", "登录/注册", "系统帮助"};//图标下的文字
        dataList = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < icno.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("img", icno[i]);
            map.put("text", name[i]); //一个map中存放img与text两组键值对
            dataList.add(map); //存入各view信息
        }
    }

    void hideData() {
        int icno[] = { R.drawable.sign1, R.drawable.help1};
        String name[] = { "登录/注册", "系统帮助"};
        dataList = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < icno.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("img", icno[i]);
            map.put("text", name[i]);
            dataList.add(map);
        }
    }

}
