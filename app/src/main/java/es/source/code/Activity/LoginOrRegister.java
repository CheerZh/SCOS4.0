package es.source.code.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonReader;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.source.code.R;
import es.source.code.Utils.Const;
import es.source.code.Utils.HttpUtil;
import es.source.code.Utils.UserLocalInfo;
import es.source.code.model.User;

public class LoginOrRegister extends AppCompatActivity {

    Map<String,String> user_info  = null;

    EditText username ;
    EditText password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginorregister);
        getSupportActionBar().setTitle(R.string.login_or_register);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        Button login = findViewById(R.id.login);
        Button register = findViewById(R.id.register);
        Button back = findViewById(R.id.back);

        user_info  = UserLocalInfo.getInfo(this);  //获取本地用户信息
        if(user_info .get(Const.USERNAME)!=null){
//            register.setVisibility(View.GONE);
            register.setEnabled(false);
            register.setBackgroundColor(Color.parseColor("#778899"));
            username.setText(user_info.get(Const.USERNAME));
        }
        else{
//            login.setVisibility(View.GONE);
            login.setEnabled(false);
            login.setBackgroundColor(Color.parseColor("#778899"));
        }

        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                String str_username = username.getText().toString();
                String str_password = password.getText().toString();
                if (checkInput(str_username,str_password)){

                    final ProgressDialog dialog = ProgressDialog.show(LoginOrRegister.this, "",
                            Const.login_msg, true, true);

                    class MyAsyncTask extends AsyncTask<Integer, Integer, String>
                    {
                        @Override
                        protected String doInBackground(Integer... params)
                        {
                            String resp =null;
                            int time = params[0];
                            try{
                                resp = toLink(str_username,str_password,"login");
//                                Thread.sleep(time);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            return resp;
                        }
                        @Override
                        protected void onPostExecute(String resp)
                        {
                            super.onPostExecute(resp);
                            dialog.dismiss();

                            int resultcode = -1;
                            try {
                                JsonReader reader = new JsonReader(new StringReader(resp));
                                reader.beginObject();
                                String tagName = reader.nextName();
                                if (tagName.equals("RESULTCODE")){
                                    resultcode = reader.nextInt();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }finally {
                                if(resultcode == -1){
                                    Toast.makeText(LoginOrRegister.this, "连接服务器出错", Toast.LENGTH_LONG).show();
                                }else if (resultcode == 0){
                                    Toast.makeText(LoginOrRegister.this, "用户名或密码错误", Toast.LENGTH_LONG).show();
                                }else{

                                    User loginUser = new User();
                                    loginUser.setter(str_username,str_password,true);
                                    //将成功登陆用户的信息存入SharedPreference
                                    UserLocalInfo.saveInfo(LoginOrRegister.this,str_username,str_password);
                                    UserLocalInfo.login_state = 1;  //登陆状态置1

                                    Intent intent = new Intent();
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable(Const.USER_KEY,loginUser);
                                    bundle.putString("sign", "LoginSuccess");
                                    intent.putExtras(bundle);
                                    intent.setClass(LoginOrRegister.this, MainScreen.class);
                                    LoginOrRegister.this.startActivity(intent);
                                }
                            }
                        }
                    }
                    MyAsyncTask mat = new MyAsyncTask();
                    mat.execute(200);
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                String str_username = username.getText().toString();
                String str_password = password.getText().toString();
                if (checkInput(str_username,str_password)){

                    final ProgressDialog dialog = ProgressDialog.show(LoginOrRegister.this, "",
                            Const.register_msg, true, true);



                    class MyAsyncTask extends AsyncTask<Integer, Integer, String> {
                        @Override
                        protected String doInBackground(Integer... params) {
                            int time = params[0];
                            String resp = null;
                            try {
                                resp = toLink(str_username, str_password, "register");
//                                Thread.sleep(time);
                                } catch (Exception e) {
                                e.printStackTrace();
                                }finally {
                                return resp;
                                }
                            }
                            @Override
                            protected void onPostExecute(String resp) {
                            super.onPostExecute(resp);
                            dialog.dismiss();
                            int resultcode = -1;
                            try {
                                JsonReader reader = new JsonReader(new StringReader(resp));
                                reader.beginObject();
                                String tagName = reader.nextName();
                                if (tagName.equals("RESULTCODE")) {
                                    resultcode = reader.nextInt();
                                    }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }finally {

                                if (resultcode == -1) {
                                    Toast.makeText(LoginOrRegister.this, "连接服务器出错", Toast.LENGTH_LONG).show();
                                }
                                else if (resultcode == 0) {
                                    Toast.makeText(LoginOrRegister.this, "用户名已被注册", Toast.LENGTH_LONG).show();
                                }
                                else {
                                    User loginUser = new User();
                                    loginUser.setter(str_username, str_password, false);
                                    //将已注册用户信息写入SharedReference
                                    UserLocalInfo.saveInfo(LoginOrRegister.this, str_username, str_password);
                                    UserLocalInfo.login_state = 1;  //登陆状态置1

                                    Intent intent = new Intent();
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable(Const.USER_KEY, loginUser);
                                    bundle.putString("sign", "RegisterSuccess");
                                    intent.putExtras(bundle);
                                    intent.setClass(LoginOrRegister.this, MainScreen.class);
                                    LoginOrRegister.this.startActivity(intent);
                                }
                            }
                        }
                    }
                    MyAsyncTask mat = new MyAsyncTask();
                    mat.execute(200);
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("sign", "return");
                intent.setClass(LoginOrRegister.this, MainScreen.class);
                LoginOrRegister.this.startActivity(intent);

                if(user_info != null){
                    UserLocalInfo.login_state = 0;  //登陆状态置0
                }
            }
        });
    }

    private String regex = "^[A-Za-z0-9]+$";
    private boolean checkInput(String str_username,String str_password ){

        if (check(str_username, regex) == false) {
            username.setFocusable(true);
            username.setFocusableInTouchMode(true);
            username.requestFocus();
            username.setError(Const.input_error);
            return false;
        }
        else if(check(str_password,regex)==false){
            password.setFocusable(true);
            password.setFocusableInTouchMode(true);
            password.requestFocus();
            password.setError(Const.input_error);
            return false;
        }
        else return true;
    }

    //检测是否满足正则表达式
    static boolean flag = false;
    public  boolean check(String str, String regex) {
        try {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(str);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    private String toLink(String username ,String password,String operation){

        Map<String, String> map = new HashMap<String, String>();
        map.put("username", username);
        map.put("password", password);
        map.put("operation",operation);
        String resp = null;

        try {
            resp = HttpUtil.postRequest(Const.baseUrl+"/LoginValidator", map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resp;
    }

}
