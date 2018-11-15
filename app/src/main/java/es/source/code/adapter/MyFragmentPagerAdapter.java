package es.source.code.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;


public class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {   //修改继承的类

    private static final String TAG = "MyFragmentPagerAdapter";
    private String[] tabNames;//tab选项名字
    private Fragment [] foodsFragments ;
    public MyFragmentPagerAdapter(FragmentManager fm, String[] tabNames,Fragment [] foodsFragments) {
        super(fm);
        this.tabNames = tabNames;
        this.foodsFragments = foodsFragments;
    }
    @Override
    public Fragment getItem(int position) {  //position会出现错乱问题！???

        return foodsFragments[position];
    }
    @Override
    public int getCount() {
        return tabNames.length;
    }

    //ViewPager与TabLayout绑定后，这里获取到PageTitle就是Tab的Text
    @Override
    public CharSequence getPageTitle(int position) {
        return tabNames[position];
    }


// 强制刷新
    private int mChildCount = 0;

    @Override
    public void notifyDataSetChanged() {
        mChildCount = getCount();
        Log.d(TAG, "notifyDataSetChanged: ");
        super.notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object)   {
        if ( mChildCount > 0) {
            mChildCount --;
            return POSITION_NONE;
        }
        return super.getItemPosition(object);
    }


    //修改两个方法解决位置错乱问题
//    @Override
//    public boolean isViewFromObject(View view, Object obj) {
//        return view == ((Fragment) obj).getView();
//    }
//
//    @Override
//    public void destroyItem(ViewGroup container, int position, Object object) {
//        Fragment fragment = ((Fragment) object);
//    }
//
}
