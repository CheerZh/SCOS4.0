package es.source.code.model;

import java.io.Serializable;

public class FoodsEntity implements Serializable {
    public int image;//图片地址
    public String foodName;//菜品名称
    public String foodPrice;//菜品价格
    public String category = null; //类别
    public int order_status = -1; //订单状态-1未点, 0已点未下单, 1已下单
    public String remarks = null; //菜品备注信息
    public int ordered_num = 0 ;   //点菜数量
    public  int stocks = 0;   //库存量

    public FoodsEntity() {
    }
    public FoodsEntity(int img, String foodName, String foodPrice) {
        image = img;
        this.foodName = foodName;
        this.foodPrice = foodPrice;
    }
    public FoodsEntity(int img, String foodName,String foodPrice,String category,int order_status){
        image = img;
        this.foodName = foodName;
        this.foodPrice = foodPrice;
        this.category = category;
        this.order_status = order_status;
    }
    public int getImg() {
        return image;
    }

    public void setImg(int img) {
        image = img;
    }

    public String getName() {
        return foodName;
    }

    public void setName(String foodsName) {
        this.foodName = foodsName;
    }

    public String getPrice() {
        return foodPrice;
    }

    public void setPrice(String goodsPrice) {
        this.foodPrice = goodsPrice;
    }

    public void setStatus(int order_status){
        this.order_status=order_status;
    }

    public int getStatus(){
        return order_status;
    }

    public void setCategory(String category){
        this.category = category;
    }

    public String getCategory(){
        return category;
    }

    public void setRemarks(String remarks){
        this.remarks = remarks;
    }

    public String getRemarks(){
        return remarks;
    }

    public int getOrdered_Num() {
        return ordered_num;
    }

    public void setOrdered_Num(int num) {
        ordered_num = num;
    }

    public int getStocks(){
        return stocks;
    }
    public void setStocks(int stocks){

        this.stocks = stocks;

    }

    @Override
    public String toString() {
        return "FoodsEntity{" +
                "imgPath='" + image + '\'' +
                ", foodName='" + foodName + '\'' +
                ", foodPrice='" + foodPrice + '\'' +
                '}';
    }
}
