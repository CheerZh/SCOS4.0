package es.source.code.Fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import es.source.code.R;
import es.source.code.Utils.Const;
import es.source.code.Activity.FoodOrderView;
import es.source.code.adapter.CollectRecycleAdapter;
import es.source.code.adapter.ConfirmedFoodRecycleAdapter;
import es.source.code.model.FoodsEntity;
import es.source.code.model.OrderItem;


public class ConfirmedFoodFragment extends Fragment {

    private View view;        //定义view用来设置fragment的layout
    public RecyclerView mCollectRecyclerView;         //定义RecyclerView
    OrderItem order_item ;
    Button mybutton;     //下单\结账按钮
    TextView tv_price;     //总价
    TextView tv_sum;    //数量
    int begin = 0;

    //定义以foodsentity实体类为对象的数据集合
    private ArrayList<FoodsEntity> foods_List = new ArrayList<>();   //定义食物列表
    //自定义recyclerveiw的适配器
    private ConfirmedFoodRecycleAdapter foodsRecyclerAdapter;

    private Bundle bundle = new Bundle();
    int mPage;

    public static ConfirmedFoodFragment newInstance(int page) {
        Bundle pages = new Bundle();
        pages.putInt(Const.PAGE_INFO, page);      //将常数写入一个单独的class
        ConfirmedFoodFragment fragment = new ConfirmedFoodFragment();
        fragment.setArguments(pages);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(Const.PAGE_INFO);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //获取fragment的layout
        view = inflater.inflate(R.layout.ordered_food_fragment, container, false);
        //对recycleview进行配置
        order_item = ((FoodOrderView)getActivity()).getOrderItem();    //这里传值不一样,已点菜列表
        initRecyclerView();

        mybutton = (Button) view.findViewById(R.id.payoff);
        tv_sum = (TextView)view.findViewById(R.id.sum_number);
        tv_price=(TextView)view.findViewById(R.id.total_price);

        double total_price_d = 0;
        int sum_number = 0;
        for(FoodsEntity foods: order_item.getConfirmedFoodList()){
            total_price_d += Double.parseDouble(foods.getPrice());
            sum_number++;
        }
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.CHINA);
        // 把转换后的货币String类型返回
        String total_price_s = format.format(total_price_d);
        final int credit = (int)(total_price_d/10);

        mybutton.setText("结账");
        tv_price.setText(total_price_s);
        tv_sum.setText(sum_number+"");

        mybutton.setOnClickListener(new View.OnClickListener(){      //结账
            public void onClick(View v){
                order_item.payoff();
                if(order_item.getUser().oldUser){
                    Toast.makeText(getActivity(), "您好，老顾客，\n本次您可享受 7 折优惠", Toast.LENGTH_LONG).show();
                }

                final ProgressDialog dialog = ProgressDialog.show(getActivity(), "",
                        "结算中，请稍等 …", true, true);


                class MyAsyncTask extends AsyncTask<Integer, Integer, Integer>
                {
                    @Override
                    protected void onPreExecute()
                    {
                        super.onPreExecute();
                    }
                    @Override
                    protected Integer doInBackground(Integer... params)
                    {
                        int time = params[0];
                        try{
                            Thread.sleep(time);

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        return 1;
                    }
                    @Override
                    protected void onProgressUpdate(Integer... values)
                    {
                        super.onProgressUpdate(values);
                    }
                    @Override
                    protected void onPostExecute(Integer result)
                    {
                        super.onPostExecute(result);
                        dialog.dismiss();
                        mybutton.setText("已结算");
                        mybutton.setEnabled(false);
                        Toast.makeText(getActivity(), "本次结算金额："+total_price_s+
                                "，\n积分增加"+credit +",\n欢迎您下次光临！", Toast.LENGTH_LONG).show();
                    }
                }
                MyAsyncTask mat = new MyAsyncTask();
                mat.execute(6000);
            }
        });

        return view;
    }


    /**
     *
     */
    private void initRecyclerView() {

        mCollectRecyclerView=(RecyclerView)view.findViewById(R.id.recycler_view_ordered);

//        if(mPage==0){   //已点
//            foods_List.addAll(order_item.getOrderedList());
//        }
        if(mPage == 1){
            foods_List.clear();
            foods_List.addAll(order_item.getConfirmedFoodList());
        }

        //创建adapter
        foodsRecyclerAdapter = new ConfirmedFoodRecycleAdapter(getActivity(), foods_List);
        //给RecyclerView设置adapter
        mCollectRecyclerView.setAdapter(foodsRecyclerAdapter);

        //设置layoutManager,可以设置显示效果，是线性布局、grid布局，还是瀑布流布局
        //参数是：上下文、列表方向（横向还是纵向）、是否倒叙
        mCollectRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        //设置item的分割线
        mCollectRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
        //RecyclerView中没有item的监听事件，需要自己在适配器中写一个监听事件的接口。参数根据自定义

        //实现CollectRecycleAdapter中的OnItemClickListener接口,实现对食物数组的操作
        foodsRecyclerAdapter.setOnItemClickListener(new CollectRecycleAdapter.OnItemClickListener() {

            @Override
            public void OnItemClick(View view, FoodsEntity food) {   //点击跳转到食物详情页

            }
        });
    }
}