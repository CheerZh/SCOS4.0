package es.source.code.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

import es.source.code.R;
import es.source.code.Utils.Const;
import es.source.code.model.FoodsEntity;
import es.source.code.model.FoodsMenu;
import es.source.code.model.OrderItem;
import es.source.code.model.User;

public class FoodDetailed extends AppCompatActivity {

    float x1 = 0;
    float x2 = 0;
    float y1 = 0;
    float y2 = 0;

    User user = null;
    public OrderItem order_item;
    private ArrayList<FoodsEntity> foodsEntityList = new ArrayList<FoodsEntity>();
    FoodsEntity Food_data = null;
    FoodsMenu foods_menu;
    int index ;
    int tab_position;
    TextView food_name ;
    TextView food_price;
    ImageView food_image;
    Button order_button;
    EditText editText_remarks;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_detailed);
        getSupportActionBar().setTitle(R.string.food_details);
        food_image = findViewById(R.id.food_detailed_image);
        food_name = findViewById(R.id.food_name_detailed);
        food_price = findViewById(R.id.food_detailed_price);
        order_button = findViewById(R.id.Order_or_cancel);
        editText_remarks = findViewById(R.id.remarks);

        order_item = (OrderItem)getApplication();
        user = order_item.getUser();
        Intent intent = getIntent();
        Bundle bundle = new Bundle();
        bundle = (Bundle)intent.getExtras();
        tab_position = bundle.getInt(Const.TAB_LOCATION);            //tab页位置
        foods_menu = (FoodsMenu)bundle.getSerializable(Const.FOODS_MENU);  //菜单
        if(tab_position == 0 ){
            foodsEntityList = foods_menu.getColdFoodsList();
        }
        else if(tab_position == 1){
            foodsEntityList = foods_menu.getHotFoodsList();
        }
        else if(tab_position == 2){
            foodsEntityList = foods_menu.getSeaFoodsList();
        }
        else {
            foodsEntityList = foods_menu.getDrinksList();
        }

        index = (int)bundle.getInt(Const.FOOD_INDEX);
        try{
            Food_data = foodsEntityList.get(index);
            food_image.setImageResource(Food_data.getImg());
            food_name.setText(Food_data.getName());
            food_price.setText(Food_data.getPrice());
            if(Food_data.getStatus()== -1){
                order_button.setText(R.string.order);
            }
            else if(Food_data.getStatus() ==0){
                order_button.setText(R.string.cancel_order);
            }
            else {
                order_button.setText(R.string.be_confirmed);
            }
        }
        catch (Exception e){

            e.printStackTrace();
        }

        order_button.setOnClickListener(new View.OnClickListener() {    //对button的修改与对食物状态的修改分开
            @Override
            public void onClick(View v) {

                if (order_button.getText().equals("点菜")) {
                    Food_data.setOrdered_Num(Food_data.getOrdered_Num()+1);   //数量加1
                    Food_data.setStatus(0);
                    Food_data.setRemarks(editText_remarks.getText().toString());   //保存备注信息
                    order_item.getOrderedList().add(Food_data);    //向订单中添加菜品信息
                    Toast.makeText(FoodDetailed.this,R.string.order_success,Toast.LENGTH_SHORT).show();
                    order_button.setText(R.string.cancel_order);
                } else if (order_button.getText().equals("退点")) {
                    Food_data.setOrdered_Num(Food_data.getOrdered_Num()-1);   //数量减一
                    Food_data.setStatus(-1);
                    Food_data.setRemarks("");   //清空备注信息
                    order_item.getOrderedList().remove(Food_data);
                    Toast.makeText(FoodDetailed.this,R.string.cancel_success,Toast.LENGTH_SHORT).show();
                    order_button.setText(R.string.order);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        order_item = (OrderItem) getApplication();
        user = order_item.getUser();
    }

    @Override
    public void onBackPressed() {       //返回键事件
        Intent intent =new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("FROM","food_detailed");
        bundle.putInt(Const.TAB_LOCATION,tab_position);
        bundle.putSerializable(Const.FOODS_MENU,foods_menu);    //返回菜单
        intent.putExtras(bundle);
        setResult(2,intent);  //返回foodview
        finish();     //回传需要调用该方法
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
            if(x1 - x2 > 50) {    //向右滑动查看后面的菜品
//                if(index<goodsEntityList.size()-1){        //不可循环风格
//                    Food_data = goodsEntityList.get(++index);
//                    food_image.setImageResource(Food_data.getImg());
//                    food_name.setText(Food_data.getGoodsName());
//                    food_price.setText(Food_data.getGoodsPrice());
//                }
//                else {
//                    Toast.makeText(FoodDetailed.this, "已到最右", Toast.LENGTH_LONG).show();
//                }

                //可循环展示
                index=(index+1)%foodsEntityList.size();
                Food_data = foodsEntityList.get(index);
                editText_remarks.setText(Food_data.getRemarks());    //备注框信息
                food_image.setImageResource(Food_data.getImg());
                food_name.setText(Food_data.getName());
                food_price.setText(Food_data.getPrice());
                if(Food_data.getStatus()==-1){
                    order_button.setText(R.string.order);
                }
                else{
                    order_button.setText(R.string.cancel_order);
                }
            }
            else if(x2 - x1 > 50) {
                //可循环展示
                index=(index-1 + foodsEntityList.size()) % foodsEntityList.size();
                Food_data = foodsEntityList.get(index);
                editText_remarks.setText(Food_data.getRemarks());    //备注框信息
                food_image.setImageResource(Food_data.getImg());
                food_name.setText(Food_data.getName());
                food_price.setText(Food_data.getPrice());
                if(Food_data.getStatus()==-1){
                    order_button.setText(R.string.order);
                }
                else{
                    order_button.setText(R.string.cancel_order);
                }
            }
        }
        return super.onTouchEvent(event);

    }
}