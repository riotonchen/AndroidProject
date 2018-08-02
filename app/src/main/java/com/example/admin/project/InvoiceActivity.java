package com.example.admin.project;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.net.HttpURLConnection;

public class InvoiceActivity extends AppCompatActivity {

    private Button btnGet,btnClear,btnGet2,btnGet3;
    private TextView txvShow;
    private String showMsg = "";
    private SQLiteDatabase db;
    private final String TABLE_NAME_ACCOUNTING="mbr_accounting";
    private boolean ifSearch;

    public class MyMessages {
        public static final int Connecting = 0;
        public static final int Post_Progressing = 1;
        public static final int Get_Progressing = 2;
        public static final int Disconnect = 3;
    }

    public class MainHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    case MyMessages.Connecting:
                        showMsg = "[Connecting]\n";
                        break;
                    case MyMessages.Post_Progressing:
                        showMsg = "[Post_Progressing]\n--------------------\n";
                        Bundle postData = msg.getData();
                        String postReturnValue = postData.getString("PostReturnValue");
                        showMsg += postReturnValue + "\n";
                        break;
                    case MyMessages.Get_Progressing:
                        showMsg = "[Get_Progressing]\n--------------------\n";
                        Bundle getData = msg.getData();
                        String[] getReturnValue = getData.getStringArray("GetReturnValue");
                        for (String elem : getReturnValue) {
                            showMsg += elem + "\n";
                        }
                        break;
                    case MyMessages.Disconnect:
                        showMsg = "--------------------\n[Disconnect]\n";
                        break;
                    default:
                        break;
                }
                txvShow.append(showMsg);
                //super.handleMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);
        btnGet =(Button) findViewById(R.id.btnGet);
        btnClear=(Button)findViewById(R.id.btnClear);
        txvShow=(TextView) findViewById(R.id.txvShow);
        btnGet2=(Button)findViewById(R.id.btnGet2);
        btnGet3=(Button)findViewById(R.id.btnGet3);
        ifSearch=false;

        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MainHandler mainHandler = new MainHandler();
                Long tsLong = System.currentTimeMillis()/1000+10;
                final String ts = tsLong.toString();
                ifSearch=true;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mainHandler.sendEmptyMessage(MyMessages.Connecting);

                            //String path = "https://api.einvoice.nat.gov.tw//PB2CAPIVAN/invapp/InvApp?version=&action=QryWinningList&invTerm=10702&appID=EINV4201804318254&UUID=/";
                            //String[] colName = {"invoYm","firstPrizeNo1"};
                            String path="https://api.einvoice.nat.gov.tw/PB2CAPIVAN/invServ/InvServ?version=0.3&cardType=3J0002&cardNo=/1P685U2&expTimeStamp="+ts+"&action=carrierInvChk&timeStamp="+ts+"&startDate=2018/01/01&endDate=2018/01/31&onlyWinningInv=N&uuid=002&appID=EINV4201804318254&cardEncrypt=0130";
                            String colName = "invNum";
                            Message message = new Message();
                            message.what = MyMessages.Get_Progressing;
                            Bundle bundle = new Bundle();
                            bundle.putStringArray("GetReturnValue", HttpUtils.SendGetRequest(path, colName));
                            message.setData(bundle);
                            mainHandler.sendMessage(message);
                            mainHandler.sendEmptyMessage(MyMessages.Disconnect);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ifSearch=false;
                txvShow.setText("");
            }
        });


        //按下mGet按鈕進行HttpGet串接傳輸
        btnGet2.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View arg0)
            {
                final MainHandler mainHandler = new MainHandler();
                Long tsLong = System.currentTimeMillis()/1000+25;
                final String ts = tsLong.toString();
                ifSearch=true;

                //建立多執行緒進行網路Server API串接的資料傳輸與讀取
                new Thread(new Runnable()
                {
                    public void run()
                    {
                        try
                        {
                            mainHandler.sendEmptyMessage(MyMessages.Connecting);
                            //API串接的uri路徑
                            //String uri1 ="https://api.einvoice.nat.gov.tw//PB2CAPIVAN/invapp/InvApp?version=&action=QryWinningList&invTerm=10702&appID=EINV4201804318254&UUID=/";
                            String path="https://api.einvoice.nat.gov.tw/PB2CAPIVAN/invServ/InvServ?version=0.3&cardType=3J0002&cardNo=/1P685U2&expTimeStamp="+ts+"&action=carrierInvChk&timeStamp="+ts+"&startDate=2018/01/01&endDate=2018/01/31&onlyWinningInv=N&uuid=002&appID=EINV4201804318254&cardEncrypt=0130";
                            String colName = "invNum";
                            Message message = new Message();
                            message.what = MyMessages.Get_Progressing;
                            Bundle bundle = new Bundle();
                            bundle.putStringArray("GetReturnValue", HttpUtils.SendGetRequest2(path, colName));
                            message.setData(bundle);
                            mainHandler.sendMessage(message);

                            mainHandler.sendEmptyMessage(MyMessages.Disconnect);

                            /*HttpClient mHttpClient = new DefaultHttpClient();
                            HttpGet mHttpGet = new HttpGet(uri1);
                            HttpResponse mHttpResponse = mHttpClient.execute(mHttpGet);
                            String mJsonText = EntityUtils.toString(mHttpResponse.getEntity());
                            String mdetails = new JSONObject(mJsonText).getJSONArray("details").toString();
                            String invoice="";*/

                            /*
                            if(mHttpResponse.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK ){
                                String JsonText = EntityUtils.toString(mHttpResponse.getEntity());
                                String details = new JSONObject(JsonText).getJSONArray("details").getJSONObject(1).get("invNum").toString();
                                txvShow.append(details);
                                Log.i("Work2", "Success2");
                            }
                            */

                            //HttpURLConnection.HTTP_OK為200，200代表串接溝通成功
                            /*if(mHttpResponse.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK )
                            {
                                //API的回傳文字，格式為json格式
                                String mJsonText = EntityUtils.toString(mHttpResponse.getEntity());
                                String superPrizeNo =new JSONObject(mJsonText).getString("superPrizeNo");
                                String spcPrizeNo =new JSONObject(mJsonText).getString("spcPrizeNo");
                                String invoYm =new JSONObject(mJsonText).getString("invoYm");
                                String firstPrizeNo1 =new JSONObject(mJsonText).getString("firstPrizeNo1");
                                String firstPrizeNo2 =new JSONObject(mJsonText).getString("firstPrizeNo2");
                                String firstPrizeNo3 =new JSONObject(mJsonText).getString("firstPrizeNo3");
                                String sixthPrizeNo1 =new JSONObject(mJsonText).getString("sixthPrizeNo1");
                                String sixthPrizeNo2 =new JSONObject(mJsonText).getString("sixthPrizeNo2");
                                String sixthPrizeNo3 =new JSONObject(mJsonText).getString("sixthPrizeNo3");
                                String superPrizeAmt =new JSONObject(mJsonText).getString("superPrizeAmt");
                                String spcPrizeAmt =new JSONObject(mJsonText).getString("spcPrizeAmt");
                                String firstPrizeAmt =new JSONObject(mJsonText).getString("firstPrizeAmt");
                                String secondPrizeAmt =new JSONObject(mJsonText).getString("secondPrizeAmt");
                                String thirdPrizeAmt =new JSONObject(mJsonText).getString("thirdPrizeAmt");
                                String fourthPrizeAmt =new JSONObject(mJsonText).getString("fourthPrizeAmt");
                                String fifthPrizeAmt =new JSONObject(mJsonText).getString("fifthPrizeAmt");
                                String sixthPrizeAmt =new JSONObject(mJsonText).getString("sixthPrizeAmt");
                                Log.i("Work1","Success1");
                                txvShow.append("Timestamp"+ts+"\n"+"開獎期別："+invoYm+"\n"+"千萬特獎號碼："+superPrizeNo+"\n"+"特獎號碼："+spcPrizeNo+"\n"+"頭獎號碼1："+firstPrizeNo1+"\n"+"頭獎號碼2："+
                                        firstPrizeNo2+"\n"+"頭獎號碼3："+firstPrizeNo3+"\n"+"六獎號碼1："+sixthPrizeNo1+"\n"+"六獎號碼2："+sixthPrizeNo2+"\n"+"六獎號碼3："+sixthPrizeNo3+"\n");
                            }*/
                        }
                        catch(Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }});

        btnGet3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ifSearch == true) {
                    ifSearch=false;
                    final MainHandler mainHandler = new MainHandler();
                    Long tsLong = System.currentTimeMillis() / 1000 + 25;
                    final String ts = tsLong.toString();

                    //建立多執行緒進行網路Server API串接的資料傳輸與讀取
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                //mainHandler.sendEmptyMessage(MyMessages.Connecting);
                                //API串接的uri路徑
                                //String uri1 ="https://api.einvoice.nat.gov.tw//PB2CAPIVAN/invapp/InvApp?version=&action=QryWinningList&invTerm=10702&appID=EINV4201804318254&UUID=/";
                                String path = "https://api.einvoice.nat.gov.tw/PB2CAPIVAN/invServ/InvServ?version=0.3&cardType=3J0002&cardNo=/1P685U2&expTimeStamp=" + ts + "&action=carrierInvChk&timeStamp=" + ts + "&startDate=2018/01/01&endDate=2018/01/31&onlyWinningInv=N&uuid=002&appID=EINV4201804318254&cardEncrypt=0130";
                                String colName = "invNum";
                                //Message message = new Message();
                                //message.what = MyMessages.Get_Progressing;
                                //Bundle bundle = new Bundle();
                                //bundle.putStringArray("GetReturnValue", HttpUtils.SendGetRequest3(path, colName));
                                //message.setData(bundle);
                                //mainHandler.sendMessage(message);
                                //mainHandler.sendEmptyMessage(MyMessages.Disconnect);
                                String[] Date = HttpUtils.SendGetRequestDate(path, colName);//發票的日期
                                String[] Amount = HttpUtils.SendGetRequestAmount(path, colName);//發票的金額
                                String[] Number = HttpUtils.SendGetRequestNumber(path, colName);//發票的號碼
                                long result = 0;
                                int haveData=1;
                                String dbInvoice = "";
                                for (int i = 0; i < Number.length; i++) {//帶入的發票號碼
                                    DBHelper DH = new DBHelper(InvoiceActivity.this);
                                    db = DH.getReadableDatabase();
                                    ContentValues cv = new ContentValues();

                                    Cursor cur = db.rawQuery("SELECT invoiceNum FROM mbr_accounting", null);
                                    Log.i("InProgress","READY");
                                    int rowsCount = cur.getCount();
                                    if (rowsCount != 0) {
                                        Log.i("InProgress","OK");
                                        cur.moveToFirst();
                                        for (int j = 0; j < rowsCount; j++) {//帳務資料表內所有的發票號碼
                                            dbInvoice = cur.getString(0);
                                            Log.i("InProgress","COMPARISON");
                                            if(Number[i].equals(dbInvoice)){
                                                Log.i("InProgress","HAVEDATA");
                                                haveData=0;
                                                break;
                                            }
                                            cur.moveToNext();
                                        }
                                    }


                                    if(haveData==1) {
                                        Log.i("InProgress","DONTHAVEDATA");
                                        cv.put("memberID", "1");
                                        cv.put("time", (Date[i].substring(0, 4) + "/" + Date[i].substring(4, 6) + "/" + Date[i].substring(6)).replace('/', '-'));
                                        cv.put("type", "0");//0為支出
                                        cv.put("sortID", "13");//要先找出代號
                                        cv.put("subsortID", "49");
                                        cv.put("amount", Amount[i]);
                                        cv.put("accountID", "1");
                                        cv.put("projectID", "1");
                                        cv.put("invoiceNum", Number[i]);
                                        cv.putNull("comment");
                                        result = db.insert(TABLE_NAME_ACCOUNTING, null, cv);
                                        UpdateAccountBalance();//更新會員帳戶剩餘金額
                                    }
                                }
                                if (result == -1) {
                                    db.close();
                                    Looper.prepare();
                                    Toast.makeText(InvoiceActivity.this, "新增失敗", Toast.LENGTH_LONG).show();
                                    Looper.loop();
                                } else {
                                    db.close();
                                    Looper.prepare();
                                    Toast.makeText(InvoiceActivity.this, "新增完畢", Toast.LENGTH_LONG).show();
                                    Looper.loop();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }).start();
                    /*Intent intent = new Intent(InvoiceActivity.this, MainActivity.class);
                    intent.putExtra("id",1);
                    startActivity(intent);*/
                }
                else {
                    Toast.makeText(InvoiceActivity.this,"請先查詢或重新查詢", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

        private void UpdateAccountBalance(){
            String sqlCmd="UPDATE mbr_memberaccount SET balance = balance- (SELECT amount FROM mbr_accounting WHERE memberID=1  " +
                    "        AND type=0 ORDER BY _id DESC LIMIT 1) " +
                    "  WHERE mbr_memberaccount.accountID=(SELECT accountID FROM mbr_accounting WHERE memberID=1  " +
                    "        AND type=0 ORDER BY _id DESC LIMIT 1)";
            db.execSQL(sqlCmd);
        }
}

