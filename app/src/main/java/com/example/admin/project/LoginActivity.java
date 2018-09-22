package com.example.admin.project;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.facebook.CallbackManager;
import com.facebook.FacebookActivity;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG="LoginActivity";
    private Button button15,btnLogin,btnRegister,button14,fblogin;
    private EditText txtPwdConfirm,txtEmail,txtPwd;
    boolean check = true;
    View view3;
    Intent intent;
    private static String email,name,id;
    private final LoginHandler loginHandler = new LoginHandler(LoginActivity.this);
    private final RegisterHandler registerHandler = new RegisterHandler(LoginActivity.this);
    private final GetHandler getHandler = new GetHandler(LoginActivity.this);

    private LoginButton loginButton;
    // FB
    private LoginManager loginManager;
    private CallbackManager callbackManager;

    private class MyMessages {
        public static final int Error = 0;
        public static final int Connecting = 1;
        public static final int Progressing = 2;
        public static final int Disconnect = 3;
    }

    public class Path {  //注意路徑有無斜線(endpoint)
        public static final String api_token_jwtauth = "https://www.177together.com/api-token-jwtauth";
//        public static final String api_token_refresh = "https://www.177together.com/api-token-refresh/";
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
                        case MyMessages.Progressing:
                            Toast.makeText(activity, "登入成功！", Toast.LENGTH_SHORT).show();
                            break;
                        case MyMessages.Error:
//                            Bundle bundle = msg.getData();
//                            String errorMsg = bundle.getString("errorMsg", "");
//                            Toast.makeText(activity, errorMsg, Toast.LENGTH_LONG).show();
                            Toast.makeText(activity, "E-Mail或密碼錯誤，請重新輸入！", Toast.LENGTH_LONG).show();
                            break;
                        default:
                            break;
                    }
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
                        case MyMessages.Progressing:
                            Toast.makeText(activity, "註冊成功！", Toast.LENGTH_SHORT).show();
                            break;
                        case MyMessages.Error:
                            Bundle bundle = msg.getData();
                            String errorMsg = bundle.getString("errorMsg", "");
                            Toast.makeText(activity, errorMsg, Toast.LENGTH_LONG).show();
                            break;
                        default:
                            break;
                    }
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
                        case MyMessages.Progressing:

                            Toast.makeText(activity, "讀取成功！", Toast.LENGTH_SHORT).show();
                            break;
                        case MyMessages.Error:
                            Bundle bundle = msg.getData();
                            String errorMsg = bundle.getString("errorMsg", "");
                            Toast.makeText(activity, errorMsg, Toast.LENGTH_LONG).show();
                            break;
                        default:
                            break;
                    }
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
        fblogin= (Button) findViewById(R.id.fblogin);
        txtPwdConfirm = (EditText) findViewById(R.id.txtPwdConfirm);
        txtPwd = (EditText) findViewById(R.id.txtPwd);
        txtEmail = (EditText) findViewById(R.id.txtEmail);

        if (!HttpUtils.IsInternetAvailable(getApplicationContext())) { //檢查網路是否連接
            Toast.makeText(this, "網路未連接，請檢察網路狀態！", Toast.LENGTH_LONG).show();
        }

        // init facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        // init LoginManager & CallbackManager
        loginManager = LoginManager.getInstance();
        callbackManager = CallbackManager.Factory.create();

        //loginButton = (LoginButton) findViewById(R.id.login_button);
        //loginButton.setReadPermissions("email");

        /*// method_1.判斷fb是否登入過
        if (Profile.getCurrentProfile() != null) {
            Profile profile = Profile.getCurrentProfile();
            // 取得用戶大頭照
            Uri userPhoto = profile.getProfilePictureUri(300, 300);
            String id = profile.getId();
            String name = profile.getName();
            Log.d(TAG, "Facebook userPhoto: " + userPhoto);
            Log.d(TAG, "Facebook id: " + id);
            Log.d(TAG, "Facebook name: " + name);
        }*/

        intent = new Intent(this, NewMainActivity.class);
            //自動登入
            SharedPreferences myPref = getSharedPreferences("jwt_token", MODE_PRIVATE);  //此處使用預設的非靜態方法
            if (myPref.getString("token", "").equals("")) {
                //未登入繼續登入
            } else {
                try {
                    String strPayload = myPref.getString("PAYLOAD", "");
                    JSONObject jsonPayload = StringToJSON(strPayload);  //轉成JSON
                    long exp = Long.valueOf(jsonPayload.getString("exp"));
                    long now = System.currentTimeMillis() / 1000L;  //系統時間
                    if (exp <= now) {
                        //逾期再次登入
                    } else {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
                        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Taipei"));
                        Toast.makeText(this, "登入成功！", Toast.LENGTH_SHORT).show();

                        intent.putExtra("username", jsonPayload.getString("name"));
                        startActivity(intent); //登入成功導向首頁

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }




        fblogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!HttpUtils.IsInternetAvailable(getApplicationContext())) { //檢查網路是否連接
                    Toast.makeText(LoginActivity.this, "網路未連接，請檢察網路狀態！", Toast.LENGTH_LONG).show();
                }else{
                    loginFB();
                }

            }
        });



        //登入
        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!HttpUtils.IsInternetAvailable(getApplicationContext())) { //檢查網路是否連接
                    Toast.makeText(LoginActivity.this, "網路未連接，請檢察網路狀態！", Toast.LENGTH_LONG).show();
                    return;
                }
                if(txtEmail.getText().toString().equals("") || txtPwd.getText().toString().equals("")){
                    Toast.makeText(LoginActivity.this, "請輸入E-Mail及密碼!", Toast.LENGTH_LONG).show();
                    return;
                }
                if(!android.util.Patterns.EMAIL_ADDRESS.matcher(txtEmail.getText().toString()).matches()) {
                    Toast.makeText(LoginActivity.this, "Email格式不正確", Toast.LENGTH_LONG).show();
                    return;
                }

                final ProgressDialog progress = new ProgressDialog(LoginActivity.this);
                progress.setMessage("登入中");
                progress.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
                progress.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Map<String, String> params = new HashMap<>();
                            //必填這三個欄位
                            params.put("username", txtEmail.getText().toString() + ";" + "2");  //xxx@xxx;2
                            params.put("password", txtPwd.getText().toString());
                            params.put("membertype_id", "2");

                            Message message = new Message();
                            message.what = MyMessages.Progressing;
                            Bundle bundle = new Bundle();
                            JSONObject jsonObj = HttpUtils.GetToken(Path.api_token_jwtauth, params);
                            if(jsonObj.getInt("responseCode")!=HttpURLConnection.HTTP_OK){
                                message.what = MyMessages.Error;
//                                bundle.putString("errorMsg", jsonObj.getString("non_field_errors"));    //"non_field_errors"為jwt預設"key"名稱
//                                message.setData(bundle);
                                loginHandler.sendMessage(message);

                            }else{
                                DealToken(jsonObj.getString("token"));  //儲存Token
                                bundle.putBundle("token_Bundle", JsonToBundle(jsonObj));    //轉成Bundle
                                message.setData(bundle);
                                loginHandler.sendMessage(message);
                                SharedPreferences myPref = getSharedPreferences("jwt_token", MODE_PRIVATE);  //此處使用預設的非靜態方法
                                String strPayload = myPref.getString("PAYLOAD", "");//讀取已儲存的SharedPref
                                JSONObject jsonPayload = StringToJSON(strPayload);  //轉成JSON
                                intent.putExtra("username",jsonPayload.getString("name"));

                                startActivity(intent); //登入成功導向首頁

                            }
                            progress.dismiss();
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
                if (!HttpUtils.IsInternetAvailable(getApplicationContext())) { //檢查網路是否連接
                    Toast.makeText(LoginActivity.this, "網路未連接，請檢察網路狀態！", Toast.LENGTH_LONG).show();
                    return;
                }else if(txtEmail.getText().toString().equals("") || txtPwd.getText().toString().equals("") || txtPwdConfirm.getText().toString().equals("")){
                    Toast.makeText(LoginActivity.this, "請輸入E-Mail、密碼及確認密碼！", Toast.LENGTH_LONG).show();
                    return;
                }else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(txtEmail.getText().toString()).matches()){
                    Toast.makeText(LoginActivity.this, "Email格式不正確", Toast.LENGTH_LONG).show();
                }else if(txtPwd.getText().toString().length()<8){
                    Toast.makeText(LoginActivity.this, "密碼長度不得小於8碼", Toast.LENGTH_LONG).show();
                }else if(!(txtPwd.getText().toString().equals(txtPwdConfirm.getText().toString()))){
                    Toast.makeText(LoginActivity.this, "兩次密碼不一致，請重新輸入！", Toast.LENGTH_LONG).show();
                }else {

                    final ProgressDialog progress = new ProgressDialog(LoginActivity.this);
                    progress.setMessage("註冊中");
                    progress.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
                    progress.show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
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
                                    SharedPreferences myPref = getSharedPreferences("jwt_token", MODE_PRIVATE);  //此處使用預設的非靜態方法
                                    String strPayload = myPref.getString("PAYLOAD", "");//讀取已儲存的SharedPref
                                    JSONObject jsonPayload = StringToJSON(strPayload);  //轉成JSON
                                    intent.putExtra("username",jsonPayload.getString("name"));
                                    startActivity(intent); //註冊成功導向首頁

                                }
                                progress.dismiss();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        });

    }
    @Override
    protected void onDestroy() {    //清除所有Handler動作
        loginHandler.removeCallbacksAndMessages(null);
        registerHandler.removeCallbacksAndMessages(null);
        getHandler.removeCallbacksAndMessages(null);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    private void loginFB() {

        // 設定FB login的顯示方式 ; 預設是：NATIVE_WITH_FALLBACK
        /**
         * 1. NATIVE_WITH_FALLBACK
         * 2. NATIVE_ONLY
         * 3. KATANA_ONLY
         * 4. WEB_ONLY
         * 5. WEB_VIEW_ONLY
         * 6. DEVICE_AUTH
         */
        loginManager.setLoginBehavior(LoginBehavior.NATIVE_WITH_FALLBACK);
        // 設定要跟用戶取得的權限，以下3個是基本可以取得，不需要經過FB的審核
        List<String> permissions = new ArrayList<>();
        permissions.add("public_profile");
        permissions.add("email");
        // 設定要讀取的權限
        loginManager.logInWithReadPermissions(this, permissions);
        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                // fb登入成功
                // 透過GraphRequest來取得用戶的Facebook資訊
                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            if (response.getConnection().getResponseCode() == 200) {
                                id = object.getString("id");
                                name = object.getString("name");
                                email = object.getString("email");
                                //-----------登入api開始---------------
                                final ProgressDialog progress = new ProgressDialog(LoginActivity.this);
                                progress.setMessage("Facebook登入中");
                                progress.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
                                progress.show();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Map<String, String> params = new HashMap<>();
                                            //必填這三個欄位

                                            params.put("username", email + ";" + "3");  //xxx@xxx;2
                                            params.put("password", id);    //MySQL392
                                            params.put("membertype_id", "3");

                                            Message message = new Message();
                                            message.what = MyMessages.Progressing;
                                            Bundle bundle = new Bundle();
                                            JSONObject jsonObj = HttpUtils.GetToken(Path.api_token_jwtauth, params);
                                            if(jsonObj.getInt("responseCode")!=HttpURLConnection.HTTP_OK){
//                                                message.what = MyMessages.Error;
//                                                bundle.putString("errorMsg", jsonObj.getString("non_field_errors"));    //"non_field_errors"為jwt預設"key"名稱
//                                                message.setData(bundle);
//                                                loginHandler.sendMessage(message);

                                                //註冊api開始------------
                                                params = new HashMap<>();
                                                params.put("account", email);    //必填
                                                params.put("identifier", id);
                                                params.put("membertype", "3");  //必填
                                                params.put("name", name);
                                                //params.put("nickname", name);
                                                params.put("password", id); //必填
                                                //params.put("localpicture", "images\\usr\\pic001.jpg");
                                                //params.put("dbpicture", "images\\usr\\pic020.jpg");

                                                message = new Message();
                                                message.what = MyMessages.Progressing;
                                                bundle = new Bundle();
                                                jsonObj = HttpUtils.Register(Path.member, params);
                                               if (jsonObj.getInt("responseCode") != HttpURLConnection.HTTP_CREATED) { //檢查responseCode
                                                    message.what = MyMessages.Error;
                                                    bundle.putString("errorMsg", jsonObj.getString("error_msg"));
                                                    message.setData(bundle);
                                                    registerHandler.sendMessage(message);
                                                } else {    //註冊成功
                                                    params = new HashMap<>();
                                                    params.put("username", email + ";" + "3");//帳號+會員類型 為唯一
                                                    params.put("password", id);
                                                    params.put("membertype_id", "3");   //FB會員
                                                    JSONObject tokenJsonObj = HttpUtils.GetToken(Path.api_token_jwtauth, params);   //取得Token以利之後存取其他資源
                                                    DealToken(tokenJsonObj.getString("token")); //儲存Token
                                                    jsonObj.put("token", tokenJsonObj.getString("token"));   //加入註冊時回傳的json
                                                    bundle.putBundle("member_Bundle", JsonToBundle(jsonObj));    //轉成Bundle
                                                    message.setData(bundle);
                                                    registerHandler.sendMessage(message);
                                                    intent.putExtra("username",name);
                                                    startActivity(intent); //登入成功導向首頁
                                                }

                                            }else{
                                                //登入成功

                                                DealToken(jsonObj.getString("token"));  //儲存Token
                                                bundle.putBundle("token_Bundle", JsonToBundle(jsonObj));    //轉成Bundle
                                                message.setData(bundle);
                                                loginHandler.sendMessage(message);
                                                SharedPreferences myPref = getSharedPreferences("jwt_token", MODE_PRIVATE);  //此處使用預設的非靜態方法
                                                String strPayload = myPref.getString("PAYLOAD", "");//讀取已儲存的SharedPref
                                                JSONObject jsonPayload = StringToJSON(strPayload);  //轉成JSON
                                                intent.putExtra("username",jsonPayload.getString("name"));
                                                startActivity(intent); //登入成功導向首頁
                                            }
                                            progress.dismiss();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();

                                // 此時如果登入成功，就可以順便取得用戶大頭照
                                Profile profile = Profile.getCurrentProfile();
                                // 設定大頭照大小
                                Uri userPhoto = profile.getProfilePictureUri(300, 300);
                                /*Glide.with(LoginActivity.this)
                                        .load(userPhoto.toString())
                                        .crossFade()
                                        .into(mImgPhoto);
                                mTextDescription.setText(String.format(Locale.TAIWAN, "Name:%s\nE-mail:%s", name, email));*/
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                // https://developers.facebook.com/docs/android/graph?locale=zh_TW
                // 如果要取得email，需透過添加參數的方式來獲取(如下)
                // 不添加只能取得id & name
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
            }

            @Override
            public void onCancel() {
                // 用戶取消
                Log.d(TAG, "Facebook onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                // 登入失敗
                Log.d(TAG, "Facebook onError:" + error.toString());
            }
        });
    }






}
