package es.source.code.Activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import es.source.code.Fragments.FoodsFragment;
import es.source.code.Fragments.FoodsFragment1;
import es.source.code.R;
import es.source.code.adapter.MyFragmentPagerAdapter;
import es.source.code.Utils.Const;
import es.source.code.model.FoodsEntity;
import es.source.code.model.FoodsMenu;
import es.source.code.model.OrderItem;
import es.source.code.model.User;
import es.source.code.service.ServerObserverService;



public class FoodView extends AppCompatActivity {


//    private NewFoodsReceiver newFoodsReceiver = null;
    private static final String TAG = "FoodView";
    public static FoodsMenu foods_menu = new FoodsMenu();
    public  static OrderItem order_item ;    //全局
    User user =null;
    ViewPager mViewPager;
    TabLayout mTabLayout;
    MyFragmentPagerAdapter myFragmentPagerAdapter;
    int tab_position = 0;       //记录当前tab页位置
    public static Handler sMessageHandler;
    private ServerObserverService myService ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_view);
        getSupportActionBar().setTitle(R.string.order);
        order_item = (OrderItem)getApplication();
        initFoods();

//        //注册广播接收器
//        newFoodsReceiver = new NewFoodsReceiver();
//        IntentFilter filter=new IntentFilter();
//        filter.addAction("es.source.code.newFoodsReceiver");
//        FoodView.this.registerReceiver(newFoodsReceiver,filter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //获取Actionbar
        android.support.v7.app.ActionBar bar = this.getSupportActionBar();
    }

    @SuppressLint("NewApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {        //资源文件添加菜单
        new MenuInflater(this).inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getTitle().equals("已点菜品")){
            Intent intent = new Intent();
            Bundle bundle = new Bundle();    //消息包
            bundle.putString("FROM","food_view");
            intent.putExtras(bundle);
            intent.setClass(FoodView.this,FoodOrderView.class);
            FoodView.this.startActivityForResult(intent,1);  //要求回传
        }
        else if(item.getTitle().equals("查看订单")){
            Intent intent = new Intent();
            Bundle bundle = new Bundle();    //消息包
            bundle.putString("FROM","food_view");
            intent.putExtras(bundle);
            intent.setClass(FoodView.this,FoodOrderView.class);
            FoodView.this.startActivity(intent);
        }

        else if(item.getTitle().equals("启动实时更新")){
            item.setTitle("停止实时更新");

            sMessageHandler = new Handler(Looper.getMainLooper()){

                @Override
                public void handleMessage(Message msg) {
                    if(msg.what == 10){

                        Log.d(TAG, "handleMessage: 更新视图");
                        //更新视图

                        String foodsString =(String)msg.getData().getSerializable("foodsString");
                        ResolveJson(foodsString);
                        UpdateViewThread updateViewThread = new UpdateViewThread();
                        updateViewThread.run();

                    }
                }
            };


            Intent intent = new Intent();
//            Bundle bundle = new Bundle();
//            bundle.putSerializable(Const.FOODS_MENU,foods_menu);
//            Log.d(TAG, "onOptionsItemSelected: "+foods_menu.getHotFoodsList().isEmpty()+"发送时");
//            intent.putExtra("bundle",bundle);

            intent.putExtra("messenger", new Messenger(sMessageHandler));
            intent.putExtra("msg",1);
//            intent.putExtra("foods",foods);

            intent.setClass(FoodView.this,ServerObserverService.class);
            startService(intent);
//            bindService(intent,conn,BIND_AUTO_CREATE);


//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
//                    String test = "aaa垃圾安卓，毁我青春bbb";
//
//                    //发送广播
//                    Intent intent=new Intent();
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable(Const.FOODS_MENU,foods_menu);
//                    intent.putExtra("bundle",bundle);
//                    intent.putExtra("test",test);
//                    intent.setAction("es.source.code.ServiceBroadcastReceiver");
//                    sendBroadcast(intent);
//
//
//                }
//            }).start();

        }
        else if(item.getTitle().equals("停止实时更新")){


            Intent intent = new Intent();
            intent.putExtra("msg",0);

            intent.setClass(FoodView.this,ServerObserverService.class);
            startService(intent);

//            stopService(new Intent(FoodView.this,ServerObserverService.class));

            item.setTitle("启动实时更新");
        }
        else{
            Toast.makeText(this, "抱歉，" + item.getTitle() + "功能尚未完善",
                    Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        order_item = (OrderItem)getApplication();
        user = order_item.getUser();
        initViews();
        mTabLayout.getTabAt(tab_position).select();
    }

    /**
     * 获取FoodDetaild的返回值，更新视图
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode==2){
            foods_menu = (FoodsMenu)data.getExtras().getSerializable(Const.FOODS_MENU);
            tab_position = data.getExtras().getInt(Const.TAB_LOCATION);
        }
//        initViews();
    }

    /**
     * 绑定service在onStop()方法中解除绑定
     */
//    @Override
//    protected void onStop(){
////        unbindService(conn);   //解除绑定Service
//        super.onStop();
//    }

    /**
     * ViewPager&Tablayout
     */
    private void initViews() {

        String[] tab_Titles = new String[]{"冷菜" , "热菜","海鲜", "酒水"};
        Fragment [] foodsFragments = new Fragment[4];
        mViewPager= (ViewPager) findViewById(R.id.viewPager);
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);

        for (String tab : tab_Titles){
            mTabLayout.addTab(mTabLayout.newTab().setText(tab));
        }

        for(int i=0;i<4;i++){
            if(i==0||i==2){
                foodsFragments[i]= FoodsFragment.newInstance(i);
            }
            else if(i==1||i==3){
                foodsFragments[i]= FoodsFragment1.newInstance(i);
            }
        }
        //使用适配器将ViewPager与Fragment绑定在一起
        myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(),tab_Titles,foodsFragments);
        mViewPager.setAdapter(myFragmentPagerAdapter);
        //将TabLayout与ViewPager绑定在一起
        mTabLayout.setupWithViewPager(mViewPager);    //Tablayout与ViewPager的组合
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        //设置Tablayout点击事件

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab_position = tab.getPosition();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


