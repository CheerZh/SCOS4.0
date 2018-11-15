package es.source.code.model;

import android.app.Application;
import java.io.Serializable;
import java.util.ArrayList;

public class OrderItem extends Application implements Serializable {

    ArrayList<FoodsEntity> OrderedFoodsList;
    ArrayList<FoodsEntity> ConfirmedFoodsList;
    User user;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public OrderItem(){
        OrderedFoodsList = new ArrayList<FoodsEntity>();
        ConfirmedFoodsList = new ArrayList<FoodsEntity>();
        user = null;
    }

    public void steUser(User user){
        this.user = user;
    }

    public void add_Orderded(FoodsEntity food){    //点菜
        OrderedFoodsList.add(food);
    }
    public void remove_Orderded(FoodsEntity food){  //退点
        OrderedFoodsList.remove(food);
    }

    public User getUser(){
        return user;
    }
    public ArrayList<FoodsEntity> getOrderedList(){   //得到已点未下单列表
        return OrderedFoodsList;
    }
    public ArrayList<FoodsEntity> getConfirmedFoodList(){   //得到已下单列表
        return ConfirmedFoodsList;
    }
    public void confirm(){    //确认下单
        for(FoodsEntity food : OrderedFoodsList){
            food.setStatus(1);
        }
        ConfirmedFoodsList.addAll(OrderedFoodsList);
        OrderedFoodsList.clear();
    }
    public void payoff(){

        for(FoodsEntity food : ConfirmedFoodsList){
            food.setOrdered_Num(0);
            food.setStatus(-1);
        }
        ConfirmedFoodsList.clear();
    }
}
