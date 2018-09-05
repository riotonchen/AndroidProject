package com.example.admin.project;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

public class LoginActivity extends AppCompatActivity {
    private Button button15,btnLogin,btnRegister,button14,friend;
    private EditText txtPwdConfirm,txtEmail,txtPwd;
    boolean check = true;
    View view3;
    Intent intent,intent2;
    private static String showMsg = "\n";
    private final LoginHandler loginHandler = new LoginHandler(LoginActivity.this);
    private final RegisterHandler registerHandler = new RegisterHandler(LoginActivity.this);
    private final GetHandler getHandler = new GetHandler(LoginActivity.this);
    private final RefreshHandler refreshHandler = new RefreshHandler(LoginActivity.this);

    private class MyMessages {
        public static final int Error = 0;
        public static final int Connecting = 1;
        public static final int Progressing = 2;
        public static final int Disconnect = 3;
    }

    private class Path {  //注意路徑有無斜線(endpoint)
        public static final String api_token_jwtauth = "https://www.177together.com/api-token-jwtauth";
        public static final String api_token_refresh = "https://www.177together.com/api-token-refresh/";
        public static final String member = "https://www.177together.com/api/member/";
        public static final String friendShip = "https://www.177together.com/api/friendship/";
    }

    //https://stackoverflow.com/questions/3806051/accessing-sharedpreferences-through-static-methods
    private static SharedPreferences myGetSharedPreferences(Context context) {
        return context.getSharedPreferences("jwt_token", MODE_PRIVATE);
    }

    //https://www.androiddesignpatterns.com/2013/01/inner-class-handler-memory-leak.html
    //http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2014/1106/1922.html
    //https://blog.csdn.net/Mr_Leixiansheng/article/details/67636817
    private static class LoginHandler extends Handler {
        private final WeakReference<LoginActivity> mActivity;    //弱引用

