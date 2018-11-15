package es.source.code.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashMap;
import java.util.Map;

public class UserLocalInfo {

    public static int login_state = 0;

    //保存用户信息
    public static boolean saveInfo(Context context, String name, String password) {
        SharedPreferences sp = context.getSharedPreferences(Const.USERINFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();   //向SharedPreferences中写数据
        edit.putString(Const.USERNAME, name);
        edit.putString(Const.PASSWORD, password);
        edit.commit();
        return true;
    }

    //读取用户信息
    public static Map<String,String> getInfo(Context context){
        SharedPreferences sp=context.getSharedPreferences(Const.USERINFO,Context.MODE_PRIVATE);
        String name = sp.getString(Const.USERNAME,null);
        String password = sp.getString(Const.PASSWORD,null);
        Map<String,String> userMap=new HashMap<String, String>();
        userMap.put(Const.USERNAME,name);
        userMap.put(Const.PASSWORD,password);
        return userMap;
    }

}
