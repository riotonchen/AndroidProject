package com.example.admin.project;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class HttpUtils {

    public static String[] SendGetRequest(String path, String colName) {
        ArrayList<String> arrayList1 = new ArrayList<>();ArrayList<String> arrayList2 = new ArrayList<>();ArrayList<String> arrayList3 = new ArrayList<>();ArrayList<String> arrayList4 = new ArrayList<>();
        HttpGet httpGet = new HttpGet(path);                           //創建一个GET方式的HttpRequest對象
        //DefaultHttpClient httpClient = new DefaultHttpClient();        //創建一个默認的HTTP客户端
        HttpClient httpClient = new DefaultHttpClient();
        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);               //執行GET方式的HTTP请求
            int responseCode = httpResponse.getStatusLine().getStatusCode();        //獲得伺服器的回應碼
            if (responseCode == HttpStatus.SC_OK) {
                String jsonStr = EntityUtils.toString(httpResponse.getEntity()); //注意編碼    // String details = new JSONObject(JsonText).getJSONArray("details").getJSONObject(1).get("invNum").toString()
                JSONArray myJSONArray=new JSONObject(jsonStr).getJSONArray("details");

                for(int i=0;i<myJSONArray.length();i++) {
                    arrayList1.add(new JSONObject(jsonStr).getJSONArray("details").getJSONObject(i).get(colName).toString());////發票明細URL需要的參數(發票號碼)
                    String sYear=(new JSONObject(jsonStr).getJSONArray("details").getJSONObject(i).getJSONObject("invDate").getString("year"));//發票明細URL需要的參數(發票日期-年)
                    int iYear =Integer.valueOf(sYear);
                    iYear=1911+iYear;//民國轉西元
                    sYear=Integer.toString(iYear);
                    arrayList2.add(sYear);
                    arrayList3.add((new JSONObject(jsonStr).getJSONArray("details").getJSONObject(i).getJSONObject("invDate").getString("month")));//發票明細URL需要的參數(發票日期-月)
                    arrayList4.add((new JSONObject(jsonStr).getJSONArray("details").getJSONObject(i).getJSONObject("invDate").getString("date")));//發票明細URL需要的參數(發票日期-日)
                }
                Log.i("GET_RESULT", String.valueOf(responseCode));     //輸出回傳狀態
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] invoice =arrayList1.toArray(new String[arrayList1.size()]);
        String[] year =arrayList2.toArray(new String[arrayList1.size()]);
        String[] month =arrayList3.toArray(new String[arrayList1.size()]);
        String[] date =arrayList4.toArray(new String[arrayList1.size()]);
        ArrayList<String> arrayList = new ArrayList<>();
        Long tsLong = System.currentTimeMillis()/1000+25;
        final String ts = tsLong.toString();
        for(int i=0;i<invoice.length;i++) {
            httpGet = new HttpGet("https://api.einvoice.nat.gov.tw/PB2CAPIVAN/invServ/InvServ?version=0.3&cardType=3J0002&cardNo=/1P685U2&expTimeStamp=" + ts + "&action=carrierInvDetail&timeStamp=" + ts + "&invNum="+invoice[i]+"&invDate="+year[i]+"/"+month[i]+"/"+date[i]+"&uuid=003&appID=EINV4201804318254&cardEncrypt=0130");
            try {
                HttpResponse httpResponse = httpClient.execute(httpGet);
                int responseCode = httpResponse.getStatusLine().getStatusCode();
                if (responseCode == HttpStatus.SC_OK) {
                    String jsonStr = EntityUtils.toString(httpResponse.getEntity());
                    arrayList.add("發票"+i);
                    arrayList.add("總金額: "+new JSONObject(jsonStr).getString("amount"));
                    arrayList.add("日期: "+year[i]+"/"+month[i]+"/"+date[i]);
                    arrayList.add("發票號碼: "+invoice[i]);
                    arrayList.add("商家: "+new JSONObject(jsonStr).getString("sellerName"));
                    arrayList.add("--------------------");
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return arrayList.toArray(new String[arrayList.size()]);//ArrayList轉成String[]






        /*ArrayList<String> arrayList = new ArrayList<>();
        HttpGet httpGet = new HttpGet(path);                           //創建一个GET方式的HttpRequest對象
        //DefaultHttpClient httpClient = new DefaultHttpClient();        //創建一个默認的HTTP客户端
        HttpClient httpClient = new DefaultHttpClient();
        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);               //執行GET方式的HTTP请求
            int responseCode = httpResponse.getStatusLine().getStatusCode();        //獲得伺服器的回應碼
            if (responseCode == HttpStatus.SC_OK) {
                String jsonStr = EntityUtils.toString(httpResponse.getEntity()); //注意編碼
                for (String elem : colName) {
                    arrayList.add(new JSONObject(jsonStr).getString(elem));

                }
                Log.i("GET_RESULT", String.valueOf(responseCode));     //輸出回傳狀態
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return arrayList.toArray(new String[arrayList.size()]);//ArrayList轉成String[]*/
    }

    public static String[] SendGetRequest2(String path, String colName) {//獲得發票號碼
        ArrayList<String> arrayList = new ArrayList<>();
        HttpGet httpGet = new HttpGet(path);                           //創建一个GET方式的HttpRequest對象
        //DefaultHttpClient httpClient = new DefaultHttpClient();        //創建一个默認的HTTP客户端
        HttpClient httpClient = new DefaultHttpClient();
        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);               //執行GET方式的HTTP请求
            int responseCode = httpResponse.getStatusLine().getStatusCode();        //獲得伺服器的回應碼
            if (responseCode == HttpStatus.SC_OK) {
                String jsonStr = EntityUtils.toString(httpResponse.getEntity()); //注意編碼    // String details = new JSONObject(JsonText).getJSONArray("details").getJSONObject(1).get("invNum").toString()
                JSONArray myJSONArray=new JSONObject(jsonStr).getJSONArray("details");

                for(int i=0;i<myJSONArray.length();i++) {
                    arrayList.add(new JSONObject(jsonStr).getJSONArray("details").getJSONObject(i).get(colName).toString());
                }

                Log.i("GET_RESULT", String.valueOf(responseCode));     //輸出回傳狀態
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return arrayList.toArray(new String[arrayList.size()]);//ArrayList轉成String[]
    }

    public static String[] SendGetRequestDate(String path, String colName) {
        ArrayList<String> arrayList1 = new ArrayList<>();ArrayList<String> arrayList2 = new ArrayList<>();ArrayList<String> arrayList3 = new ArrayList<>();ArrayList<String> arrayList4 = new ArrayList<>();
        HttpGet httpGet = new HttpGet(path);                           //創建一个GET方式的HttpRequest對象
        //DefaultHttpClient httpClient = new DefaultHttpClient();        //創建一个默認的HTTP客户端
        HttpClient httpClient = new DefaultHttpClient();
        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);               //執行GET方式的HTTP请求
            int responseCode = httpResponse.getStatusLine().getStatusCode();        //獲得伺服器的回應碼
            if (responseCode == HttpStatus.SC_OK) {
                String jsonStr = EntityUtils.toString(httpResponse.getEntity()); //注意編碼    // String details = new JSONObject(JsonText).getJSONArray("details").getJSONObject(1).get("invNum").toString()
                JSONArray myJSONArray=new JSONObject(jsonStr).getJSONArray("details");

                for(int i=0;i<myJSONArray.length();i++) {
                    arrayList1.add(new JSONObject(jsonStr).getJSONArray("details").getJSONObject(i).get(colName).toString());////發票明細URL需要的參數(發票號碼)
                    String sYear=(new JSONObject(jsonStr).getJSONArray("details").getJSONObject(i).getJSONObject("invDate").getString("year"));//發票明細URL需要的參數(發票日期-年)
                    int iYear =Integer.valueOf(sYear);
                    iYear=1911+iYear;//民國轉西元
                    sYear=Integer.toString(iYear);
                    arrayList2.add(sYear);
                    arrayList3.add((new JSONObject(jsonStr).getJSONArray("details").getJSONObject(i).getJSONObject("invDate").getString("month")));//發票明細URL需要的參數(發票日期-月)
                    arrayList4.add((new JSONObject(jsonStr).getJSONArray("details").getJSONObject(i).getJSONObject("invDate").getString("date")));//發票明細URL需要的參數(發票日期-日)
                }
                Log.i("GET_RESULT", String.valueOf(responseCode));     //輸出回傳狀態
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] invoice =arrayList1.toArray(new String[arrayList1.size()]);
        String[] year =arrayList2.toArray(new String[arrayList1.size()]);
        String[] month =arrayList3.toArray(new String[arrayList1.size()]);
        String[] date =arrayList4.toArray(new String[arrayList1.size()]);
        ArrayList<String> arrayList = new ArrayList<>();
        Long tsLong = System.currentTimeMillis()/1000+25;
        final String ts = tsLong.toString();
        for(int i=0;i<invoice.length;i++) {
            httpGet = new HttpGet("https://api.einvoice.nat.gov.tw/PB2CAPIVAN/invServ/InvServ?version=0.3&cardType=3J0002&cardNo=/1P685U2&expTimeStamp=" + ts + "&action=carrierInvDetail&timeStamp=" + ts + "&invNum="+invoice[i]+"&invDate="+year[i]+"/"+month[i]+"/"+date[i]+"&uuid=003&appID=EINV4201804318254&cardEncrypt=0130");
            try {
                HttpResponse httpResponse = httpClient.execute(httpGet);
                int responseCode = httpResponse.getStatusLine().getStatusCode();
                if (responseCode == HttpStatus.SC_OK) {
                    String jsonStr = EntityUtils.toString(httpResponse.getEntity());
                    arrayList.add(new JSONObject(jsonStr).getString("invDate"));
                }


            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return arrayList.toArray(new String[arrayList.size()]);//ArrayList轉成String[]
    }

    public static String[] SendGetRequestAmount(String path, String colName) {
        ArrayList<String> arrayList1 = new ArrayList<>();ArrayList<String> arrayList2 = new ArrayList<>();ArrayList<String> arrayList3 = new ArrayList<>();ArrayList<String> arrayList4 = new ArrayList<>();
        HttpGet httpGet = new HttpGet(path);                           //創建一个GET方式的HttpRequest對象
        //DefaultHttpClient httpClient = new DefaultHttpClient();        //創建一个默認的HTTP客户端
        HttpClient httpClient = new DefaultHttpClient();
        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);               //執行GET方式的HTTP请求
            int responseCode = httpResponse.getStatusLine().getStatusCode();        //獲得伺服器的回應碼
            if (responseCode == HttpStatus.SC_OK) {
                String jsonStr = EntityUtils.toString(httpResponse.getEntity()); //注意編碼    // String details = new JSONObject(JsonText).getJSONArray("details").getJSONObject(1).get("invNum").toString()
                JSONArray myJSONArray=new JSONObject(jsonStr).getJSONArray("details");

                for(int i=0;i<myJSONArray.length();i++) {
                    arrayList1.add(new JSONObject(jsonStr).getJSONArray("details").getJSONObject(i).get(colName).toString());////發票明細URL需要的參數(發票號碼)
                    String sYear=(new JSONObject(jsonStr).getJSONArray("details").getJSONObject(i).getJSONObject("invDate").getString("year"));//發票明細URL需要的參數(發票日期-年)
                    int iYear =Integer.valueOf(sYear);
                    iYear=1911+iYear;//民國轉西元
                    sYear=Integer.toString(iYear);
                    arrayList2.add(sYear);
                    arrayList3.add((new JSONObject(jsonStr).getJSONArray("details").getJSONObject(i).getJSONObject("invDate").getString("month")));//發票明細URL需要的參數(發票日期-月)
                    arrayList4.add((new JSONObject(jsonStr).getJSONArray("details").getJSONObject(i).getJSONObject("invDate").getString("date")));//發票明細URL需要的參數(發票日期-日)
                }
                Log.i("GET_RESULT", String.valueOf(responseCode));     //輸出回傳狀態
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] invoice =arrayList1.toArray(new String[arrayList1.size()]);
        String[] year =arrayList2.toArray(new String[arrayList1.size()]);
        String[] month =arrayList3.toArray(new String[arrayList1.size()]);
        String[] date =arrayList4.toArray(new String[arrayList1.size()]);
        ArrayList<String> arrayList = new ArrayList<>();
        Long tsLong = System.currentTimeMillis()/1000+25;
        final String ts = tsLong.toString();
        for(int i=0;i<invoice.length;i++) {
            httpGet = new HttpGet("https://api.einvoice.nat.gov.tw/PB2CAPIVAN/invServ/InvServ?version=0.3&cardType=3J0002&cardNo=/1P685U2&expTimeStamp=" + ts + "&action=carrierInvDetail&timeStamp=" + ts + "&invNum="+invoice[i]+"&invDate="+year[i]+"/"+month[i]+"/"+date[i]+"&uuid=003&appID=EINV4201804318254&cardEncrypt=0130");
            try {
                HttpResponse httpResponse = httpClient.execute(httpGet);
                int responseCode = httpResponse.getStatusLine().getStatusCode();
                if (responseCode == HttpStatus.SC_OK) {
                    String jsonStr = EntityUtils.toString(httpResponse.getEntity());
                    arrayList.add(new JSONObject(jsonStr).getString("amount"));
                }


            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return arrayList.toArray(new String[arrayList.size()]);//ArrayList轉成String[]
    }

    public static String[] SendGetRequestNumber(String path, String colName) {
        ArrayList<String> arrayList1 = new ArrayList<>();ArrayList<String> arrayList2 = new ArrayList<>();ArrayList<String> arrayList3 = new ArrayList<>();ArrayList<String> arrayList4 = new ArrayList<>();
        HttpGet httpGet = new HttpGet(path);                           //創建一个GET方式的HttpRequest對象
        //DefaultHttpClient httpClient = new DefaultHttpClient();        //創建一个默認的HTTP客户端
        HttpClient httpClient = new DefaultHttpClient();
        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);               //執行GET方式的HTTP请求
            int responseCode = httpResponse.getStatusLine().getStatusCode();        //獲得伺服器的回應碼
            if (responseCode == HttpStatus.SC_OK) {
                String jsonStr = EntityUtils.toString(httpResponse.getEntity()); //注意編碼    // String details = new JSONObject(JsonText).getJSONArray("details").getJSONObject(1).get("invNum").toString()
                JSONArray myJSONArray=new JSONObject(jsonStr).getJSONArray("details");

                for(int i=0;i<myJSONArray.length();i++) {
                    arrayList1.add(new JSONObject(jsonStr).getJSONArray("details").getJSONObject(i).get(colName).toString());////發票明細URL需要的參數(發票號碼)
                    String sYear=(new JSONObject(jsonStr).getJSONArray("details").getJSONObject(i).getJSONObject("invDate").getString("year"));//發票明細URL需要的參數(發票日期-年)
                    int iYear =Integer.valueOf(sYear);
                    iYear=1911+iYear;//民國轉西元
                    sYear=Integer.toString(iYear);
                    arrayList2.add(sYear);
                    arrayList3.add((new JSONObject(jsonStr).getJSONArray("details").getJSONObject(i).getJSONObject("invDate").getString("month")));//發票明細URL需要的參數(發票日期-月)
                    arrayList4.add((new JSONObject(jsonStr).getJSONArray("details").getJSONObject(i).getJSONObject("invDate").getString("date")));//發票明細URL需要的參數(發票日期-日)
                }
                Log.i("GET_RESULT", String.valueOf(responseCode));     //輸出回傳狀態
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] invoice =arrayList1.toArray(new String[arrayList1.size()]);
        String[] year =arrayList2.toArray(new String[arrayList1.size()]);
        String[] month =arrayList3.toArray(new String[arrayList1.size()]);
        String[] date =arrayList4.toArray(new String[arrayList1.size()]);
        ArrayList<String> arrayList = new ArrayList<>();
        Long tsLong = System.currentTimeMillis()/1000+25;
        final String ts = tsLong.toString();
        for(int i=0;i<invoice.length;i++) {
            httpGet = new HttpGet("https://api.einvoice.nat.gov.tw/PB2CAPIVAN/invServ/InvServ?version=0.3&cardType=3J0002&cardNo=/1P685U2&expTimeStamp=" + ts + "&action=carrierInvDetail&timeStamp=" + ts + "&invNum="+invoice[i]+"&invDate="+year[i]+"/"+month[i]+"/"+date[i]+"&uuid=003&appID=EINV4201804318254&cardEncrypt=0130");
            try {
                HttpResponse httpResponse = httpClient.execute(httpGet);
                int responseCode = httpResponse.getStatusLine().getStatusCode();
                if (responseCode == HttpStatus.SC_OK) {
                    String jsonStr = EntityUtils.toString(httpResponse.getEntity());
                    arrayList.add(new JSONObject(jsonStr).getString("invNum"));
                }


            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return arrayList.toArray(new String[arrayList.size()]);//ArrayList轉成String[]
    }
}
