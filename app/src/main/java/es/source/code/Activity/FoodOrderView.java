package es.source.code.Activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import es.source.code.Fragments.ConfirmedFoodFragment;
import es.source.code.Fragments.OrderedFoodFragment;
import es.source.code.R;
import es.source.code.adapter.MyFragmentPagerAdapter;
import es.source.code.model.OrderItem;
import es.source.code.model.User;

public class FoodOrderView extends AppCompatActivity {

    User user = null;
    ViewPager mViewPager;
    TabLayout mTabLayout;
    public static OrderItem order_item ;
//    public static ArrayList<FoodsEntity> OrderedFoodList = new ArrayList<FoodsEntity>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(R.string.check_order);
        setContentView(R.layout.food_order_view);
    }

    @Override
    public void onResume() {
        super.onResume();
        order_item = (OrderItem)getApplication();
        user = order_item.getUser();
        initViews();
        mTabLayout.getTabAt(1).select();
    }

    private void initViews() {

        String[] tab_Titles = new String[]{"未下单", "已下单"};

        Fragment [] foodsFragments = new Fragment[2];
        mViewPager = (ViewPager) findViewById(R.id.food_order_viewPager);
        mTabLayout = (TabLayout) findViewById(R.id.food_order_tabLayout);
        for (String tab : tab_Titles){
            mTabLayout.addTab(mTabLayout.newTab().setText(tab));
        }
        foodsFragments[0]= OrderedFoodFragment.newInstance(0);
        foodsFragments[1] = ConfirmedFoodFragment.newInstance(1);

        //使用适配器将ViewPager与Fragment绑定在一起
        MyFragmentPagerAdapter myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), tab_Titles,foodsFragments);
        mViewPager.setAdapter(myFragmentPagerAdapter);
        //将TabLayout与ViewPager绑定在一起
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
    }
    public OrderItem  getOrderItem(){        //向Fragment传值
        return order_item;
    }
    public void setOrderItem(OrderItem order_item){      //对oeder_item进行更新
        this.order_item = order_item;
    }

}
