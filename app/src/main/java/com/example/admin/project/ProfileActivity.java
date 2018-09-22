package com.example.admin.project;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private Button btnReturn2, btnUpdate;
    private EditText txtAccount, txtToid, txtName, txtNewPwd, txtNewPwdConfirm;
    private static String account, toid, name;
    private TextView txvNewPwd,txvNewPwdConfirm;

    private final ProfileUpdateHandler profileUpdateHandler = new ProfileUpdateHandler(ProfileActivity.this);

    private class MyMessages {
        public static final int Error = 0;
        public static final int Connecting = 1;
        public static final int Progressing = 2;
        public static final int Disconnect = 3;
    }

    private static SharedPreferences myGetSharedPreferences(Context context) {
        return context.getSharedPreferences("jwt_token", MODE_PRIVATE);
    }

    private static class ProfileUpdateHandler extends Handler {
        private final WeakReference<ProfileActivity> mActivity;    //弱引用

        private ProfileUpdateHandler(ProfileActivity activity) {
            mActivity = new WeakReference<ProfileActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            ProfileActivity activity = mActivity.get();    //獲取弱引用的對象
            if (activity != null) {
                try {
                    switch (msg.what) {
                        case MyMessages.Progressing:
                            Bundle bundle = msg.getData();
                            JSONObject jsonObject = new JSONObject(bundle.getString("patch_jsonString"));
                            int responseCode = jsonObject.getInt("responseCode");
                            String strMsg;
                            if (responseCode == 204) {
                                strMsg = "更新成功！";
                                SharedPreferences myPref = myGetSharedPreferences(activity.getApplicationContext());    //靜態方法
                                String strPayload = myPref.getString("PAYLOAD", "");//讀取已儲存的SharedPref
                                JSONObject jsonPayload = NewMainFragment.StringToJSON(strPayload);  //轉成JSON
                                toid = activity.txtToid.getText().toString();   //覆寫
                                name = activity.txtName.getText().toString();
                                jsonPayload.put("toid", toid);   //覆寫
                                jsonPayload.put("name", name);
                                SharedPreferences.Editor editor = myPref.edit();
                                editor.putString("PAYLOAD", jsonPayload.toString());    //存入
                                editor.apply();
                            } else {
                                strMsg = "更新失敗！\n" + jsonObject.toString();
                            }

                            Toast.makeText(activity, strMsg, Toast.LENGTH_LONG).show();
                            break;
                        case MyMessages.Error:
                            Toast.makeText(activity, "更新失敗！ToId已被使用，請重新輸入！", Toast.LENGTH_LONG).show();

                            break;
                        case MyMessages.Disconnect:
                            String strNewPwd = activity.txtNewPwd.getText().toString();
                            String strNewPwdConfirm = activity.txtNewPwdConfirm.getText().toString();
                            Boolean isPwdConfirm = strNewPwd.equals(strNewPwdConfirm);
                            //密碼有變更就自動登出
                            if (isPwdConfirm && !strNewPwd.isEmpty()) {
                                SharedPreferences.Editor editor = myGetSharedPreferences(activity.getApplicationContext()).edit();
                                editor.clear(); //清除Token
                                editor.apply();
                                Toast.makeText(activity, "變更成功，請重新登入！", Toast.LENGTH_LONG).show();
                            }
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
        setContentView(R.layout.activity_profile);
        btnReturn2 = findViewById(R.id.btnReturn2);
        btnUpdate = findViewById(R.id.btnUpdate);
        txtAccount = findViewById(R.id.txtAccount);
        txtToid = findViewById(R.id.txtToid);
        txtName = findViewById(R.id.txtName);
        txvNewPwd = findViewById(R.id.txvNewPwd);
        txvNewPwdConfirm = findViewById(R.id.txvNewPwdConfirm);
        txtNewPwd = findViewById(R.id.txtNewPwd);
        txtNewPwdConfirm = findViewById(R.id.txtNewPwdConfirm);

        try {
            //帶入資料
            SharedPreferences myPref = getSharedPreferences("jwt_token", MODE_PRIVATE);
            String strPayload = myPref.getString("PAYLOAD", "");//讀取已儲存的SharedPref
            JSONObject jsonPayload = NewMainFragment.StringToJSON(strPayload);  //轉成JSON
            account = jsonPayload.getString("username");
            toid = jsonPayload.getString("toid");
            name = jsonPayload.getString("name");

            txtAccount.setText(account.substring(0,account.length()-2));   //去除分號之後
            txtToid.setText(toid);
            txtName.setText(name);
            String temp = account.substring(account.length()-1,account.length());
            if( !account.substring(account.length()-1,account.length()).equals("2")){
                txvNewPwd.setVisibility(View.INVISIBLE);
                txvNewPwdConfirm.setVisibility(View.INVISIBLE);
                txtNewPwd.setVisibility(View.INVISIBLE);
                txtNewPwdConfirm.setVisibility(View.INVISIBLE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        //返回
        btnReturn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //更新
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //先檢查有無修改
                final String strToid = txtToid.getText().toString().toUpperCase();
                final String strName = txtName.getText().toString();
                final String strNewPwd = txtNewPwd.getText().toString();
                final String strNewPwdConfirm = txtNewPwdConfirm.getText().toString();
                final Boolean isPwdConfirm = strNewPwd.equals(strNewPwdConfirm);

                if(txtToid.getText().toString().length()<8) {
                    Toast.makeText(getBaseContext(), "ToId必須為8碼，請重新輸入！", Toast.LENGTH_LONG).show();
                }else if(strToid.equals(toid)&&strName.equals(name)&&strNewPwd.isEmpty()&&strNewPwdConfirm.isEmpty()){//未修改
                    Toast.makeText(getBaseContext(), "資料未變更，不須更新", Toast.LENGTH_LONG).show();
                }else if(!isPwdConfirm){
                    Toast.makeText(getBaseContext(), "兩次密碼不一致，請重新輸入！", Toast.LENGTH_LONG).show();
                }else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                SharedPreferences myPref = getSharedPreferences("jwt_token", MODE_PRIVATE);
                                String token = myPref.getString("token", "");//讀取已儲存的Token
                                String strPayload = myPref.getString("PAYLOAD", "");//讀取已儲存的SharedPref
                                JSONObject jsonPayload = NewMainFragment.StringToJSON(strPayload);  //轉成JSON
                                Map<String, String> params = new HashMap<>();
                                params.put("toid", strToid);
                                params.put("name", strName);
                                if(isPwdConfirm&&!strNewPwd.isEmpty())
                                    params.put("password",strNewPwd);
                                Message message = new Message();
                                Bundle bundle = new Bundle();

                                message.what = ProfileActivity.MyMessages.Progressing;
                                String path = LoginActivity.Path.member + jsonPayload.getString("user_id") + "/";
                                JSONObject jsonObject = HttpUtils.Patch(path, token, params);
                                bundle.putString("patch_jsonString", jsonObject.toString());    //轉成String
                                message.setData(bundle);
                                int responseCode = jsonObject.getInt("responseCode");
                                if(responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {//失敗的話
                                    profileUpdateHandler.sendEmptyMessage(MyMessages.Error);
                                }
                                else {
                                    if(isPwdConfirm&&!strNewPwd.isEmpty()){ //密碼有變更，跳轉到登入畫面
                                        Intent it = new Intent(ProfileActivity.this, LoginActivity.class);
                                        startActivity(it);

                                    }else{//成功的話跳轉至首頁
                                        Intent it = new Intent(ProfileActivity.this, NewMainActivity.class);
                                        it.putExtra("username", strName);
                                        startActivity(it);
                                    }
                                    profileUpdateHandler.sendMessage(message);

                                }

                                profileUpdateHandler.sendEmptyMessage(MyMessages.Disconnect);

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
        profileUpdateHandler.removeCallbacksAndMessages(null);

        super.onDestroy();
    }
}
