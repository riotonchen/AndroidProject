package com.example.admin.project;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class SearchFriendActivity extends AppCompatActivity {

    private final GetHandler getHandler = new GetHandler(SearchFriendActivity.this);
    private final SearchFriendHandler searchFriendHandler = new SearchFriendHandler(SearchFriendActivity.this);
    private static String all;
    private static String showMsg = "\n";
    private Button btnSearchFriend,add;
    private TextView txvRecord,textView24;
    private EditText txtToid;
    private RadioGroup rGroup;
    private ImageView userimage;

    private class MyMessages {
        public static final int Error = 0;
        public static final int Connecting = 1;
        public static final int Progressing = 2;
        public static final int Disconnect = 3;
    }

    private static class GetHandler extends Handler {
        private final WeakReference<SearchFriendActivity> mActivity;    //弱引用

        private GetHandler(SearchFriendActivity activity) {
            mActivity = new WeakReference<SearchFriendActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            SearchFriendActivity activity = mActivity.get();    //獲取弱引用的對象
            if (activity != null) {
                try {
                    switch (msg.what) {
                        case SearchFriendActivity.MyMessages.Connecting:
                            showMsg = "\n[Connecting]>>>";
                            break;
                        case SearchFriendActivity.MyMessages.Progressing:
                            showMsg = "[Progressing]>>>";
                            Bundle bundle = msg.getData();
                            JSONArray jsonArray = new JSONArray(bundle.getString("get_jsonArrayString"));
                            JSONObject jsonObject = jsonArray.getJSONObject(jsonArray.length() - 1);    //最後一個為responseCode
                            showMsg +=
                                    "\n[ResponseCode]：" + String.valueOf(jsonObject.getInt("responseCode")) +
                                            "\n" + jsonArray.toString();  //這邊直接輸出字串，未處理

                            Toast.makeText(activity, "讀取成功！", Toast.LENGTH_SHORT).show();
                            break;
                        case SearchFriendActivity.MyMessages.Disconnect:
                            showMsg = "\n[Disconnect]\n";
                            break;
                        case SearchFriendActivity.MyMessages.Error:
                            bundle = msg.getData();
                            String errorMsg = bundle.getString("errorMsg", "");
                            Toast.makeText(activity, errorMsg, Toast.LENGTH_LONG).show();
                            break;
                        default:
                            break;
                    }
                    activity.txvRecord.append(showMsg);
                    //super.handleMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }



    private static class SearchFriendHandler extends Handler {
        private final WeakReference<SearchFriendActivity> mActivity;    //弱引用

        private SearchFriendHandler(SearchFriendActivity activity) {
            mActivity = new WeakReference<SearchFriendActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            SearchFriendActivity activity = mActivity.get();    //獲取弱引用的對象
            if (activity != null) {
                try {
                    switch (msg.what) {
                        case SearchFriendActivity.MyMessages.Connecting:
                            //showMsg = "\n[Connecting]>>>";
                            break;
                        case SearchFriendActivity.MyMessages.Progressing:
                            //showMsg = "[Progressing]>>>";
                            Bundle bundle = msg.getData();
                            JSONArray jsonArray = new JSONArray(bundle.getString("get_jsonArrayString"));
                            JSONObject jsonObject = jsonArray.getJSONObject(jsonArray.length() - 1);    //最後一個為responseCode
                            //showMsg +="\n[ResponseCode]：" + String.valueOf(jsonObject.getInt("responseCode")) + "\n";
                            if(jsonArray.length()==1){//只有回應碼
                                showMsg = "查無此人";
                                activity.txvRecord.setText("此ID不存在!");
                            }else {
                                for (int i = 0; i < jsonArray.length()-1; i++) {  //取出資料，注意位置(最後一個是responseCode)，注意null
                                    jsonObject = jsonArray.getJSONObject(i);
                                    String id = jsonObject.getString("id");
                                    String name = jsonObject.getString("name");
                                    String localpicture = jsonObject.getString("localpicture");
                                    String dbpicture = jsonObject.getString("dbpicture");
                                    showMsg += "{id:" + id + ", name:" + name +
                                            ", localpicture:" + localpicture + ", dbpicture:" + dbpicture + "},\n";
                                    activity.txvRecord.setText(name);
                                    activity.rGroup.setVisibility(View.INVISIBLE);
                                    activity.btnSearchFriend.setVisibility(View.INVISIBLE);
                                    activity.txtToid.setVisibility(View.INVISIBLE);
                                    activity.add.setVisibility(View.VISIBLE);
                                    activity.userimage.setVisibility(View.VISIBLE);
                                    activity.textView24.setText("加入好友");


                                }

                            }
                            Toast.makeText(activity, "查詢成功！", Toast.LENGTH_SHORT).show();
                            break;
                        case SearchFriendActivity.MyMessages.Disconnect:
                            showMsg = "\n[Disconnect]\n";
                            break;
                        case SearchFriendActivity.MyMessages.Error:
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
        setContentView(R.layout.activity_search_friend);
        // This will get the radiogroup
        rGroup = (RadioGroup)findViewById(R.id.radioGroup1);
        // This will get the radiobutton in the radiogroup that is checked
        //RadioButton checkedRadioButton = (RadioButton)rGroup.findViewById(rGroup.getCheckedRadioButtonId());
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);   //進入含有EditText的Activity時，不自動彈出虛擬鍵盤
        btnSearchFriend = findViewById(R.id.btnSearchFriend);
        txtToid = findViewById(R.id.txtToid);
        txvRecord = findViewById(R.id.txvRecord);
        add = findViewById(R.id.add);
        userimage = findViewById(R.id.userimage);
        textView24 = findViewById(R.id.textView24);

        rGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                // This will get the radiobutton that has changed in its check state
                RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
                // This puts the value (true/false) into the variable
                boolean isChecked = checkedRadioButton.isChecked();
                // If the radiobutton that has changed in check state is now checked...
                if (isChecked)
                {
                    if("行動條碼".equals(checkedRadioButton.getText().toString())){
                        IntentIntegrator integrator = new IntentIntegrator(SearchFriendActivity.this);
                        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                        integrator.setPrompt("請掃描QR-Code!!");
                        integrator.setCameraId(0);
                        integrator.setBeepEnabled(false);
                        integrator.setBarcodeImageEnabled(false);
                        integrator.setOrientationLocked(false);
                        integrator.initiateScan();
                    }

                }
            }
        });

        //搜尋好友
        btnSearchFriend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            searchFriendHandler.sendEmptyMessage(SearchFriendActivity.MyMessages.Connecting);

                            SharedPreferences myPref = getSharedPreferences("jwt_token", MODE_PRIVATE);
                            String token = myPref.getString("token", "");//讀取已儲存的Token
                            Message message = new Message();
                            Bundle bundle = new Bundle();
                            if (token.equals("")) {
                                message.what = SearchFriendActivity.MyMessages.Error;
                                bundle.putString("errorMsg", "尚未登入，請先登入！");
                                message.setData(bundle);
                                searchFriendHandler.sendMessage(message);
                            } else {
                                message.what = SearchFriendActivity.MyMessages.Progressing;
                                //帶入查詢參數
                                String toid = txtToid.getText().toString().toUpperCase();   //大寫
                                String path = LoginActivity.Path.member + "?toid=" + toid ;
                                JSONArray jsonArray = HttpUtils.Get(path, token);
                                bundle.putString("get_jsonArrayString", jsonArray.toString());    //轉成String
                                message.setData(bundle);
                                searchFriendHandler.sendMessage(message);
                            }

                            searchFriendHandler.sendEmptyMessage(SearchFriendActivity.MyMessages.Disconnect);
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
        getHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //掃描
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "You cancelled the scanning", Toast.LENGTH_SHORT).show();
            } else {
                all = result.getContents();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            searchFriendHandler.sendEmptyMessage(SearchFriendActivity.MyMessages.Connecting);

                            SharedPreferences myPref = getSharedPreferences("jwt_token", MODE_PRIVATE);
                            String token = myPref.getString("token", "");//讀取已儲存的Token
                            Message message = new Message();
                            Bundle bundle = new Bundle();
                            if (token.equals("")) {
                                message.what = SearchFriendActivity.MyMessages.Error;
                                bundle.putString("errorMsg", "尚未登入，請先登入！");
                                message.setData(bundle);
                                searchFriendHandler.sendMessage(message);
                            } else {
                                message.what = SearchFriendActivity.MyMessages.Progressing;
                                //帶入查詢參數
                                String toid = all.toUpperCase();   //大寫
                                String path = LoginActivity.Path.member + "?toid=" + toid ;
                                JSONArray jsonArray = HttpUtils.Get(path, token);
                                bundle.putString("get_jsonArrayString", jsonArray.toString());    //轉成String
                                message.setData(bundle);
                                searchFriendHandler.sendMessage(message);
                            }

                            searchFriendHandler.sendEmptyMessage(SearchFriendActivity.MyMessages.Disconnect);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    public void OnClick(View view){
        finish();
    }

}
