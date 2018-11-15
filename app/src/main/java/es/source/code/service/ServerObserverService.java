package es.source.code.service;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import es.source.code.R;
import es.source.code.Utils.Const;
import es.source.code.model.FoodsEntity;
import es.source.code.model.FoodsMenu;


public class ServerObserverService extends Service {

    private FoodsMenuReceiver foodsMenuReceiver = null;

    private static final String TAG = "ServerObserverService";
    public static Handler cMessageHandler;
    public static FoodsMenu foods_menu = new FoodsMenu();
    public boolean isStop ;
    private Messenger mMessager = null;

    UpdateThread updateThread;

    public class MyBinder extends Binder {
        // 声明一个方法，getService。（提供给客户端调用）
        public ServerObserverService getService() {
            return ServerObserverService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();  //返回MyBinder Service对象
    }

    /**
     * 首次创建服务时，系统将调用此方法来执行一次性设置程序（在调用 onStartCommand() 或 onBind() 之前）。
     * 如果服务已在运行，则不会调用此方法。该方法只被调用一次
     */

    @SuppressLint("HandlerLeak")
    @Override
    public void onCreate() {

        Log.d(TAG, "onCreate: ");
        updateThread = new UpdateThread();

//        foodsMenuReceiver = new FoodsMenuReceiver();
//        IntentFilter filter=new IntentFilter();
//        filter.addAction("es.source.code.ServiceBroadcastReceiver");
//        this.registerReceiver(foodsMenuReceiver,filter);

        cMessageHandler = new Handler(Looper.getMainLooper()){

            @Override
            public void handleMessage(Message msg) {

                Log.d(TAG, "handleMessage: ");
                if(msg.what == 1){
                    updateThread.run();
                }
                else if (msg.what == 0){
                    //关闭模拟接收服务器传回菜品库存信息的多线程
                    cMessageHandler.removeMessages(1);
                    Log.d(TAG, "handleMessage: remove");
                    stopSelf(); //关闭Service
                }
            }
        };
    }

    /**
     * 每次通过startService()方法启动Service时都会被回调。
     * @param intent
     * @param flags
     * @param startId
     * @return
     */

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        if(mMessager == null){
            mMessager =(Messenger) intent.getExtras().get("messenger");
        }

//        String foods = intent.getExtras().getString("foods");

//        resolveFoods(foods);

        initFoods();

        Message msg = cMessageHandler.obtainMessage();
        msg.what = (int)intent.getExtras().get("msg");
        cMessageHandler.sendMessage(msg);

        Log.d(TAG, "onStartCommand: ");
        return Service.START_STICKY;    //1
}

    /**
     * 服务销毁时的回调
     */
    @Override
    public void onDestroy() {
        isStop = true;
//        this.unregisterReceiver(foodsMenuReceiver);
        Log.d(TAG, "onDestroy: !!");

        super.onDestroy();
    }

    private class UpdateThread extends Thread{
        
        public void run() {

                Log.d(TAG, "run: 服务中");
                updateFood(foods_menu.getHotFoodsList());
                updateFood(foods_menu.getColdFoodsList());
                updateFood(foods_menu.getSeaFoodsList());
                updateFood(foods_menu.getDrinksList());
                if(isAppInForeground(getApplicationContext())){    //SCOS进程处于运行状态
                    //向主线程发送message

                    Log.d(TAG, "run: 向主线程发送消息");

                    Message message = new Message();
                    message.what = 10;

                    String foods = MenuToJSON(foods_menu);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("foodsString",foods);
                    message.setData(bundle);
                    try {
                        mMessager.send(message);

//                        Intent intent=new Intent();
//                        Bundle bundle = new Bundle();
//                        bundle.putSerializable(Const.FOODS_MENU,foods_menu);
//                        intent.putExtra("bundle",bundle);
//                        intent.setAction("es.source.code.newFoodsReceiver");
//                        sendBroadcast(intent);


                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    //向工作线程发送message
                    Message msg = cMessageHandler.obtainMessage();
                    msg.what = 1;
                    cMessageHandler.sendMessage(msg);
                }

            try {
                sleep(3000);
                Log.d(TAG, "run: sleep");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 判断app是否处于运行状态
     * @param context
     * @return
     */

    public static boolean isAppInForeground(Context context) {

        Log.d(TAG, "isAppInForeground: ");
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                return appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
            }
        }
        return false;
    }

    @SuppressLint("HandlerLeak")

    private void updateFood(ArrayList<FoodsEntity> foodsEntityArrayList){

        int food_stock;
        for (FoodsEntity food : foodsEntityArrayList) {
            food_stock = food.stocks - (int) (Math.random() * 10) + 4;
            if (food_stock < 0) {
                food.setStocks(0);
            } else {
                food.setStocks(food_stock);
            }
        }
        Log.d(TAG, "updateFood: ");
    }

    public class FoodsMenuReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle=intent.getExtras().getBundle("bundle");

            String test = (String)intent.getExtras().get("test");
            Log.d(TAG, "onReceive: ");

        }
    }


    private String MenuToJSON(FoodsMenu foods){

        ArrayList<ArrayList<FoodsEntity>> listOfList= new ArrayList<>();
        listOfList.add(foods.getColdFoodsList());
        listOfList.add(foods.getHotFoodsList());
        listOfList.add(foods.getSeaFoodsList());
        listOfList.add(foods.getDrinksList());

        JSONObject foodsJson = new JSONObject();

        int j = 0;
        for(ArrayList<FoodsEntity> list : listOfList){

            JSONArray jsonArray = new JSONArray();

            int i = 0;

            for(FoodsEntity food : list) {
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("stocks",food.stocks);
//                jsonObj.put("price", food);
//                jsonObj.put("category", food);
                jsonArray.add(i++,jsonObj);
            }
            foodsJson.element("jsonArray"+j, jsonArray);
            j++;
        }
        return foodsJson.toString();
    }


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

    

}