//    public class NewFoodsReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Bundle bundle=intent.getExtras().getBundle("bundle");
//            foods_menu = (FoodsMenu) bundle.getSerializable(Const.FOODS_MENU);
//        }
//    }

    private void ResolveJson (String foods){

        JSONObject jsonObject = JSONObject.fromObject(foods);

        JSONArray jsonArray = jsonObject.getJSONArray("jsonArray"+0);
        for(int i = foods_menu.getColdFoodsList().size();i>0;i--){
            JSONObject food = jsonArray.getJSONObject(i-1);
            foods_menu.getColdFoodsList().get(i-1).setStocks((int)food.get("stocks"));
        }
        jsonArray = jsonObject.getJSONArray("jsonArray"+1);
        for(int i = foods_menu.getColdFoodsList().size();i>0;i--){
            JSONObject food = jsonArray.getJSONObject(i-1);
            foods_menu.getHotFoodsList().get(i-1).setStocks((int)food.get("stocks"));
        }
        jsonArray = jsonObject.getJSONArray("jsonArray"+2);
        for(int i = foods_menu.getColdFoodsList().size();i>0;i--){
            JSONObject food = jsonArray.getJSONObject(i-1);
            foods_menu.getSeaFoodsList().get(i-1).setStocks((int)food.get("stocks"));
        }
        jsonArray = jsonObject.getJSONArray("jsonArray"+3);
        for(int i = foods_menu.getColdFoodsList().size();i>0;i--){
            JSONObject food = jsonArray.getJSONObject(i-1);
            foods_menu.getDrinksList().get(i-1).setStocks((int)food.get("stocks"));
        }

        Log.d(TAG, "ResolveJson: ");

    }



    /**
     * 初始化菜单
     */
    private void initFoods(){

        ArrayList<FoodsEntity> cold_foods_List = new ArrayList<FoodsEntity>();
        ArrayList<FoodsEntity> hot_foods_List = new ArrayList<FoodsEntity>();
        ArrayList<FoodsEntity> sea_foods_List = new ArrayList<FoodsEntity>();
        ArrayList<FoodsEntity> drinks_List = new ArrayList<FoodsEntity>();

        int [] cold_food_img ={R.drawable.liangbanhuanggua1,R.drawable.liangpi1,R.drawable.liangbanhaibaicai1,
                R.drawable.qiaobanyecai1,R.drawable.shousiji1,R.drawable.hongyoumuer1,R.drawable.jiangxiangbanya1};
        String [] cold_foods={"凉拌黄瓜","凉皮","凉拌海白菜","巧拌野菜","手撕鸡","红油木耳","酱香板鸭"};
        String [] cold_foods_prices ={"10.00","15.00","15.00","20.00","25.00","18.00","24.00"};

        for (int i=0;i<cold_food_img.length;i++){
            FoodsEntity foodsEntity=new FoodsEntity();
            foodsEntity.setImg(cold_food_img[i]);
            foodsEntity.setName(cold_foods[i]);
            foodsEntity.setPrice(cold_foods_prices[i]);
            foodsEntity.setCategory("COLD_FOODS");
            foodsEntity.setStatus(-1);   //未点未下单
            foodsEntity.setStocks((int)(Math.random()*50)+50);   //设置库存量
            cold_foods_List.add(foodsEntity);
        }

        int [] hot_food_img ={R.drawable.qingjiaorousi1,R.drawable.yuxiangqiezi1,R.drawable.gongbaojiding1,R.drawable.suancaiyu1,
                R.drawable.huiguorou1,R.drawable.shuizuroupian1,R.drawable.jiaoyanliji1};
        String[] hot_foods = {"青椒肉丝", "鱼香茄子", "宫保鸡丁", "酸菜鱼", "回锅肉", "水煮肉片","椒盐里脊"};
        String[] hot_foods_prices = {"20.00", "15.00", "25.00", "30.00", "18.00", "35.00","25.00"};
        for (int i=0;i<hot_food_img.length;i++){
            FoodsEntity goodsEntity=new FoodsEntity();
            goodsEntity.setImg(hot_food_img[i]);
            goodsEntity.setName(hot_foods[i]);
            goodsEntity.setPrice(hot_foods_prices[i]);
            goodsEntity.setCategory("HOT_FOODS");
            goodsEntity.setStatus(-1);   //未点未下单
            goodsEntity.setStocks((int)(Math.random()*50)+50);   //设置库存量
            hot_foods_List.add(goodsEntity);
        }

        int []sea_food_img={R.drawable.youmendazhaxie1,R.drawable.baozhikouliaoshen1,R.drawable.chishengpinpan1,
                R.drawable.shengbanyouyu1,R.drawable.pijiuxiaolongxia1,R.drawable.kaoshenghao1,R.drawable.chaohuajia1};
        String[] sea_foods = {"油闷大闸蟹","鲍汁扣辽参", "刺身拼盘","生拌鱿鱼", "啤酒小龙虾", "烤生蚝", "炒花甲" };
        String[] sea_foods_prices = {"45.00", "65.00", "60.00", "50.00", "40.00", "35.00","35.00"};
        for (int i=0;i<sea_food_img.length;i++){
            FoodsEntity foodsEntity=new FoodsEntity();
            foodsEntity.setImg(sea_food_img[i]);
            foodsEntity.setName(sea_foods[i]);
            foodsEntity.setPrice(sea_foods_prices[i]);
            foodsEntity.setCategory("SEA_FOODS");
            foodsEntity.setStatus(-1);   //未点未下单
            foodsEntity.setStocks((int)(Math.random()*50)+50);   //设置库存量
            sea_foods_List.add(foodsEntity);
        }

        int [] drinks_img ={R.drawable.baiwei1,R.drawable.qingdaochunsheng1,R.drawable.yenai1,R.drawable.wanglaoji1,
                R.drawable.luzhoulaojiao1,R.drawable.jackdaniels1,R.drawable.jacobscreek1};
        String[] drinks = {"百威", "青岛纯生", "椰奶", "王老吉", "泸州老窖","杰克丹尼", "杰卡斯梅洛干红"};
        String[] drinks_price = {"8.00", "8.00", "12.00", "8.00","158.00", "588.00", "218.00"};
        for (int i=0;i<drinks_img.length;i++){
            FoodsEntity foodsEntity=new FoodsEntity();
            foodsEntity.setImg(drinks_img[i]);
            foodsEntity.setName(drinks[i]);
            foodsEntity.setPrice(drinks_price[i]);
            foodsEntity.setCategory("DRINKS");
            foodsEntity.setStatus(-1);   //未点未下单
            foodsEntity.setStocks((int)(Math.random()*50)+50);   //设置库存量
            drinks_List.add(foodsEntity);
        }

        foods_menu.setColdFoodsList(cold_foods_List);
        foods_menu.setHotFoodsList(hot_foods_List);
        foods_menu.setSeaFoodsList(sea_foods_List);
        foods_menu.setDrinks(drinks_List);
    }

    /**
     *
     */
    private class UpdateViewThread extends Thread{

        @Override
        public void run() {

            Log.d(TAG, "handleMessage:接收到service发回的消息 ");
//            myFragmentPagerAdapter.notifyDataSetChanged();
////            FoodsFragment.foodsRecyclerAdapter.notifyDataSetChanged();
////            FoodsFragment1.foodsRecyclerAdapter.notifyDataSetChanged();

            ArrayList<FoodsEntity> newfoods = null;

            if(tab_position == 0 || tab_position == 2) {

                if(tab_position ==0) {
                    newfoods = foods_menu.getColdFoodsList();
                }
                else if(tab_position == 2) {
                    newfoods = foods_menu.getSeaFoodsList();
                }
                FoodsFragment.notifyDataSetChanged(newfoods);
            }
            else {
                if(tab_position ==1) {
                    newfoods = foods_menu.getHotFoodsList();
                }
                else if(tab_position == 3) {
                    newfoods = foods_menu.getDrinksList();
                }
                FoodsFragment1.notifyDataSetChanged(newfoods);
            }
        }
    }



    /**
     * 绑定service通过connect获取service
     */
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name,  IBinder service) {

            try{
                myService = ((ServerObserverService.MyBinder)service).getService();//获取后台Service信息
            }catch (Exception e){
                e.printStackTrace();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    public static FoodsMenu getFoodsMenu(){        //与Fragment传值

        return foods_menu;
    }       //传给fragment


    public void setFoodsMenu(FoodsMenu foods_menu){
        this.foods_menu =foods_menu;
    }

    public static OrderItem  getOrderItem(){        //向Fragment传值
        return order_item;
    }
    public void setOrderItem(OrderItem order_item){      //对oeder_item进行更新
        this.order_item = order_item;
    }

}
