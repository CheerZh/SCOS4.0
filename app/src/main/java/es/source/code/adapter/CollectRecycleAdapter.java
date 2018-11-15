package es.source.code.adapter;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

import es.source.code.Activity.FoodView;
import es.source.code.R;
import es.source.code.model.FoodsEntity;
import es.source.code.model.OrderItem;

public class CollectRecycleAdapter extends RecyclerView.Adapter<CollectRecycleAdapter.myViewHodler> {
    private Context context;
    private static ArrayList<FoodsEntity> foodsEntityList;
    private static final String TAG = "CollectRecycleAdapter";

    //创建构造函数
    public CollectRecycleAdapter(Context context, ArrayList<FoodsEntity> foodsEntityList) {   //传入的goodsEntityList正确否??
        //将传递过来的数据，赋值给本地变量
        Log.d(TAG, "CollectRecycleAdapter: ");
        this.context = context;//上下文
        CollectRecycleAdapter.foodsEntityList = foodsEntityList;//实体类数据ArrayList
    }

        /**
         * 创建viewhodler，相当于listview中getview中的创建view和viewhodler
         *
         * @param parent
         * @param viewType
         * @return
         */

        @Override
        public myViewHodler onCreateViewHolder (ViewGroup parent,int viewType){  //Holder
            //创建自定义布局
            View itemView = View.inflate(context, R.layout.list_item, null);
            return new myViewHodler(itemView);
        }

        /**
         * 绑定数据，数据与view绑定
         *
         * @param holder
         * @param position
         */
        @Override
        public void onBindViewHolder (myViewHodler holder,int position){
            //根据点击位置绑定数据
            FoodsEntity food = foodsEntityList.get(position);

            holder.mFoodsImg.setImageResource(food.image);
            holder.mFoodsName.setText(food.foodName);//获取实体类中的name字段并设置
            holder.mFoodsPrice.setText(food.foodPrice);//获取实体类中的price字段并设置
            holder.index =foodsEntityList.indexOf(food);
            holder.stocks = food.stocks;
            holder.mFoodsStocks.setText(food.stocks+"");   //显示存量
            if(food.getStatus() == -1){
               holder.mbutton.setText(R.string.order);
            }
            else{
                holder.mbutton.setText(R.string.cancel_order);
            }
            if(food.stocks ==0){
                holder.mbutton.setText(R.string.sell_out);
            }
        }

        /**
         * 得到总条数
         *
         * @return
         */
        @Override
        public int getItemCount () {
            return foodsEntityList.size();
        }//

        //自定义viewhodler
        public class myViewHodler extends RecyclerView.ViewHolder {
            private ImageView mFoodsImg;
            private TextView mFoodsName;
            private TextView mFoodsPrice;
            private TextView mFoodsStocks;
            private Button mbutton;
            private int index;   //菜品在列表中的位置
            private int stocks;   //存量

            public myViewHodler(View itemView) {
                super(itemView);
                mFoodsImg = (ImageView) itemView.findViewById(R.id.food_image);
                mFoodsName = (TextView) itemView.findViewById(R.id.food_name);
                mFoodsPrice = (TextView) itemView.findViewById(R.id.food_price);
                mFoodsStocks = (TextView) itemView.findViewById(R.id.foods_stocks);
                mbutton = (Button) itemView.findViewById(R.id.order_button);

                //点击事件放在adapter中使用，也可以写个接口在activity中调用
                //方法一：在adapter中设置点击事件
                mbutton.setOnClickListener(new View.OnClickListener() {    //对button的修改与对食物状态的修改分开
                    @Override
                    public void onClick(View v) {

                        if (mbutton.getText().equals("点菜")) {
                            foodsEntityList.get(index).setOrdered_Num(foodsEntityList.get(index).getOrdered_Num()+1);  //点菜数量加一
                            foodsEntityList.get(index).setStatus(0);
                            FoodView.order_item.add_Orderded(foodsEntityList.get(index));   //向已点列表中添加菜品
                            Toast.makeText(context,R.string.order_success,Toast.LENGTH_SHORT).show();
                            mbutton.setText(R.string.cancel_order);
                        } else if (mbutton.getText().equals("退点")) {
                            foodsEntityList.get(index).setOrdered_Num(foodsEntityList.get(index).getOrdered_Num()-1);   //点菜数量减一
                            foodsEntityList.get(index).setStatus(-1);
                            FoodView.order_item.remove_Orderded(foodsEntityList.get(index));  //从已点列表中移除菜品
                            Toast.makeText(context,R.string.cancel_success,Toast.LENGTH_SHORT).show();
                            mbutton.setText(R.string.order);
                        }
                    }
                });

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //可以选择直接在本位置直接写业务处理
                        //Toast.makeText(context,"点击了xxx",Toast.LENGTH_SHORT).show();
                        //此处回传点击监听事件
                        if (onItemClickListener != null) {
                            onItemClickListener.OnItemClick(v, foodsEntityList.get(getAdapterPosition()));
                        }
                    }
                });

            }
        }

        /**
         * 设置item的监听事件的接口
         */
        public interface OnItemClickListener {
            /**
             * 接口中的点击每一项的实现方法，参数自己定义
             *
             * @param view 点击的item的视图
             * @param data 点击的item的数据
             */
            public void OnItemClick(View view, FoodsEntity data);
        }

        //需要外部访问，所以需要设置set方法，方便调用
        private OnItemClickListener onItemClickListener;

        public void setOnItemClickListener (OnItemClickListener onItemClickListener){
            this.onItemClickListener = onItemClickListener;
        }

        public void refreshView(ArrayList<FoodsEntity> foodsEntityList){

            foodsEntityList.clear();
            foodsEntityList.addAll(foodsEntityList);
            notifyDataSetChanged();
        }
}