        private LoginHandler(LoginActivity activity) {
            mActivity = new WeakReference<LoginActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            LoginActivity activity = mActivity.get();    //獲取弱引用的對象

            if (activity != null) {
                try {
                    switch (msg.what) {
                        case MyMessages.Connecting:
                            showMsg = "\n[Connecting]>>>";
                            break;
                        case MyMessages.Progressing:
                            showMsg = "[Progressing]>>>";

                            SharedPreferences myPref = myGetSharedPreferences(activity.getApplicationContext());    //靜態方法
                            String strPayload = myPref.getString("PAYLOAD", "");//讀取已儲存的SharedPref
                            JSONObject jsonPayload = StringToJSON(strPayload);  //轉成JSON

                            Bundle bundle = msg.getData();
                            JSONObject jsonObj = BundleToJson(bundle.getBundle("token_Bundle"));    //轉成JSON
                            showMsg +=
                                    "\n[ResponseCode]：" + String.valueOf(jsonObj.getInt("responseCode")) +
                                            "\n[Token]：" + jsonObj.getString("token") +
                                            "\n[PAYLOAD.user_id]：" + jsonPayload.getInt("user_id") +
                                            "\n[PAYLOAD.username]：" + jsonPayload.getString("username") +
                                            "\n[PAYLOAD.exp]：" + jsonPayload.getInt("exp") +
                                            "\n[PAYLOAD.orig_iat]：" + jsonPayload.getInt("orig_iat");

                            //activity.txvStatus.setText("狀態：已登入 ; user_id = " + String.valueOf(jsonPayload.getInt("user_id")));
                            //activity.txvStatus.setTextColor(Color.BLUE);

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
                            sdf.setTimeZone(TimeZone.getTimeZone("Asia/Taipei"));
                            Date expiredDate = new Date(jsonPayload.getInt("exp") * 1000L); //Unix timestamp 轉 Date
                            Date orig_iatDate = new Date(jsonPayload.getInt("orig_iat") * 1000L);
                            //activity.txvExpired.setText("Token Expired：" + sdf.format(expiredDate));
                            //activity.txvOrig_iat.setText("Token Issued At：" + sdf.format(orig_iatDate));

                            //activity.btnLogin.setEnabled(false); //已登入不允許再登入及註冊
                            //activity.btnRegister.setEnabled(false);
                            //activity.txtEmail.setEnabled(false);
                            //activity.txtPwd.setEnabled(false);
                            //activity.txtPwdConfirm.setEnabled(false);


                            Toast.makeText(activity, "登入成功！", Toast.LENGTH_SHORT).show();
                            break;
                        case MyMessages.Disconnect:
                            showMsg = "\n[Disconnect]\n";
                            break;
                        case MyMessages.Error:
                            bundle = msg.getData();
                            String errorMsg = bundle.getString("errorMsg", "");
                            Toast.makeText(activity, errorMsg, Toast.LENGTH_LONG).show();
                            break;
                        default:
                            break;
                    }
                    //activity.txvRecord.append(showMsg);
                    //super.handleMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class RegisterHandler extends Handler {
        private final WeakReference<LoginActivity> mActivity;    //弱引用

        private RegisterHandler(LoginActivity activity) {
            mActivity = new WeakReference<LoginActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            LoginActivity activity = mActivity.get();    //獲取弱引用的對象
            if (activity != null) {
                try {
                    switch (msg.what) {
                        case MyMessages.Connecting:
                            showMsg = "\n[Connecting]>>>";
                            break;
                        case MyMessages.Progressing:
                            showMsg = "[Progressing]>>>";
                            SharedPreferences myPref = myGetSharedPreferences(activity.getApplicationContext());
                            String strPayload = myPref.getString("PAYLOAD", "");//讀取已儲存的SharedPref
                            JSONObject jsonPayload = StringToJSON(strPayload);  //轉成JSON

                            Bundle bundle = msg.getData();
                            JSONObject jsonObj = BundleToJson(bundle.getBundle("member_Bundle"));    //轉成JSON
                            showMsg +=
                                    "\n[ResponseCode]：" + String.valueOf(jsonObj.getInt("responseCode")) +
                                            "\n[Token]：" + jsonObj.getString("token") +
                                            "\n[PAYLOAD.exp]：" + jsonPayload.getInt("exp") +
                                            "\n[PAYLOAD.orig_iat]：" + jsonPayload.getInt("orig_iat");
                            showMsg += "\n{\n\"id\" : " + jsonObj.getInt("id") + ",\n" +
                                    "\"toid\" : \"" + jsonObj.getString("toid") + "\",\n" +
                                    "\"account\" : \"" + jsonObj.getString("account") + "\",\n" +
                                    "\"identifier\" : \"" + jsonObj.getString("identifier") + "\",\n" +
                                    "\"membertype\" : " + jsonObj.getInt("membertype") + ",\n" +
                                    "\"name\" : \"" + jsonObj.getString("name") + "\",\n" +
                                    "\"nickname\" : \"" + jsonObj.getString("nickname") + "\",\n" +
                                    "\"password\" : \"" + jsonObj.getString("password") + "\",\n" +
                                    "\"localpicture\" : \"" + jsonObj.getString("localpicture") + "\",\n" +
                                    "\"dbpicture\" : \"" + jsonObj.getString("dbpicture") + "\",\n}";
                            //activity.txvStatus.setText("狀態：已登入 ; user_id = " + String.valueOf(jsonPayload.getInt("user_id")));
                            //activity.txvStatus.setTextColor(Color.BLUE);

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
                            sdf.setTimeZone(TimeZone.getTimeZone("Asia/Taipei"));
                            Date expiredDate = new Date(jsonPayload.getInt("exp") * 1000L); //Unix timestamp 轉 Date
                            Date orig_iatDate = new Date(jsonPayload.getInt("orig_iat") * 1000L);
                            //activity.txvExpired.setText("Token Expired：" + sdf.format(expiredDate));
                            //activity.txvOrig_iat.setText("Token Issued At：" + sdf.format(orig_iatDate));
                            //activity.btnLogin.setEnabled(false); //已登入不允許再登入及註冊
                            //activity.btnRegister.setEnabled(false);
                            //activity.txtEmail.setEnabled(false);
                            //activity.txtPwd.setEnabled(false);
                            //activity.txtPwdConfirm.setEnabled(false);

                            Toast.makeText(activity, "註冊成功！", Toast.LENGTH_SHORT).show();
                            break;
                        case MyMessages.Disconnect:
                            showMsg = "\n[Disconnect]\n";
                            break;
                        case MyMessages.Error:
                            bundle = msg.getData();
                            String errorMsg = bundle.getString("errorMsg", "");
                            Toast.makeText(activity, errorMsg, Toast.LENGTH_LONG).show();
                            break;
                        default:
                            break;
                    }
                    //activity.txvRecord.append(showMsg);
                    //super.handleMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class GetHandler extends Handler {
        private final WeakReference<LoginActivity> mActivity;    //弱引用

        private GetHandler(LoginActivity activity) {
            mActivity = new WeakReference<LoginActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            LoginActivity activity = mActivity.get();    //獲取弱引用的對象
            if (activity != null) {
                try {
                    switch (msg.what) {
                        case MyMessages.Connecting:
                            showMsg = "\n[Connecting]>>>";
                            break;
                        case MyMessages.Progressing:
                            showMsg = "[Progressing]>>>";
                            Bundle bundle = msg.getData();
                            JSONArray jsonArray = new JSONArray(bundle.getString("get_jsonArrayString"));
                            JSONObject jsonObject = jsonArray.getJSONObject(jsonArray.length() - 1);    //最後一個為responseCode
                            showMsg +=
                                    "\n[ResponseCode]：" + String.valueOf(jsonObject.getInt("responseCode")) +
                                            "\n" + jsonArray.toString();  //這邊直接輸出字串，未處理

                            Toast.makeText(activity, "讀取成功！", Toast.LENGTH_SHORT).show();
                            break;
                        case MyMessages.Disconnect:
                            showMsg = "\n[Disconnect]\n";
                            break;
                        case MyMessages.Error:
                            bundle = msg.getData();
                            String errorMsg = bundle.getString("errorMsg", "");
                            Toast.makeText(activity, errorMsg, Toast.LENGTH_LONG).show();
                            break;
                        default:
                            break;
                    }
                    //activity.txvRecord.append(showMsg);
                    //super.handleMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class RefreshHandler extends Handler {
        private final WeakReference<LoginActivity> mActivity;    //弱引用

        private RefreshHandler(LoginActivity activity) {
            mActivity = new WeakReference<LoginActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            LoginActivity activity = mActivity.get();    //獲取弱引用的對象
            if (activity != null) {
                try {
                    switch (msg.what) {
                        case MyMessages.Connecting:
                            showMsg = "\n[Connecting]>>>";
                            break;
                        case MyMessages.Progressing:
                            showMsg = "[Progressing]>>>";
                            SharedPreferences myPref = myGetSharedPreferences(activity.getApplicationContext());
                            String strPayload = myPref.getString("PAYLOAD", "");//讀取已儲存的SharedPref
                            JSONObject jsonPayload = StringToJSON(strPayload);  //轉成JSON

                            Bundle bundle = msg.getData();
                            JSONObject jsonObj = BundleToJson(bundle.getBundle("token_Bundle"));
                            showMsg +=
                                    "\n[ResponseCode]：" + String.valueOf(jsonObj.getInt("responseCode")) +
                                            "\n[Token]：" + jsonObj.getString("token") +
                                            "\n[PAYLOAD.exp]：" + jsonPayload.getInt("exp") +
                                            "\n[PAYLOAD.orig_iat]：" + jsonPayload.getInt("orig_iat");
                            //activity.txvStatus.setText("狀態：已登入 ; user_id = " + String.valueOf(jsonPayload.getInt("user_id")));
                            //activity.txvStatus.setTextColor(Color.BLUE);

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
                            sdf.setTimeZone(TimeZone.getTimeZone("Asia/Taipei"));
                            Date expiredDate = new Date(jsonPayload.getInt("exp") * 1000L); //Unix timestamp 轉 Date
                            Date orig_iatDate = new Date(jsonPayload.getInt("orig_iat") * 1000L);
                            //activity.txvExpired.setText("Token Expired：" + sdf.format(expiredDate));
                            //activity.txvOrig_iat.setText("Token Issued At：" + sdf.format(orig_iatDate));
                            Toast.makeText(activity, "Token更新成功！", Toast.LENGTH_SHORT).show();
                            break;
                        case MyMessages.Disconnect:
                            showMsg = "\n[Disconnect]\n";
                            break;
                        case MyMessages.Error:
                            bundle = msg.getData();
                            String errorMsg = bundle.getString("errorMsg", "");
                            Toast.makeText(activity, errorMsg, Toast.LENGTH_LONG).show();
                            break;
                        default:
                            break;
                    }
                    //activity.txvRecord.append(showMsg);
                    //super.handleMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);   //進入含有EditText的Activity時，不自動彈出虛擬鍵盤

        button15 = (Button) findViewById(R.id.button15);
        button14 = (Button) findViewById(R.id.button14);
        view3 = (View) findViewById(R.id.view3);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        friend= (Button) findViewById(R.id.friend);
        txtPwdConfirm = (EditText) findViewById(R.id.txtPwdConfirm);
        txtPwd = (EditText) findViewById(R.id.txtPwd);
        txtEmail = (EditText) findViewById(R.id.txtEmail);

        //View header = navigationView.getHeaderView(0);


        intent = new Intent(this, NewMainActivity.class);
        intent2 = new Intent(this, SearchFriendActivity.class);

        friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent2); //登入成功導向首頁
            }
        });
        //登入
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            loginHandler.sendEmptyMessage(MyMessages.Connecting);

                            Map<String, String> params = new HashMap<>();
                            //必填這三個欄位
                            params.put("username", txtEmail.getText().toString() + ";" + "2");  //xxx@xxx;2
                            params.put("password", txtPwd.getText().toString());    //MySQL392
                            params.put("membertype_id", "2");

                            Message message = new Message();
                            message.what = MyMessages.Progressing;
                            Bundle bundle = new Bundle();
                            JSONObject jsonObj = HttpUtils.GetToken(Path.api_token_jwtauth, params);
                            if(jsonObj.getInt("responseCode")!=HttpURLConnection.HTTP_OK){
                                message.what = MyMessages.Error;
                                bundle.putString("errorMsg", jsonObj.getString("non_field_errors"));    //"non_field_errors"為jwt預設"key"名稱
                                message.setData(bundle);
                                loginHandler.sendMessage(message);

                            }else{
                                intent.putExtra("username","吳彥霆");
                                startActivity(intent); //登入成功導向首頁
                                DealToken(jsonObj.getString("token"));  //儲存Token
                                bundle.putBundle("token_Bundle", JsonToBundle(jsonObj));    //轉成Bundle
                                message.setData(bundle);
                                loginHandler.sendMessage(message);
                            }

                            loginHandler.sendEmptyMessage(MyMessages.Disconnect);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        //註冊
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (!(txtPwd.getText().toString().equals(txtPwdConfirm.getText().toString()))) {
                                Message message = new Message();
                                message.what = MyMessages.Error;
                                Bundle bundle = new Bundle();
                                bundle.putString("errorMsg", "兩次密碼不一致，請重新輸入！");
                                message.setData(bundle);
                                registerHandler.sendMessage(message);
                            } else {
                                registerHandler.sendEmptyMessage(MyMessages.Connecting);
                                Map<String, String> params = new HashMap<>();
                                params.put("account", txtEmail.getText().toString());    //必填
                                params.put("identifier", null);
                                params.put("membertype", "2");  //必填
                                params.put("name", null);
                                params.put("nickname", null);
                                params.put("password", txtPwd.getText().toString()); //必填
                                params.put("localpicture", "images\\usr\\pic001.jpg");
                                params.put("dbpicture", "images\\usr\\pic020.jpg");

                                Message message = new Message();
                                message.what = MyMessages.Progressing;
                                Bundle bundle = new Bundle();
                                JSONObject jsonObj = HttpUtils.Register(Path.member, params);
                                if (jsonObj.getInt("responseCode") != HttpURLConnection.HTTP_CREATED) { //檢查responseCode
                                    message.what = MyMessages.Error;
                                    bundle.putString("errorMsg", jsonObj.getString("error_msg"));
                                    message.setData(bundle);
                                    registerHandler.sendMessage(message);

                                } else {    //註冊成功
                                    params = new HashMap<>();
                                    params.put("username", txtEmail.getText().toString() + ";" + "2");//帳號+會員類型 為唯一
                                    params.put("password", txtPwd.getText().toString());
                                    params.put("membertype_id", "2");   //本站會員
                                    JSONObject tokenJsonObj = HttpUtils.GetToken(Path.api_token_jwtauth, params);   //取得Token以利之後存取其他資源
                                    DealToken(tokenJsonObj.getString("token")); //儲存Token
                                    jsonObj.put("token", tokenJsonObj.getString("token"));   //加入註冊時回傳的json
                                    bundle.putBundle("member_Bundle", JsonToBundle(jsonObj));    //轉成Bundle
                                    message.setData(bundle);
                                    registerHandler.sendMessage(message);
                                }
                                registerHandler.sendEmptyMessage(MyMessages.Disconnect);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }
        });

    }
    @Override
    protected void onDestroy() {    //清除所有Handler動作
        loginHandler.removeCallbacksAndMessages(null);
        registerHandler.removeCallbacksAndMessages(null);
        getHandler.removeCallbacksAndMessages(null);
        refreshHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private static Bundle JsonToBundle(JSONObject jsonObj) throws JSONException {
        Bundle bundle = new Bundle();
        Iterator iter = jsonObj.keys();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            String value = jsonObj.getString(key);
            bundle.putString(key, value);
        }
        return bundle;
    }

    private static JSONObject BundleToJson(Bundle bundle) throws JSONException {
        JSONObject jsonObj = new JSONObject();
        Set<String> keys = bundle.keySet();
        for (String key : keys) {
            jsonObj.put(key, JSONObject.wrap(bundle.get(key)));
        }
        return jsonObj;
    }

    private static JSONObject StringToJSON(String jsonString) throws JSONException {
        return new JSONObject(jsonString);
    }

    private void DealToken(String token) {
        SharedPreferences myPref = getSharedPreferences("jwt_token", MODE_PRIVATE);
        SharedPreferences.Editor editor = myPref.edit();
        editor.clear();  //舊的先清除
        editor.apply();
        String[] encodeArray = token.split("\\.");  //注意特殊字元
        String[] decodeArray = new String[2];   //[HEADER,PAYLOAD,VERIFY SIGNATURE] (VERIFY SIGNATURE 不處理)
        for (int i = 0; i < encodeArray.length - 1; i++) {
            byte[] data = Base64.decode(encodeArray[i], Base64.DEFAULT);
            decodeArray[i] = new String(data, StandardCharsets.UTF_8);
        }
        editor.putString("token", token)
                .putString("PAYLOAD", decodeArray[1])   //只需要PAYLOAD
                .apply();   //apply()為非同步寫入
        //http://android-deve.blogspot.com/2012/11/sharedpreferences-keyvalue.html
    }
    public void onClick(View view){
        if(check == true){
            txtPwdConfirm.setVisibility(View.VISIBLE);
            button14.setVisibility(View.INVISIBLE);
            btnRegister.setVisibility(View.VISIBLE);
            btnLogin.setVisibility(View.INVISIBLE);
            button15.setText("已經有帳號?");
            view3.setVisibility(View.INVISIBLE);
            check = false;
        }else{
            txtPwdConfirm.setVisibility(View.INVISIBLE);
            button14.setVisibility(View.VISIBLE);
            view3.setVisibility(View.VISIBLE);
            btnRegister.setVisibility(View.INVISIBLE);
            btnLogin.setVisibility(View.VISIBLE);
            button15.setText("還沒有帳號");
            check = true;
        }
    }





   
}
