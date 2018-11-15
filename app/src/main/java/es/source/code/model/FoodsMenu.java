package es.source.code.model;

import java.io.Serializable;
import java.util.ArrayList;

public class FoodsMenu implements Serializable{
    public static ArrayList<FoodsEntity> cold_foods_List ;   //定义食物列表
    public static ArrayList<FoodsEntity> hot_foods_List;
    public static ArrayList<FoodsEntity> sea_foods_List;
    public static ArrayList<FoodsEntity> drinks_List;

    public FoodsMenu(){
        cold_foods_List =new ArrayList<>();
        hot_foods_List =new ArrayList<>();
        sea_foods_List = new ArrayList<>();
        drinks_List = new ArrayList<>();

    }
    public void setColdFoodsList(ArrayList<FoodsEntity> cold_foods_List){

        this.cold_foods_List.addAll(cold_foods_List);

    }
    public void setHotFoodsList(ArrayList<FoodsEntity> hot_foods_List){

        this.hot_foods_List.addAll(hot_foods_List);

    }
    public void setSeaFoodsList(ArrayList<FoodsEntity> sea_foods_List){

        this.sea_foods_List.addAll(sea_foods_List);

    }
    public void setDrinks(ArrayList<FoodsEntity> drinks_List){

        this.drinks_List.addAll(drinks_List);

    }
    public ArrayList<FoodsEntity> getColdFoodsList(){
        return cold_foods_List;
    }

    public ArrayList<FoodsEntity> getHotFoodsList(){
        return hot_foods_List;
    }

    public ArrayList<FoodsEntity> getSeaFoodsList(){
        return sea_foods_List;
    }

    public ArrayList<FoodsEntity> getDrinksList(){
        return drinks_List;
    }
}

